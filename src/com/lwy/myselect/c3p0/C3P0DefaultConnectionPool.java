package com.lwy.myselect.c3p0;


public class C3P0DefaultConnectionPool extends C3P0ConnectionPool {
	
	public C3P0DefaultConnectionPool(){
		cpds.setCheckoutTimeout(30000);
		cpds.setIdleConnectionTestPeriod(30);
		cpds.setInitialPoolSize(10);
		cpds.setMaxIdleTime(30);
		cpds.setMaxPoolSize(100);
		cpds.setMinPoolSize(10);
		cpds.setMaxStatements(200);
	}
	
	
}
