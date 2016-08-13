package com.lwy.myselect.datasource.connection;

import javax.sql.ConnectionEvent;
import javax.sql.ConnectionEventListener;

/**
 * Created by frank lee on 2016/8/10.
 * Email: frankleecsz@gmail.com
 */
public class ConnectionListener implements ConnectionEventListener {
    @Override
    public void connectionClosed(ConnectionEvent event) {

    }

    @Override
    public void connectionErrorOccurred(ConnectionEvent event) {

    }
}
