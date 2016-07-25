package com.lwy.myselect.pool;

import java.beans.PropertyVetoException;

import com.lwy.myselect.datasource.Option;
import com.mchange.v2.c3p0.ComboPooledDataSource;


public class ConnectionPool {
	protected static ComboPooledDataSource cpds = null;
	
	public ConnectionPool build(){
		try {
			cpds = new ComboPooledDataSource();
			cpds.setDriverClass(DataBaseProperty.getDriver());
			cpds.setJdbcUrl(DataBaseProperty.getUrl());
			cpds.setUser(DataBaseProperty.getUser());
			cpds.setPassword(DataBaseProperty.getPassword());
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
		return this;
	}
	
	public ComboPooledDataSource getConnectionPool(){
		new DefaultConnectionPool();
		return cpds;
	}
	
	public ComboPooledDataSource getConnectionPool(Option option){
		new CustomConnectionPool(option);
		return cpds;
	}
}
