package com.lwy.myselect.datasource.connection;



import com.lwy.myselect.datasource.PooledDataSource;

import javax.sql.ConnectionEvent;
import javax.sql.ConnectionEventListener;
import javax.sql.PooledConnection;
import javax.sql.StatementEventListener;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * implementation of pooled connection
 * Created by frank lee on 2016/8/2.
 * Email: frankleecsz@gmail.com
 */
public class ManagedPooledConnection implements PooledConnection{

    private final String CLOSE = "CLOSE";
    private final String ERROR = "ERROR";
    private Properties properties;
    private ConnectionWrapper realConnection;
    protected PooledDataSource pooledDataSource;
    private long birth = System.currentTimeMillis();
    private Time time = new Time();
    private List<ConnectionEventListener> connectionEventListeners = new ArrayList<>();

    public ManagedPooledConnection(Properties properties, PooledDataSource pooledDataSource) {
        this.pooledDataSource = pooledDataSource;
        this.properties = properties;
        realConnection = new RealConnection(this.properties,this);
    }

    @Override
    public Connection getConnection() throws SQLException {
        return realConnection;
    }

    /**
     * close physical connection
     * @throws SQLException
     */
    @Override
    public void close() throws SQLException {
        System.out.println("close connection:"+realConnection.hashCode());
        realConnection.getPhysicalConnection().close();
        notifyListeners(CLOSE);
    }

    private void notifyListeners(String type){
        switch (type){
            case CLOSE:
                for (ConnectionEventListener listener : connectionEventListeners) {
                    listener.connectionClosed(new ConnectionEvent(this));
                }
                break;
            case ERROR:
                for (ConnectionEventListener listener : connectionEventListeners) {
                    listener.connectionErrorOccurred(new ConnectionEvent(this));
                }
                break;
        }

    }

    @Override
    public void addConnectionEventListener(ConnectionEventListener listener) {
        connectionEventListeners.add(listener);
    }

    @Override
    public void removeConnectionEventListener(ConnectionEventListener listener) {
        connectionEventListeners.remove(listener);
    }

    @Override
    public void addStatementEventListener(StatementEventListener listener) {

    }

    @Override
    public void removeStatementEventListener(StatementEventListener listener) {

    }

    void recycle(){
        setIdle(0);
        pooledDataSource.recycle(this);
    }

    public long getAge() {
        return System.currentTimeMillis() - birth;
    }

    public long getIdle() {
        return time.getIdle();
    }

    public void setIdle(long idle) {
        time.setIdle(idle);
    }

    private static class Time{
        private long idle;

        private long getIdle() {
            return idle;
        }

        private void setIdle(long idle) {
            this.idle = idle;
        }
    }
}
