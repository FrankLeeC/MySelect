package com.lwy.myselect.datasource.connection;

import javax.sql.PooledConnection;
import java.sql.Connection;

/**
 * Created by frank lee on 2016/8/10.
 * Email: frankleecsz@gmail.com
 */
public interface ConnectionWrapper extends Connection {
    Connection getPhysicalConnection();
    PooledConnection getPooledConnection();
}
