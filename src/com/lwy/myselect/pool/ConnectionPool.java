package com.lwy.myselect.pool;

import java.beans.PropertyVetoException;

import com.mchange.v2.c3p0.ComboPooledDataSource;

public class ConnectionPool {
	protected static ComboPooledDataSource cpds = null;
	
	public ConnectionPool build(){
		try {
			cpds = new ComboPooledDataSource();
			cpds.setDriverClass(JdbcLoader.getDriver());
			cpds.setJdbcUrl(JdbcLoader.getUrl());
			cpds.setUser(JdbcLoader.getUser());
			cpds.setPassword(JdbcLoader.getPassword());
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
		return this;
	}
	
	public ComboPooledDataSource getConnectionPool(){
		new DefaultConnectionPool();
		return cpds;
	}
	
	public ComboPooledDataSource getConnectionPool(String name){
		new CustomConnectionPool(name);
		return cpds;
	}
}
