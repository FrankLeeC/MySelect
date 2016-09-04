package com.lwy.myselect.datasource;


import com.lwy.myselect.datasource.connection.ConnectionWrapper;
import com.lwy.myselect.datasource.connection.ManagedPooledConnection;

import javax.sql.ConnectionPoolDataSource;
import javax.sql.PooledConnection;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;

/**
 * manager pooled connection
 * create,hold,remove
 * Created by frank lee on 2016/8/2.
 * Email: frankleecsz@gmail.com
 */
public class PooledDataSource implements ConnectionPoolDataSource{

    private Option option;
    private boolean initiated = false;
    private volatile boolean run = true;              //check thread
    private int acquireIncrement = 3;                 //每次增加的数量
    private int initialPoolSize = 10;                  //初始化的size
    private int maxPoolSize = 30;                      //最大的pool  size
    private int maxProcessor = 15;                     //最大同时工作数量
    private int minPoolSize = 10;                      //最小pool size
    private int maxIdleTime = 10;                      //空闲时间 0表示永不撤销
    private int currentCount = 0;                      //当前总数量
    private int currentWork = 0;                       //当前工作数量
    private int maxConnectionAge = 30;                 //最长时间，不同于maxIdleTime,当达到这个时间，必须撤销，0表示永不撤销
    private int timeout = 0;                           //等待connect to db的最大时间   SECOND
    private String driver;
    private String url;
    private String user;
    private String password;
    private Properties properties;
    private ConcurrentLinkedQueue<PooledConnection> connections = new ConcurrentLinkedQueue<>();//only idle connection
    private List<PooledConnection> connectionCheckList = new ArrayList<>();   //检查用的集合， only idle connection
    private final Object lock = new Object();
    private PrintWriter writer = null;

    public PooledDataSource(Option option) {
        this.option = option;
        init();
    }

