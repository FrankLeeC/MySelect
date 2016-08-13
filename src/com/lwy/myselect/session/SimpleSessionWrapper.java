package com.lwy.myselect.session;

import java.sql.Connection;

/**
 * decorator of session
 * Created by frank lee on 2016/7/26.
 * Email: frankleecsz@gmail.com
 */
public class SimpleSessionWrapper implements Session{

    private Session session;

    protected SimpleSessionWrapper(Session session) {
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
}
