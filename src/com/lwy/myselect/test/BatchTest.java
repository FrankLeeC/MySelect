package com.lwy.myselect.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class BatchTest {

	public static void main(String[] args) {
		Connection con = getConnection();
		String sql1 = "insert into entity (entity_inte,entity_lon,entity_str,entity_fl,entity_dou,entity_date) values(15,666,'ggg',7.9,67.9,'2016-5-14');";
		String sql2 = "update entity set entity_str = 'hhh' where entity_inte = 15;";
		String sql3 = "delete from entity where id = 16;"; //这一句会出错，导致回滚
		
		Statement stmt = null;
		try {
			con.setAutoCommit(false);
			stmt = con.createStatement();
			stmt.addBatch(sql1);
			stmt.addBatch(sql2);
			stmt.addBatch(sql3);
//			stmt.executeQuery(sql3);
			stmt.executeBatch();
			stmt.clearBatch();
			con.setAutoCommit(true);
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("error");
			try {
				if(con != null){
					con.rollback();
					con.setAutoCommit(true);
				}
			} catch (Exception e2) {
				// TODO: handle exception
			}
			
		} finally{
			try{
				if(stmt != null){
					stmt.close();
					stmt = null;
				}
				if(con != null){
					con.close();
					con = null;
				}
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}
	}
	
	private static Connection getConnection(){
		try {
			Connection con = null;
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/myselect", "root", "frank");
			return con;
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
