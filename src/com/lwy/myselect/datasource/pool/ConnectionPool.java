package com.lwy.myselect.datasource.pool;

import com.lwy.myselect.datasource.DataSourceFactory;
import com.lwy.myselect.datasource.Option;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class ConnectionPool {

	private static DataSource dataSource = null;
	private final static Object lock = new Object();
	
	public static Connection getConnection(String poolType, Option option){
		if(dataSource == null) {
			synchronized (lock) {
				if (dataSource == null){
					dataSource = DataSourceFactory.getDataSource(poolType,option);
				}
			}
		}
		Connection con = null;
		try {
			con = dataSource.getConnection();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return con;
		
	}
	
	private static void closeConnection(Connection con){
		if(con != null)
			try {
				con.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	public static void closeConnection(Connection con , Statement statement){
		if(statement != null)
			try {
				statement.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		closeConnection(con);
	}
	
	public static void closeConnection(Connection con,Statement statement,ResultSet rs){
		if(rs != null)
			try {
				rs.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		closeConnection(con,statement);
	}

}
