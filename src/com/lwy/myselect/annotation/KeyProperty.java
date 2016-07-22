package com.lwy.myselect.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//only used on field
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface KeyProperty {
	String value(); //column name
	Class<?> type(); //class 
	String strategy(); //id strategy
}
