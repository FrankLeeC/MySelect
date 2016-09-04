package com.lwy.myselect.datasource.statement;


import java.sql.*;

/**
 * Created by frank lee on 2016/8/10.
 */
public class RealStatement extends StatementWrapper {

    private Statement physicalStatement;
    private Connection realConnection;
    private ResultSet physicalResultSet;
    private boolean poolable = true;

    public RealStatement(Statement physicalStatement, Connection realConnection){
        this.physicalStatement = physicalStatement;
        this.realConnection = realConnection;
    }

    @Override
    public ResultSet executeQuery(String sql) throws SQLException {
        physicalResultSet = physicalStatement.executeQuery(sql);
        return physicalResultSet;
    }

    @Override
    public int executeUpdate(String sql) throws SQLException {
        return physicalStatement.executeUpdate(sql);
    }

    @Override
    public void close() throws SQLException {
        if(physicalResultSet != null)
            physicalResultSet.close();
        if(physicalStatement != null)
            physicalStatement.close();
    }

    @Override
    public int getMaxFieldSize() throws SQLException {
        return physicalStatement.getMaxFieldSize();
    }

    @Override
    public void setMaxFieldSize(int max) throws SQLException {
        physicalStatement.setMaxFieldSize(max);
    }

    @Override
    public int getMaxRows() throws SQLException {
        return physicalStatement.getMaxRows();
    }

    @Override
    public void setMaxRows(int max) throws SQLException {
        physicalStatement.setMaxRows(max);
    }

    @Override
    public void setEscapeProcessing(boolean enable) throws SQLException {
        physicalStatement.setEscapeProcessing(enable);
    }

    @Override
    public int getQueryTimeout() throws SQLException {
        return physicalStatement.getQueryTimeout();
    }

    @Override
    public void setQueryTimeout(int seconds) throws SQLException {
        physicalStatement.setQueryTimeout(seconds);
    }

    @Override
    public void cancel() throws SQLException {
        physicalStatement.cancel();
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        return physicalStatement.getWarnings();
    }

    @Override
    public void clearWarnings() throws SQLException {
        physicalStatement.clearWarnings();
    }

    @Override
    public void setCursorName(String name) throws SQLException {
        physicalStatement.setCursorName(name);
    }

    @Override
    public boolean execute(String sql) throws SQLException {
        return physicalStatement.execute(sql);
    }

    @Override
    public ResultSet getResultSet() throws SQLException {
        return physicalResultSet;
    }

    @Override
    public int getUpdateCount() throws SQLException {
        return physicalStatement.getUpdateCount();
    }

    @Override
    public boolean getMoreResults() throws SQLException {
        return physicalStatement.getMoreResults();
    }

    @Override
    public void setFetchDirection(int direction) throws SQLException {
        physicalStatement.setFetchDirection(direction);
    }

    @Override
    public int getFetchDirection() throws SQLException {
        return physicalStatement.getFetchDirection();
    }

    @Override
    public void setFetchSize(int rows) throws SQLException {
        physicalStatement.setFetchSize(rows);
    }

    @Override
    public int getFetchSize() throws SQLException {
        return physicalStatement.getFetchSize();
    }

    @Override
    public int getResultSetConcurrency() throws SQLException {
        return physicalStatement.getResultSetConcurrency();
    }

    @Override
    public int getResultSetType() throws SQLException {
        return physicalStatement.getResultSetType();
    }

    @Override
    public void addBatch(String sql) throws SQLException {
        physicalStatement.addBatch(sql);
    }

    @Override
    public void clearBatch() throws SQLException {
        physicalStatement.clearBatch();
    }

    @Override
    public int[] executeBatch() throws SQLException {
        return physicalStatement.executeBatch();
    }

    @Override
    public Connection getConnection() throws SQLException {
        return realConnection;
    }

    @Override
    public boolean getMoreResults(int current) throws SQLException {
        return physicalStatement.getMoreResults();
    }

    @Override
    public ResultSet getGeneratedKeys() throws SQLException {
        return physicalResultSet = physicalStatement.getGeneratedKeys();
    }

    @Override
    public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
        return physicalStatement.executeUpdate(sql,autoGeneratedKeys);
    }

    @Override
    public int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
        return physicalStatement.executeUpdate(sql,columnIndexes);
    }

    @Override
    public int executeUpdate(String sql, String[] columnNames) throws SQLException {
        return physicalStatement.executeUpdate(sql,columnNames);
    }

    @Override
    public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
        return physicalStatement.execute(sql,autoGeneratedKeys);
    }

    @Override
    public boolean execute(String sql, int[] columnIndexes) throws SQLException {
        return physicalStatement.execute(sql,columnIndexes);
    }

    @Override
    public boolean execute(String sql, String[] columnNames) throws SQLException {
        return physicalStatement.execute(sql,columnNames);
    }

    @Override
    public int getResultSetHoldability() throws SQLException {
        return physicalStatement.getResultSetHoldability();
    }

    @Override
    public boolean isClosed() throws SQLException {
        return physicalStatement.isClosed();
    }

    @Override
    public void setPoolable(boolean poolable) throws SQLException {
//        physicalStatement.setPoolable(poolable);
        this.poolable = poolable;
    }

    @Override
    public boolean isPoolable() throws SQLException {
        return this.poolable;
    }

    @Override
    public void closeOnCompletion() throws SQLException {
        physicalStatement.closeOnCompletion();
    }

    @Override
    public boolean isCloseOnCompletion() throws SQLException {
        return physicalStatement.isCloseOnCompletion();
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return physicalStatement.unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return physicalStatement.isWrapperFor(iface);
    }
}