    private void init(){
        if(!initiated){
            InputStream in = PooledDataSource.class.getClassLoader().getResourceAsStream("jdbc.properties");
            Properties property = new Properties();
            try {
                registerIfContains("acquireIncrement");
                registerIfContains("initialPoolSize");
                registerIfContains("maxPoolSize");
                registerIfContains("minPoolSize");
                registerIfContains("maxIdleTime");
                registerIfContains("maxConnectionAge");
                registerIfContains("timeout");
                property.load(in);
                driver = property.getProperty("driver");
                Class.forName(driver);
                url = property.getProperty("url");
                user = property.getProperty("user");
                password = property.getProperty("password");
                properties = new Properties();
                properties.put("driver",driver);
                properties.put("url",url);
                properties.put("user",user);
                properties.put("password",password);
                properties.put("maxIdleTime",maxIdleTime);
                properties.put("maxConnectionAge",maxConnectionAge);
            } catch (IOException | ClassNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            currentCount = initialPoolSize;
            createPool(initialPoolSize);
            threadStart();
            initiated = true;
        }
    }

    private void threadStart(){
        new Thread(new Checker()).start();
    }

    /**
     * 如果已经达到最大量，就不再生产
     * @param count 本次生产数量
     */
    private void createPool(int count){
        for(int i=0;i<count;i++){
            if(connections.size()>=maxPoolSize)
                break;
            PooledConnection connection = newConnection();
            connections.add(connection);
            synchronized (lock){
                connectionCheckList.add(connection);
            }
        }
    }

    private synchronized void moreCount(){
        currentCount++;
    }

    private synchronized void lessCount(){
        currentCount--;
    }

    private synchronized void moreWorker(){
        currentWork++;
    }

    private synchronized void lessWorker(){
        currentWork--;
    }

    private PooledConnection newConnection(){
        moreCount();
        return new ManagedPooledConnection(properties,this);
    }

    private void registerIfContains(String name){
        if(option.contains(name)){
            switch (name){
                case "acquireIncrement":
                    acquireIncrement = Integer.valueOf(option.getProperties().getProperty(name));
                    break;
                case "initialPoolSize":
                    initialPoolSize = Integer.valueOf(option.getProperties().getProperty(name));
                    break;
                case "maxPoolSize":
                    maxPoolSize = Integer.valueOf(option.getProperties().getProperty(name));
                    break;
                case "minPoolSize":
                    minPoolSize = Integer.valueOf(option.getProperties().getProperty(name));
                    break;
                case "maxIdleTime":
                    maxIdleTime = Integer.valueOf(option.getProperties().getProperty(name));
                    break;
                case "maxConnectionAge":
                    maxConnectionAge = Integer.valueOf(option.getProperties().getProperty(name));
                    break;
                case "timeout":
                    timeout = Integer.valueOf(option.getProperties().getProperty(name));
                    break;
            }
        }
    }

    /**
     * 如果拿到空，说明没有合格的并且空闲的connection
     *      如果没达到最大数量，则新建
     *      如果达到最大数量，就等待一段时间
     *          等待之后，如果有空闲的，则获取
     *          等待之后，如果没有空闲的，就继续等待，知道拿到，或者超时
     *          如果超时，返回null
     * @return connection wrapper
     * @throws SQLException
     */
    @Override
    public PooledConnection getPooledConnection() throws SQLException {
        PooledConnection pooledConnection = retrieveAndCheck(); //当为空时，
        if(pooledConnection != null) {                       //有空闲的
            return pooledConnection;
        }
        else{        //没有空闲的
           if(currentCount < maxPoolSize && currentWork < maxProcessor){  //当前数量小于最大数量 && 当前工作数量小于最大工作数量
                createPool(acquireIncrement);
                pooledConnection = connections.remove();
                connectionCheckList.remove(pooledConnection);
                moreWorker();
           }
           else{                   //不能新建，只能等待
               long before = System.currentTimeMillis();
               boolean flag = false;
               while(!flag){
                   if(timeout > 0 && System.currentTimeMillis() - before > timeout * 1000){
                       throw new SQLException("time out");         //超时
                   }
                   try {
                       Thread.sleep(2*1000);
                   } catch (InterruptedException e) {
                       throw new SQLException("out of wait");         //等待中断
                   }
                   pooledConnection = retrieveAndCheck();
                   if(pooledConnection != null){    //如果拿到了
                       flag = true;
                   }
               }
           }
        }
        return pooledConnection;
    }

    /**
     * 有可能一个connection在Checker线程休眠期间过期了，如果再被拿去使用，那么工作期间，Checker检查不到它，
     * 如果下次又在Checker休眠期间被拿走，有可能导致该连接长时间无法释放，所以在获取时，先检查状态，合格则返回，否则关闭
     * 如果全部都关闭了，那么poll出来的是空，那么跳出循环，返回空，让getPooledConnection新建连接
     * @return PooledConnection
     */
    private PooledConnection retrieveAndCheck(){
        boolean flag = false;
        ManagedPooledConnection connection = null;
        while(!flag){
            synchronized (lock){
                connection = (ManagedPooledConnection) connections.poll();
                if(connection == null){   //说明里面的连接全都是过期的，跳出循环，返回null，让getPooledConnection新建连接
                    break;
                }
                if(connection.getAge()>=maxConnectionAge*60*1000 || connection.getIdle()>=maxIdleTime*60*1000) {   //达到最大时长
                    try {
                        connection.close();  //关闭
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    connectionCheckList.remove(connection);
                    lessCount();
                }
                else{
                    flag = true;
                    connectionCheckList.remove(connection);
                    moreWorker();
                }
            }
        }
        return connection;
    }

    @Override
    public PooledConnection getPooledConnection(String user, String password) throws SQLException {
        return null;
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return writer;
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        writer = out;
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
        timeout = seconds;
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return timeout;
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return null;
    }

    public void recycle(PooledConnection pooledConnection){
        connections.add(pooledConnection);
        connectionCheckList.add(pooledConnection);
        lessWorker();                         //工作数量减一
    }

    /**
     * close specified pooled connection
     * @param connection connection wrapper to be closed
     * @throws SQLException
     */
    void close(Connection connection) throws SQLException {
        if(connectionCheckList.contains(connection)){
            synchronized (lock){
                ((ConnectionWrapper) connection).getPooledConnection().close();
                connectionCheckList.remove(((ConnectionWrapper) connection).getPooledConnection());
                connections.remove(((ConnectionWrapper) connection).getPooledConnection());
            }
        }
        else {
            ((ConnectionWrapper) connection).getPooledConnection().close();
        }
    }

    /**
     * close all pooled connection
     */
    synchronized void close(){
        for (PooledConnection connection : connections) {
            try {
                connection.close();
                writer.println(((ManagedPooledConnection)connection).getAge()
                        +"  "+((ManagedPooledConnection)connection).getIdle());
                writer.flush();
            } catch (SQLException e) {
                continue;  //继续关闭下一个
            }
        }
        clear();
    }

    private void clear(){
        connections.clear();
        connectionCheckList.clear();
        currentWork = 0;
        currentCount = 0;
    }

    private class Checker implements Runnable{

        @Override
        public void run() {
            while(run){
                synchronized (lock){
                    Iterator<PooledConnection> iterator = connectionCheckList.iterator();
                    while (iterator.hasNext()){
                        ManagedPooledConnection pooledConnection = (ManagedPooledConnection) iterator.next();
                        if(pooledConnection.getAge()>=maxConnectionAge*60*1000 ||
                                pooledConnection.getIdle()>=maxIdleTime*60*1000){   //达到最大时长
                            writer.println(pooledConnection.getAge()+"  "+pooledConnection.getIdle());
                            writer.flush();
                            iterator.remove();
                            connections.remove(pooledConnection);
                            try {
                                pooledConnection.close();
                                lessCount();          //总数减一
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }
                        else{
                            pooledConnection.setIdle(pooledConnection.getIdle()+3*1000);
                        }
                    }
                }
                try {
                    Thread.sleep(3*1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
