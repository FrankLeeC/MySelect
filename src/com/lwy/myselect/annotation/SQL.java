package com.lwy.myselect.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by frank lee on 2016/7/22.
 * Email: frankleecsz@gmail.com
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SQL {
    String value();  //alias
}
