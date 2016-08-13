package com.lwy.myselect.session;

import com.lwy.myselect.cache.CacheManager;
import com.lwy.myselect.datasource.pool.DataBaseConnection;
import com.lwy.myselect.mapper.Configuration;

import java.sql.Connection;

public final class SessionFactory {
	private static ThreadLocal<Session> local = new ThreadLocal<>(); //must be static

	private Configuration configuration;
	private CacheManager cacheManager;

	public SessionFactory(Configuration configuration){
		this.configuration = configuration;
		cacheManager = configuration.createCacheManager();
	}

	public Session getSession(Class<?> clazz){
		Connection connection = DataBaseConnection.getConnection(configuration.getPoolType(),configuration.getOption());
		Session session = new SimpleSession.Builder().clazz(clazz)
													.configuration(configuration)
													.sessionFactory(this)
													.connection(connection)
													.build();
		return new SimpleSessionWrapper(session);
	}

//	public Configuration getConfiguration() {
//		return configuration;
//	}

	public Session getCurrentSession(Class<?> clazz){
		Session session = local.get();
		if(session == null){
			Connection connection = DataBaseConnection.getConnection(configuration.getPoolType(),configuration.getOption()); //这里已经修改为数据库连接池
			Session simpleSession = new SimpleSession.Builder(true).clazz(clazz)
																	.connection(connection)
																	.configuration(configuration)
																	.sessionFactory(this).build();
			session = new SimpleSessionWrapper(simpleSession);
			local.set(session);
		}
		if(session.getConnection() == null){
			Connection connection = DataBaseConnection.getConnection(configuration.getPoolType(),configuration.getOption()); //这里已经修改为数据库连接池
			((SimpleSession)((SimpleSessionWrapper) session).getSession()).setConnection(connection);
			((SimpleSession)((SimpleSessionWrapper) session).getSession()).open();
		}
		return session;
	}
	
	public void closeSession(Session session){
		session.close();
	}

	public <T,E> void cache(String className,T t,E e){
		cacheManager.save(className,t,e);
	}

	protected CacheManager getCacheManager(){
		return cacheManager;
	}
}
