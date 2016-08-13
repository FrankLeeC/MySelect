package com.lwy.myselect.datasource;

/**
 * Created by frank lee on 2016/8/10.
 * Email: frankleecsz@gmail.com
 */
public interface DataSource extends javax.sql.DataSource {
    void close();
}
