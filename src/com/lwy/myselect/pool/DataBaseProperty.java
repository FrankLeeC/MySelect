package com.lwy.myselect.pool;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DataBaseProperty {
	private static String driver;
	private static String url;
	private static String user;
	private static String password;
	
	static{
		InputStream in = DataBaseProperty.class.getClassLoader().getResourceAsStream("jdbc.properties");
		Properties properties = new Properties();
		try {
			properties.load(in);
			driver = properties.getProperty("driver");
			url = properties.getProperty("url");
			user = properties.getProperty("user");
			password = properties.getProperty("password");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static String getDriver(){
		return driver;
	}
	
	public static String getUrl(){
		return url;
	}
	
	public static String getUser(){
		return user;
	}
	
	public static String getPassword(){
		return password;
	}
}
