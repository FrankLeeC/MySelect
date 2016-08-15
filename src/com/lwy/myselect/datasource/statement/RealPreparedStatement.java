package com.lwy.myselect.datasource.statement;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.util.Calendar;

/**
 * Created by frank lee on 2016/8/15 15:13.
 * Email: frankleecsz@gmail.com
 */
public class RealPreparedStatement extends PreparedStatementWrapper {

    private PreparedStatement physicalStatement;
    private Connection realConnection;
    private ResultSet physicalResultSet;
    private boolean poolable = true;

    public RealPreparedStatement(PreparedStatement physicalStatement, Connection realConnection){
        this.physicalStatement = physicalStatement;
        this.realConnection = realConnection;
    }

    @Override
    public ResultSet executeQuery() throws SQLException {
        return physicalResultSet = physicalStatement.executeQuery();
    }

    @Override
    public int executeUpdate() throws SQLException {
        return physicalStatement.executeUpdate();
    }

    @Override
    public void setNull(int parameterIndex, int sqlType) throws SQLException {
        physicalStatement.setNull(parameterIndex,sqlType);
    }

    @Override
    public void setBoolean(int parameterIndex, boolean x) throws SQLException {
        physicalStatement.setBoolean(parameterIndex,x);
    }

    @Override
    public void setByte(int parameterIndex, byte x) throws SQLException {
        physicalStatement.setByte(parameterIndex,x);
    }

    @Override
    public void setShort(int parameterIndex, short x) throws SQLException {
        physicalStatement.setShort(parameterIndex,x);
    }

    @Override
    public void setInt(int parameterIndex, int x) throws SQLException {
        physicalStatement.setInt(parameterIndex,x);
    }

    @Override
    public void setLong(int parameterIndex, long x) throws SQLException {
        physicalStatement.setLong(parameterIndex,x);
    }

    @Override
    public void setFloat(int parameterIndex, float x) throws SQLException {
        physicalStatement.setFloat(parameterIndex,x);
    }

    @Override
    public void setDouble(int parameterIndex, double x) throws SQLException {
        physicalStatement.setDouble(parameterIndex,x);
    }

    @Override
    public void setBigDecimal(int parameterIndex, BigDecimal x) throws SQLException {
        physicalStatement.setBigDecimal(parameterIndex,x);
    }

    @Override
    public void setString(int parameterIndex, String x) throws SQLException {
        physicalStatement.setString(parameterIndex,x);
    }

    @Override
    public void setBytes(int parameterIndex, byte[] x) throws SQLException {
        physicalStatement.setBytes(parameterIndex,x);
    }

    @Override
    public void setDate(int parameterIndex, Date x) throws SQLException {
        physicalStatement.setDate(parameterIndex,x);
    }

    @Override
    public void setTime(int parameterIndex, Time x) throws SQLException {
        physicalStatement.setTime(parameterIndex,x);
    }

    @Override
    public void setTimestamp(int parameterIndex, Timestamp x) throws SQLException {
        physicalStatement.setTimestamp(parameterIndex,x);
    }

    @Override
    public void setAsciiStream(int parameterIndex, InputStream x, int length) throws SQLException {
        physicalStatement.setAsciiStream(parameterIndex,x,length);
    }

    @Override
    public void setUnicodeStream(int parameterIndex, InputStream x, int length) throws SQLException {
        physicalStatement.setUnicodeStream(parameterIndex,x,length);
    }

    @Override
    public void setBinaryStream(int parameterIndex, InputStream x, int length) throws SQLException {
        physicalStatement.setBinaryStream(parameterIndex,x,length);
    }

    @Override
    public void clearParameters() throws SQLException {
        physicalStatement.clearParameters();
    }

    @Override
    public void setObject(int parameterIndex, Object x, int targetSqlType) throws SQLException {
        physicalStatement.setObject(parameterIndex,x,targetSqlType);
    }

    @Override
    public void setObject(int parameterIndex, Object x) throws SQLException {
        physicalStatement.setObject(parameterIndex,x);
    }

    @Override
    public boolean execute() throws SQLException {
        return physicalStatement.execute();
    }

    @Override
    public void addBatch() throws SQLException {
        physicalStatement.addBatch();
    }

    @Override
    public void setCharacterStream(int parameterIndex, Reader reader, int length) throws SQLException {
        physicalStatement.setCharacterStream(parameterIndex,reader, length);
    }

    @Override
    public void setRef(int parameterIndex, Ref x) throws SQLException {
        physicalStatement.setRef(parameterIndex,x);
    }

    @Override
    public void setBlob(int parameterIndex, Blob x) throws SQLException {
        physicalStatement.setBlob(parameterIndex,x);
    }

    @Override
    public void setClob(int parameterIndex, Clob x) throws SQLException {
        physicalStatement.setClob(parameterIndex,x);
    }

    @Override
    public void setArray(int parameterIndex, Array x) throws SQLException {
        physicalStatement.setArray(parameterIndex,x);
    }

    @Override
    public ResultSetMetaData getMetaData() throws SQLException {
        return physicalResultSet.getMetaData();
    }

