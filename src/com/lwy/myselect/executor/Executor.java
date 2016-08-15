package com.lwy.myselect.executor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by frank lee on 2016/8/15 14:41.
 * Email: frankleecsz@gmail.com
 */
public interface Executor {

    int update(String sql, List<Object> propertyList) throws SQLException;

    ResultSet query(String sql, List<Object> propertyList) throws SQLException;
}
