package com.lwy.myselect.executor;

import com.lwy.myselect.session.Session;

import java.sql.*;
import java.util.List;

/**
 * Created by frank lee on 2016/8/15 14:48.
 * Email: frankleecsz@gmail.com
 */
public class StandardExecutor implements Executor{

    private Session session;
    private Connection connection;

    public StandardExecutor(Session session, Connection connection) {
        this.session = session;
        this.connection = connection;
    }

    public ResultSet query(String sql, List<Object> propertyList) throws SQLException {
        checkConnection();
        PreparedStatement statement = preparedStatement(connection,propertyList,sql);
        return statement.executeQuery();
    }

    public int update(String sql, List<Object> propertyList) throws SQLException {
        checkConnection();
        PreparedStatement statement = preparedStatement(connection,propertyList,sql);
        return statement.executeUpdate();
    }

    private PreparedStatement preparedStatement(Connection connection,List<Object> propertyList, String sql) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(sql);
        for(int i=0;i<propertyList.size();i++){
            statement.setObject(i+1,propertyList.get(i));
        }
        return statement;
    }

    private void checkConnection(){
        if(connection == null)
            throw new NullPointerException("connection has closed");
    }
}
