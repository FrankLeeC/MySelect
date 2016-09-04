package com.lwy.myselect.datasource;

import com.lwy.myselect.c3p0.C3P0ConnectionPool;


/**
 * Created by frank lee on 2016/8/14.
 * Email: frankleecsz@gmail.com
 */
public class DataSourceFactory {

    public static javax.sql.DataSource getDataSource(String poolType, Option option){
        if("default".equalsIgnoreCase(poolType))
            return new DefaultDataSource(option);
        else
            return new C3P0ConnectionPool().build().getConnectionPool(option);
    }
}
