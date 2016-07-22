package com.lwy.myselect.pool;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

import com.lwy.myselect.datasource.Option;
import com.lwy.myselect.pool.ConnectionPool;
import com.mchange.v2.c3p0.ComboPooledDataSource;


public class JdbcConnection {
	
	public static synchronized Connection getConnection(Option option){
		
		ComboPooledDataSource cpds = new ConnectionPool().build().getConnectionPool(option);
		Connection con = null;
		try {
			con = cpds.getConnection();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return con;
		
	}
	
	public static void closeConnection(Connection con){
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
