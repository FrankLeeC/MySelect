package com.lwy.myselect.session;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.lwy.myselect.mapper.Configuration;
import com.lwy.myselect.mapper.EntityMapper;
import com.lwy.myselect.pool.DataBaseConnection;
import com.lwy.myselect.reflection.Reflection;

import static com.lwy.myselect.reflection.Reflection.reflectToGetId;

public final class Session {

	private boolean current = false;
	private boolean transaction = false;
	private List<Object> batchObjectList = null;    //批处理对象
	private String singleBatchSql = null;           //单条sql批处理
	private List<String> batchSqlList = null;       //批处理sql
	private Class<?> clazz;
	private Connection connection;
	private Configuration configuration;
	private SessionFactory sessionFactory;
	
	public Class<?> getClazz() {
		return clazz;
	}
	
	public void setClazz(Class<?> clazz){
		this.clazz = clazz; 
	}
	
	public Session(){}

	public Session(Class<?> clazz,Configuration configuration,SessionFactory sessionFactory){
		this.clazz = clazz;
		this.configuration = configuration;
		this.sessionFactory = sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public void setConfiguration(Configuration configuration){
		this.configuration = configuration;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}

	public Connection getConnection() {
		return connection;
	}

	public boolean isCurrent() {
		return current;
	}

	public void setCurrent(boolean current) {
		this.current = current;
	}
	
	public void openTransaction(){
		batchObjectList = new ArrayList<>();
		batchSqlList = new ArrayList<>();
		transaction = true;
	}
	
	public boolean isTransaction(){
		return transaction;
	}
	
	public void close(){
		try {
			if(connection != null)
				connection.close();
			connection = null;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public int commit(){
		if(isTransaction())
			return insertAndDeleteAndUpdate(singleBatchSql,batchObjectList);
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
	
	@SuppressWarnings("unchecked")
	private int singleSqlBatch(String sql,Object object){
		if(!isTransaction())
			return insertAndDeleteAndUpdate(sql, object);
		else{
			singleBatchSql = sql;
			batchObjectList = (List<Object>) object;
			return batchObjectList.size();
		}
	}
	
	public int insert(String sql,Object object){
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
		return singleSqlBatch(sql,object);
	}
	
	public int delete(String sql,Object object){
		return singleSqlBatch(sql,object);
	}
	
	public int update(String sql,Object object){
		return singleSqlBatch(sql,object);
	}

	private int insertAndDeleteAndUpdate(String sql,Object object){
		int result = 0;
		EntityMapper entityMapper = configuration.getEntity(clazz.getName());
		String sqlStatement = entityMapper.getSQLMapper(sql).getSql();
		List<Object> propertyList = null;
		List<List<Object>> propertyBatch = null;
		PreparedStatement ps = null;
		if(!isTransaction()){
			propertyList = Reflection.reflectToGetProperty(sqlStatement, object, clazz,entityMapper);
		}
		else{
			propertyBatch = new ArrayList<>();
			for(int i=0;i<batchObjectList.size();i++){
				List<Object> property = Reflection.reflectToGetProperty(sqlStatement, batchObjectList.get(i)
																						, clazz,entityMapper);
				propertyBatch.add(property);
			}
		}
		if(connection != null){
			try {
				if(!isTransaction()){
					ps = connection.prepareStatement(sqlStatement);
					for(int i=0;i<propertyList.size();i++)
						ps.setObject(i+1, propertyList.get(i));
					result = ps.executeUpdate();
				}
				else{
					connection.setAutoCommit(false);
					ps = connection.prepareStatement(sqlStatement);
					for(int j=0;j<batchObjectList.size();j++){
						List<Object> properties = propertyBatch.get(j);
						for(int i=0;i<properties.size();i++)
							ps.setObject(i+1, properties.get(i));
						ps.addBatch();
						if(j>0&&j%200 == 0){
							ps.executeBatch();
							connection.commit();
							ps.clearBatch();
						}
					}
					ps.executeBatch();
					ps.clearBatch();
					connection.commit();
					connection.setAutoCommit(true);
					result = batchObjectList.size();
				}
			} catch (Exception e) {
				e.printStackTrace();
				if(isTransaction()){
					try {
						if(connection != null){
							connection.rollback();
							connection.setAutoCommit(true);
						}
					} catch (Exception e2) {
						// TODO: handle exception
					}
				}
			}finally{
				if(isTransaction())
					resetBatch(); //重置batchList 和  sql
				if(ps != null)
					DataBaseConnection.closeConnection(connection, ps);
			}
		}
		return result;
	}
	
	
	public Object select(String sql,Object object){
		EntityMapper entityMapper = configuration.getEntity(clazz.getName());
		String sqlStatement = entityMapper.getSQLMapper(sql).getSql();
		String returnAlias = entityMapper.getSQLMapper(sql).getReturnAlias();
		List<Object> list = Reflection.reflectToGetProperty(sqlStatement, object, clazz,entityMapper);
		List<Object> result = new ArrayList<>();
		PreparedStatement ps = null;
		ResultSet rs = null;
		int len = list.size();
		List<String> columnList = null; 
		int columnLen = 0; 
		//带条件查询
		if(len>0){
			String className = configuration.getClassName(returnAlias);
			EntityMapper em = configuration.getEntity(className);
			columnList = Reflection.reflectToGetColumn(sqlStatement,em);
			columnLen = columnList.size();
		}
		if(connection != null){
			try {
				ps = connection.prepareStatement(sqlStatement);
				//如果带条件查询，就需要填充
				if(len>0){
					for(int i=0;i<len;i++)
						ps.setObject(i+1, list.get(i));
				}
				rs = ps.executeQuery();
				//如果查询数量
				if(sqlStatement.contains("count(*)")){
					rs.first();
					return rs.getObject("count(*)"); //查询记录条数时，结果是一张表，count(*)字段就是记录条数
				}
				//如果不是查询数量
				String className = configuration.getClassName(returnAlias);
				while(rs.next()){
					List<Object> columnResult = new ArrayList<>();
					for(int i=0;i<columnLen;i++){
						columnResult.add(rs.getObject(columnList.get(i)));
					}
					Object o = Reflection.reflectToCreateEntity(columnResult, className, columnList,entityMapper);
					EntityMapper em = configuration.getEntity(className);
					Object id = Reflection.reflectToGetId(em,o);
					//如果id属性不为空，则将其缓存起来
					if(id != null)
						sessionFactory.cache(className,id,o);
					result.add(o);
				}
				return result;
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally{
				if(rs != null){
					DataBaseConnection.closeConnection(connection, ps, rs);
				}
			}
		}
		return null;
	}
}