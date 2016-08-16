package com.lwy.myselect.session;

import com.lwy.myselect.executor.Executor;
import com.lwy.myselect.executor.StandardExecutor;
import com.lwy.myselect.mapper.Configuration;
import com.lwy.myselect.mapper.EntityMapper;
import com.lwy.myselect.reflection.Reflection;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public final class SimpleSession extends BaseSession{

	private boolean current = false;
	private boolean transaction = false;
	private List<Object> batchObjectList = null;    //批处理对象
	private String singleBatchSql = null;           //单条sql批处理
	private List<String> batchSqlList = null;       //批处理sql
	private Class<?> clazz;
	private Connection connection;
	private Configuration configuration;
	private SessionFactory sessionFactory;
	private boolean closed = false;
	private boolean autoCommit = true;             //自动提交
	private Executor executor;

	private void setClazz(Class<?> clazz){
		this.clazz = clazz; 
	}
	
	private SimpleSession(){}

	private void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	private void setConfiguration(Configuration configuration){
		this.configuration = configuration;
	}

	protected void setConnection(Connection connection) {
		this.connection = connection;
	}

	private void setCurrent(boolean current){
		this.current = current;
	}

	private void setExecutor(Executor executor){
		this.executor = executor;
	}

	private void setAutoCommit(boolean autoCommit){
		this.autoCommit = autoCommit;
	}

	@Override
	public Class<?> getClazz() {
		return clazz;
	}

	@Override
	protected Connection getConnection() {
		return connection;
	}

	@Override
	public boolean isCurrent() {
		return current;
	}

	@Override
	public void openTransaction(){
		batchObjectList = new ArrayList<>();
		batchSqlList = new ArrayList<>();
		transaction = true;
	}

	@Override
	public boolean isTransaction(){
		return transaction;
	}

	protected void open(){
		closed = false;
	}

	@Override
	public void close(){
		if(!closed){
			try {
				if(connection != null)
					connection.close();
				connection = null;
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			closed = true;
		}
	}

	@Override
	public int commit(){
//		if(isTransaction())
//			return insertAndDeleteAndUpdate(singleBatchSql,batchObjectList);
		return 0;
	}
	
	private void resetBatch(){
		batchObjectList.clear();
		batchObjectList = null;
		batchSqlList.clear();
		batchSqlList = null;
		singleBatchSql = null;
	}
	
	public void addBatch(){
		
	}


	/**
	 * put in cache after insert
	 * @param object object inserted
     */
	private void putInCache(Object object){
		String key = configuration.getEntity(clazz.getName()).getKey();
		Method[] methods = clazz.getDeclaredMethods();
		for(Method m:methods){
			if(("get"+key).equalsIgnoreCase(m.getName())){
				try {
					Object id = m.invoke(object);
					sessionFactory.cache(clazz.getName(),id,object);
				} catch (IllegalAccessException | InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public int insert(String sql,Object object){
		int result = update(sql,object);
		if(result > 0){
			putInCache(object);
		}
		return result;
	}


	@Override
	public int delete(String sql,Object object){
		return update(sql,object);
	}

	@Override
	public int update(String sql,Object object){
		try {
			return executeUpdate(sql,object);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
		}
		return -1;
	}

	private int executeUpdate(String sql,Object object) throws SQLException {
		EntityMapper entityMapper = configuration.getEntity(clazz.getName());
		String sqlStatement = entityMapper.getSQLMapper(sql).getSql();
		List<Object> propertyList = Reflection.reflectToGetProperty(sqlStatement, object, clazz,entityMapper);
		return executor.update(sqlStatement,propertyList);
	}

	private Object executeQuery(String sql,Object object) throws SQLException {
		EntityMapper entityMapper = configuration.getEntity(clazz.getName());
		String sqlStatement = entityMapper.getSQLMapper(sql).getSql();
		if(!sqlStatement.contains("count(*)")){
			Object result = findInCache(object,entityMapper);
			if(result != null) {
				System.out.println("get in cache");
				return result;
			}
		}
		String returnAlias = entityMapper.getSQLMapper(sql).getReturnAlias();               //返回对象的别名
		String className = configuration.getClassName(returnAlias);                         //返回对象的类名
		EntityMapper returnEntityMapper = configuration.getEntity(className);               //返回对象的映射
		List<Object> propertyList = Reflection.reflectToGetProperty(sqlStatement, object, clazz,entityMapper);//属性值，插入sql的问号中
		return executor.query(sqlStatement,propertyList,returnEntityMapper);
	}

	private int executeTransaction(){
		return 0;
	}


	/**
	 * find in cache
	 * @param object 实体
	 * @param mapper mapper
     * @return list contains object in cache
     */
	private Object findInCache(Object object, EntityMapper mapper){
//		System.out.println("search in cache");
		String key = mapper.getKey();
		Class<?> clazz = null;
		try {
			clazz = Class.forName(mapper.getClassName());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		if(clazz != null){
			Method[] methods = clazz.getDeclaredMethods();
			Object t = null;
			for(Method m:methods){
				if(("get"+key).equalsIgnoreCase(m.getName())){
					try {
						t = m.invoke(object);
					} catch (IllegalAccessException | InvocationTargetException e) {
						e.printStackTrace();
					}
				}
			}
			List<Object> list = new ArrayList<>();
			Object result = sessionFactory.getCacheManager().find(mapper.getClassName(),t);
			if(result != null) {
				list.add(result);
				return list;
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object select(String sql,Object object){
		try {
			EntityMapper entityMapper = configuration.getEntity(clazz.getName());
			Object cached = findInCache(object,entityMapper);
			if(cached != null) {
				System.out.println("get in cache");
				return cached;
			}
			Object result = executeQuery(sql,object);
			String returnAlias = entityMapper.getSQLMapper(sql).getReturnAlias();
			String className = configuration.getClassName(returnAlias);
			EntityMapper em = configuration.getEntity(className);
			if(result instanceof List){
				List<Object> results = (List<Object>) result;
				for (Object cacheObj:results) {
					Object id = Reflection.reflectToGetId(em,cacheObj);
					//如果id属性不为空，则将其缓存起来
					if(id != null) {
						sessionFactory.cache(className, id, cacheObj);
					}
				}
			}
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}


	protected static class Builder{

		private Configuration configuration;

		private Connection connection;

		private Class<?> clazz;

		private SessionFactory sessionFactory;

		private boolean current = false;

		private boolean autoCommit = false;

		protected Builder(){}

		protected Builder(boolean current){
			this.current = current;
		}

		protected Builder configuration(Configuration configuration){
			this.configuration = configuration;
			return this;
		}

		protected Builder connection(Connection connection){
			System.out.println("in SimpleSession: connection.hashCode="+connection.hashCode());
			this.connection = connection;
			return this;
		}

		protected Builder clazz(Class<?> clazz){
			this.clazz = clazz;
			return this;
		}

		protected Builder sessionFactory(SessionFactory sessionFactory){
			this.sessionFactory = sessionFactory;
			return this;
		}

		protected Builder autoCommit(boolean autoCommit){
			this.autoCommit = autoCommit;
			return this;
		}

		private Executor createExecutor(Session session){
			return new StandardExecutor(session,connection);
		}

		protected SimpleSession build(){
			SimpleSession session = new SimpleSession();
			session.setConnection(connection);
			session.setClazz(clazz);
			session.setConfiguration(configuration);
			session.setSessionFactory(sessionFactory);
			session.setCurrent(current);
			session.setExecutor(createExecutor(session));
			return session;
		}

	}
}