    @Override
    public void setDate(int parameterIndex, Date x, Calendar cal) throws SQLException {
        physicalStatement.setDate(parameterIndex,x,cal);
    }

    @Override
    public void setTime(int parameterIndex, Time x, Calendar cal) throws SQLException {
        physicalStatement.setTime(parameterIndex,x,cal);
    }

    @Override
    public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal) throws SQLException {
        physicalStatement.setTimestamp(parameterIndex,x,cal);
    }

    @Override
    public void setNull(int parameterIndex, int sqlType, String typeName) throws SQLException {
        physicalStatement.setNull(parameterIndex,sqlType,typeName);
    }

    @Override
    public void setURL(int parameterIndex, URL x) throws SQLException {
        physicalStatement.setURL(parameterIndex,x);
    }

    @Override
    public ParameterMetaData getParameterMetaData() throws SQLException {
        return physicalStatement.getParameterMetaData();
    }

    @Override
    public void setRowId(int parameterIndex, RowId x) throws SQLException {
        physicalStatement.setRowId(parameterIndex,x);
    }

    @Override
    public void setNString(int parameterIndex, String value) throws SQLException {
        physicalStatement.setNString(parameterIndex,value);
    }

    @Override
    public void setNCharacterStream(int parameterIndex, Reader value, long length) throws SQLException {
        physicalStatement.setNCharacterStream(parameterIndex,value,length);
    }

    @Override
    public void setNClob(int parameterIndex, NClob value) throws SQLException {
        physicalStatement.setNClob(parameterIndex,value);
    }

    @Override
    public void setClob(int parameterIndex, Reader reader, long length) throws SQLException {
        physicalStatement.setClob(parameterIndex,reader,length);
    }

    @Override
    public void setBlob(int parameterIndex, InputStream inputStream, long length) throws SQLException {
        physicalStatement.setBlob(parameterIndex,inputStream,length);
    }

    @Override
    public void setNClob(int parameterIndex, Reader reader, long length) throws SQLException {
        physicalStatement.setNCharacterStream(parameterIndex,reader,length);
    }

    @Override
    public void setSQLXML(int parameterIndex, SQLXML xmlObject) throws SQLException {
        physicalStatement.setSQLXML(parameterIndex,xmlObject);
    }

    @Override
    public void setObject(int parameterIndex, Object x, int targetSqlType, int scaleOrLength) throws SQLException {
        physicalStatement.setObject(parameterIndex,x,targetSqlType,scaleOrLength);
    }

    @Override
    public void setAsciiStream(int parameterIndex, InputStream x, long length) throws SQLException {
        physicalStatement.setAsciiStream(parameterIndex,x,length);
    }

    @Override
    public void setBinaryStream(int parameterIndex, InputStream x, long length) throws SQLException {
        physicalStatement.setBinaryStream(parameterIndex,x,length);
    }

    @Override
    public void setCharacterStream(int parameterIndex, Reader reader, long length) throws SQLException {
        physicalStatement.setCharacterStream(parameterIndex,reader,length);
    }

    @Override
    public void setAsciiStream(int parameterIndex, InputStream x) throws SQLException {
        physicalStatement.setAsciiStream(parameterIndex,x);
    }

    @Override
    public void setBinaryStream(int parameterIndex, InputStream x) throws SQLException {
        physicalStatement.setBinaryStream(parameterIndex,x);
    }

    @Override
    public void setCharacterStream(int parameterIndex, Reader reader) throws SQLException {
        physicalStatement.setCharacterStream(parameterIndex,reader);
    }

    @Override
    public void setNCharacterStream(int parameterIndex, Reader value) throws SQLException {
        physicalStatement.setNCharacterStream(parameterIndex,value);
    }

    @Override
    public void setClob(int parameterIndex, Reader reader) throws SQLException {
        physicalStatement.setClob(parameterIndex,reader);
    }

    @Override
    public void setBlob(int parameterIndex, InputStream inputStream) throws SQLException {
        physicalStatement.setBlob(parameterIndex,inputStream);
    }

    @Override
    public void setNClob(int parameterIndex, Reader reader) throws SQLException {
        physicalStatement.setNClob(parameterIndex,reader);
    }

    @Override
    public ResultSet executeQuery(String sql) throws SQLException {
        return physicalResultSet = physicalStatement.executeQuery(sql);
    }

    @Override
    public int executeUpdate(String sql) throws SQLException {
        return physicalStatement.executeUpdate(sql);
    }

    @Override
    public void close() throws SQLException {
        if(physicalResultSet != null || !physicalResultSet.isClosed()) {
            physicalResultSet.close();
            physicalResultSet = null;
        }
        if(physicalStatement != null || !physicalStatement.isClosed()){
            physicalStatement.close();
            physicalStatement = null;
        }
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
        return physicalResultSet = physicalStatement.getResultSet();
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
        return physicalStatement.getConnection();
    }

    @Override
    public boolean getMoreResults(int current) throws SQLException {
        return physicalStatement.getMoreResults(current);
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
        physicalStatement.setPoolable(poolable);
    }

    @Override
    public boolean isPoolable() throws SQLException {
        return physicalStatement.isPoolable();
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
