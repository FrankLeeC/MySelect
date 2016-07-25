package com.lwy.myselect.session;

import com.lwy.myselect.cache.CacheManager;
import com.lwy.myselect.mapper.Configuration;
import com.lwy.myselect.pool.DataBaseConnection;

import java.sql.Connection;
import java.sql.SQLException;

public final class SessionFactory {
	private static ThreadLocal<Session> local = new ThreadLocal<Session>(); //must be static

	private Configuration configuration;
	private CacheManager cacheManager;

	public SessionFactory(Configuration configuration){
		this.configuration = configuration;
		cacheManager = configuration.createCacheManager();
	}

	public Session getSession(Class<?> clazz){
		Session session = new Session(clazz,configuration,this);
		Connection connection = DataBaseConnection.getConnection(configuration.getOption());
		session.setConnection(connection);
		return session;
	}

	public Configuration getConfiguration() {
		return configuration;
	}

	public Session getCurrentSession(Class<?> clazz){
		Session session = local.get();
		if(session == null){
			session = new Session();
			session.setCurrent(true);
		}
		local.set(session);
		Connection connection = DataBaseConnection.getConnection(configuration.getOption()); //这里已经修改为数据库连接池
		session.setConnection(connection);
		session.setClazz(clazz);
		session.setConfiguration(configuration);
		session.setSessionFactory(this);
		return session;
	}
	
	public void closeSession(Session session){
		if(session.isCurrent()){
			try {
				if(session.getConnection() != null)
					session.getConnection().close();
				session.setConnection(null);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else{
			session.close();
		}
	}

	public <T,E> void cache(String className,T t,E e){
		cacheManager.save(className,t,e);
	}
}