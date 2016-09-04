package com.lwy.myselect.executor;

import com.lwy.myselect.mapper.EntityMapper;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by frank lee on 2016/8/15 14:41.
 * Email: frankleecsz@gmail.com
 */
public interface Executor {

    int update(String sql, List<Object> propertyList) throws SQLException;

    Object query(String sql, List<Object> propertyList, EntityMapper entityMapper) throws SQLException;

    int executeBatch(String sql, List<List<Object>> batchProperties, int count) throws SQLException;
}
