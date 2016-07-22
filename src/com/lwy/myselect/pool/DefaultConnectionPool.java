package com.lwy.myselect.pool;


public class DefaultConnectionPool extends ConnectionPool{
	
	public DefaultConnectionPool(){
		cpds.setCheckoutTimeout(30000);
		cpds.setIdleConnectionTestPeriod(30);
		cpds.setInitialPoolSize(10);
		cpds.setMaxIdleTime(30);
		cpds.setMaxPoolSize(100);
		cpds.setMinPoolSize(10);
		cpds.setMaxStatements(200);
	}
	
	
}
