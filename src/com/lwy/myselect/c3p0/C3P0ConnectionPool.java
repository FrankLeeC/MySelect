package com.lwy.myselect.c3p0;

import java.beans.PropertyVetoException;

import com.lwy.myselect.datasource.Option;
import com.mchange.v2.c3p0.ComboPooledDataSource;


public class C3P0ConnectionPool {
	protected static ComboPooledDataSource cpds = null;
	
	public C3P0ConnectionPool build(){
		try {
			cpds = new ComboPooledDataSource();
			cpds.setDriverClass(C3P0DataBaseProperty.getDriver());
			cpds.setJdbcUrl(C3P0DataBaseProperty.getUrl());
			cpds.setUser(C3P0DataBaseProperty.getUser());
			cpds.setPassword(C3P0DataBaseProperty.getPassword());
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
		return this;
	}
	
	public ComboPooledDataSource getConnectionPool(){
		new C3P0DefaultConnectionPool();
		return cpds;
	}
	
	public ComboPooledDataSource getConnectionPool(Option option){
		new C3P0CustomConnectionPool(option);
		return cpds;
	}
}
