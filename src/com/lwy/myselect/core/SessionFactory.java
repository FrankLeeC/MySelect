package com.lwy.myselect.core;

import com.lwy.myselect.mapper.Configuration;
import com.lwy.myselect.pool.JdbcConnection;

import java.sql.Connection;
import java.sql.SQLException;

public final class SessionFactory {
	private static ThreadLocal<Session> local = new ThreadLocal<Session>(); //must be static

	private Configuration configuration;

	public SessionFactory(Configuration configuration){
		this.configuration = configuration;
	}

	public Session getSession(Class<?> clazz){
		Session session = new Session(clazz,configuration);
		Connection connection = JdbcConnection.getConnection();
		session.setConnection(connection);
		return session;
	}
	
	public Session getCurrentSession(Class<?> clazz){
		Session session = local.get();
		if(session == null){
			session = new Session();
			session.setCurrent(true);	
		}
		local.set(session);
		Connection connection = JdbcConnection.getConnection(); //这里已经修改为数据库连接池
		session.setConnection(connection);
		session.setClazz(clazz);
		session.setConfiguration(configuration);
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
}
