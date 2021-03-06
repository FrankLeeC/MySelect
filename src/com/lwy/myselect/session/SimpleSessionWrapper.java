package com.lwy.myselect.session;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * decorator of session
 * Created by frank lee on 2016/7/26.
 * Email: frankleecsz@gmail.com
 */
public class SimpleSessionWrapper extends BaseSession{

    private BaseSession session;

    protected SimpleSessionWrapper(BaseSession session) {
        this.session = session;
    }

    protected Session getSession(){
        return session;
    }

    @Override
    public Class<?> getClazz() {
        return session.getClazz();
    }

    @Override
    public Connection getConnection() {
        return session.getConnection();
    }

    @Override
    public boolean isCurrent() {
        return session.isCurrent();
    }

    @Override
    public void openTransaction() {
        session.openTransaction();
    }

    @Override
    public boolean isTransaction() {
        return session.isTransaction();
    }

    @Override
    public void close() {
        session.close();
    }

    @Override
    public int commit() {
        return session.commit();
    }

    @Override
    public void rollback() throws SQLException {
        session.rollback();
    }

    @Override
    public int insert(String sql, Object object) {
        return session.insert(sql,object);
    }

    @Override
    public int delete(String sql, Object object) {
        return session.delete(sql,object);
    }

    @Override
    public int update(String sql, Object object) {
        return session.update(sql,object);
    }

    @Override
    public Object select(String sql, Object object) {
        return session.select(sql,object);
    }

    @Override
    public void addBatch(String sql) {
        session.addBatch(sql);
    }

    @Override
    public void addBatch(Object object) {
        session.addBatch(object);
    }

    @Override
    public void addBatch(List<Object> objects) {
        session.addBatch(objects);
    }

    @Override
    public int executeBatch(int count) throws SQLException {
        return session.executeBatch(count);
    }
}
