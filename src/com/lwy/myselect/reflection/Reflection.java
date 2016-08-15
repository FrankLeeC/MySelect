package com.lwy.myselect.reflection;

import com.lwy.myselect.mapper.EntityMapper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class Reflection {
	
	/**
	 * 获取属性的值，用于放入PreparedStatement中执行
	 * @param sqlStatement sql语句
	 * @param object 实例对象
	 * @param clazz 实体对象的类对象
	 * @return List<Object>  属性值的集合
	 */
	public static List<Object> reflectToGetProperty(String sqlStatement, Object object, Class<?> clazz, EntityMapper 																								entityMapper){
		List<Object> propertyList = new ArrayList<>();
		//如果是插入。需要特殊处理
		if(sqlStatement.trim().toLowerCase().startsWith("insert")){
			String propertyStr = sqlStatement.substring(sqlStatement.indexOf("(")+1,sqlStatement.indexOf(")"));
			String[] columnArray = propertyStr.split(",");
			for (int i = 0; i < columnArray.length; i++) {   //需要去掉空格
				String trim = columnArray[i].trim();
				columnArray[i] = trim;
			}
			String[] propertyArray = getProperties(columnArray,entityMapper);
			reflectToInvokeGetMethod(propertyList, clazz, propertyArray, object);
		}
		//如果不是插入
		else{
			List<String> columnList = splitSQL(sqlStatement);
			String[] columnArray = new String[columnList.size()];
			int len = columnArray.length;
			for(int i=0;i<len;i++)
				columnArray[i] = columnList.get(i);
			String[] propertyArray = getProperties(columnArray,entityMapper);
			reflectToInvokeGetMethod(propertyList, clazz, propertyArray, object);
		}
		return propertyList;
	}

	/**
	 * 解析sql
	 * @param sql  sql
	 * @return   list which only contains column names
     */
	private static List<String> splitSQL(String sql){
		List<String> list = new ArrayList<>();
		while(sql.contains("?")){
			if(sql.startsWith(","))
				sql = sql.substring(1);
			int index = sql.indexOf("?");
			String s = sql.substring(0,index);
			String[] ss = s.split(" ");
			List<String> temp = new ArrayList<>();
			for(String st:ss)
				temp.add(st);
			Iterator<String> iterator = temp.iterator();
			while(iterator.hasNext()){     //需要去掉多余的空格
				String str = iterator.next().trim();
				if(str.length() == 0 || "".equals(str))
					iterator.remove();
			}
			list.add(temp.get(temp.size()-2));
			sql = sql.substring(index+1);
		}
		return list;
	}
	
	/**
	 * 根据数组顺序找出对应getter，然后得到返回值装入list
	 * @param list 属性值的集合，由reflectToGetProperty方法传入
	 * @param clazz  目标类
	 * @param properties 属性数组
	 * @param object 实例
	 */
	private static void reflectToInvokeGetMethod(List<Object> list,Class<?> clazz,String[] properties
															,Object object){
		Method[] methods = clazz.getDeclaredMethods();
		for(int i=0;i<properties.length;i++){
			String propertyName = properties[i];
			for(int j=0;j<methods.length;j++){
				methods[j].setAccessible(true);
				if(methods[j].getName().equalsIgnoreCase("get"+propertyName)){
					try {
						list.add(methods[j].invoke(object));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	/*
	 * 根据sql中的字段名获取到对应的对象属性
	 * 如user_name(column) --->  name(property)
	 * @return 返回属性数组
	 *
	 */
	private static String[] getProperties(String[] propertiesInSql,EntityMapper entityMapper){
		String[] properties = new String[propertiesInSql.length];
		for(int i=0;i<propertiesInSql.length;i++){
			String column = propertiesInSql[i];
			properties[i] = entityMapper.getPropertyMapper(column).getName();
		}
		return properties;
	}
	
	/**
	 * 根据sql返回类型，得到字段名
	 * 如果是select * 则返回全部字段，否则返回指定字段
	 * @param sqlStatement  sql
	 * @param entityMapper 返回类型对应的实体映射
	 * @return 返回字段名集合
	 */
	public static List<String> reflectToGetColumn(String sqlStatement,EntityMapper entityMapper){
		List<String> list;
		//查询所有字段
		if(sqlStatement.toLowerCase().trim().startsWith("select * from")){
			Set<String> columns = entityMapper.getColumns();
			String[] temp = new String[columns.size()];
			temp = columns.toArray(temp);
			list = Arrays.asList(temp);
		}
		//查询指定字段
		else{
			int headIndex = sqlStatement.trim().indexOf("select");
			int tailIndex = sqlStatement.trim().indexOf("from");
			String columnSegment = sqlStatement.trim().substring(headIndex+6,tailIndex).trim();
			String[] columns = columnSegment.split(",");
			list = Arrays.asList(columns);
		}
		return list;
	}
	
	/**
	 * 根据数据查询结果封装为对象
	 * @param propertyList  数据查询结果
	 * @param className  类名
	 * @param columns 需要查询的字段名，可能是全部，也可能是一部分
	 * @return 返回一个实例
	 */
	public static Object reflectToCreateEntity(List<Object> propertyList,String className
											,List<String> columns, EntityMapper entityMapper){
		try {
			Class<?> clazz = Class.forName(className);
			Method[] methods = clazz.getDeclaredMethods();
			Object object = clazz.newInstance();
			String[] properties = getProperties(columns.toArray(new String[columns.size()]),entityMapper);
			int len = properties.length;
			for(int i=0;i<len;i++){
				String property = properties[i];
				for(Method m:methods){
					if(("set"+property).equalsIgnoreCase(m.getName())){
						m.invoke(object,propertyList.get(i));
					}
				}
			}
			return object;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static Object reflectToGetId(EntityMapper entityMapper,Object object){
		String className = entityMapper.getClassName();
		Object id = null;
		Class<?> clazz = null;
		try {
			clazz = Class.forName(className);
			String key = entityMapper.getKey();
			Method[] methods = clazz.getDeclaredMethods();
			for(Method m:methods){
				if(("get"+key).equalsIgnoreCase(m.getName())){
					id = m.invoke(object);
				}
			}
		} catch (ClassNotFoundException | InvocationTargetException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return id;
	}
}
