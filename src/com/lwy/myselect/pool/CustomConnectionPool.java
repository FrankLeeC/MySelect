package com.lwy.myselect.pool;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map;


public class CustomConnectionPool extends ConnectionPool {
	
	public CustomConnectionPool(String name){
		new DefaultConnectionPool();
		configCustomProperty(name);
	}
	
	private void configCustomProperty(String name){
		Map<String,String> optionMap = ParseConfiguration.dataSourceMap.get(name);
		try {
			Class<?> clazz = Class.forName(cpds.getClass().getName());
			//因为cpds继承自AbstractComboPooledDataSource，所有的set方法都在其中，所以不能用getDeclaredMethods
			Method[] methods = clazz.getMethods();
			Iterator<String> iterator = optionMap.keySet().iterator();
			while(iterator.hasNext()){
				String propertyName = iterator.next();
				String value = optionMap.get(propertyName);
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
