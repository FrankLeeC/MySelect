package com.lwy.myselect.session;


import java.sql.SQLException;
import java.util.List;

/**
 * Session
 * Created by frank lee on 2016/7/26.
 * Email: frankleecsz@gmail.com
 */
public interface Session {

    Class<?> getClazz();

//    Connection getConnection();

    /**
     * 如果是current session(thread local)返回true,否则false
     * @return  true if this session is current thread local session, false otherwise
     */
    boolean isCurrent();

    /**
     * open transaction
     */
    void openTransaction();

    boolean isTransaction();

    void close();

    int commit();

    void rollback() throws SQLException;

    int insert(String sql,Object object);

    int delete(String sql,Object object);

    int update(String sql,Object object);

    Object select(String sql,Object object);

    void addBatch(String sql);

    void addBatch(Object object);

    void addBatch(List<Object> objects);

    int executeBatch(int count) throws SQLException;
}
