package com.lwy.myselect.session;

import java.sql.Connection;

/**
 * Created by frank lee on 2016/8/15 14:51.
 * Email: frankleecsz@gmail.com
 */
abstract class BaseSession implements Session{

    /*
    connection shouldn't be accessed by user when using session
     */
    abstract Connection getConnection();
}
