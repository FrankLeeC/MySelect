package com.lwy.myselect.datasource;

import javax.sql.ConnectionPoolDataSource;
import javax.sql.PooledConnection;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

/**
 * Created by frank lee on 2016/8/2.
 * Email: frankleecsz@gmail.com
 */
public class DefaultDataSource implements DataSource {

    private Option option;
    private ConnectionPoolDataSource poolDataSource;
    private PrintWriter writer = null;
    private boolean writable = false;
    private boolean closed = false;

    public DefaultDataSource(Option option) {
        this.option = option;
        poolDataSource = new PooledDataSource(this.option);
    }

    @Override
    public Connection getConnection() throws SQLException {
        if(!closed){
            PooledConnection pooledConnection = poolDataSource.getPooledConnection();
            if(pooledConnection != null)
                return pooledConnection.getConnection();
            else
                throw new SQLException("time out");
        }
        throw new SQLException("data source is already closed");
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        if(!closed){
            PooledConnection pooledConnection = poolDataSource.getPooledConnection(username,password);
            if(pooledConnection != null)
                return pooledConnection.getConnection();
            else
                throw new SQLException("time out");
        }
        throw new SQLException("data source is already closed");
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }

    /**
     * <p>Retrieves the log writer for this <code>DataSource</code>
     * object.
     *
     * <p>The log writer is a character output stream to which all logging
     * and tracing messages for this data source will be
     * printed.  This includes messages printed by the methods of this
     * object, messages printed by methods of other objects manufactured
     * by this object, and so on.  Messages printed to a data source
     * specific log writer are not printed to the log writer associated
     * with the <code>java.sql.DriverManager</code> class.  When a
     * <code>DataSource</code> object is
     * created, the log writer is initially null; in other words, the
     * default is for logging to be disabled.
     *
     * @return the log writer for this data source or null if
     *        logging is disabled
     * @exception SQLException if a database access error occurs
     * @see #setLogWriter
     * @since 1.4
     */
    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return writable ? writer : null;
    }

    /**
     * <p>Sets the log writer for this <code>DataSource</code>
     * object to the given <code>java.io.PrintWriter</code> object.
     *
     * <p>The log writer is a character output stream to which all logging
     * and tracing messages for this data source will be
     * printed.  This includes messages printed by the methods of this
     * object, messages printed by methods of other objects manufactured
     * by this object, and so on.  Messages printed to a data source-
     * specific log writer are not printed to the log writer associated
     * with the <code>java.sql.DriverManager</code> class. When a
     * <code>DataSource</code> object is created the log writer is
     * initially null; in other words, the default is for logging to be
     * disabled.
     *
     * @param out the new log writer; to disable logging, set to null
     * @exception SQLException if a database access error occurs
     * @see #getLogWriter
     * @since 1.4
     */
    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        if(out == null)
            writable = false;
        else{
            writer = out;
            writable = true;
            poolDataSource.setLogWriter(out);
        }
    }

    /**
     * <p>Sets the maximum time in seconds that this data source will wait
     * while attempting to connect to a database.  A value of zero
     * specifies that the timeout is the default system timeout
     * if there is one; otherwise, it specifies that there is no timeout.
     * When a <code>DataSource</code> object is created, the login timeout is
     * initially zero.
     *
     * @param seconds the data source login time limit
     * @exception SQLException if a database access error occurs.
     * @see #getLoginTimeout
     * @since 1.4
     */
    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        option.registerOption("timeout", String.valueOf(seconds));
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        String timeout = option.getProperties().getProperty("timeout");
        if(timeout == null)
            throw new NumberFormatException("timeout is not a number");
        return Integer.parseInt(timeout);
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return null;
    }

    /**
     *  {@link PooledDataSource#close} may be blocking, so change closed first,
     *  so {@link DefaultDataSource#getConnection()} will be disabled immediately after call this method.
     *  由于((PooledDataSource) poolDataSource).close() 会因为获取锁而被阻塞3秒，所以必须先修改closed，
     *  这样，当调用关闭方法之后，立马“生效”，getConnection()将立马失效.
     */
    @Override
    public void close() {
        if(!closed){
            closed = true;
            ((PooledDataSource) poolDataSource).close();
        }
    }
}
