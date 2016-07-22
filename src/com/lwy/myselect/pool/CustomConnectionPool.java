package com.lwy.myselect.pool;

import com.lwy.myselect.datasource.Option;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import static java.lang.System.getProperties;


public class CustomConnectionPool extends ConnectionPool {
	
	public CustomConnectionPool(Option option){
		new DefaultConnectionPool();
		configCustomProperty(option);
	}
	
	private void configCustomProperty(Option option){
		try {
			Class<?> clazz = Class.forName(cpds.getClass().getName());
			//因为cpds继承自AbstractComboPooledDataSource，所有的set方法都在其中，所以不能用getDeclaredMethods
			Method[] methods = clazz.getMethods();
			Properties properties = option.getProperties();
			Iterator<Object> iterator = properties.keySet().iterator();
			while(iterator.hasNext()){
				String propertyName = (String) iterator.next();
				String value = (String) properties.get(propertyName);
				for(Method m:methods){
					if(("set"+propertyName).equalsIgnoreCase(m.getName())){
						String type = m.getParameters()[0].getType().getName();
						if("int".equals(type))								
							m.invoke(cpds, Integer.parseInt(value));
						else if("bool".equals(type))
							m.invoke(cpds, Boolean.parseBoolean(value));
						else
							m.invoke(cpds, value);
					}
				}
			}
		} catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}

}
