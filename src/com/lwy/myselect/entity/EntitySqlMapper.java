package com.lwy.myselect.entity;

import com.lwy.myselect.annotation.*;

//String的名字作为id表示符号 ，将来在注解上可以少使用一个属性，而且反射查找减少遍历，将来在注解上添加其他功能
@SQL(value = "entity")
public class EntitySqlMapper {
	
	@Insert(returns = "int")
	public final static String insertEntity = "insert into entity (entity_lon,entity_str,entity_fl,entity_dou,entity_date) values(?,?,?,?,?);";
	@Delete(returns = "int")
	public final static String deleteEntity = "delete from entity where entity_inte = ?;";
	@Update(returns = "int")
	public final static String updateEntity = "update entity set entity_dou = ? where entity_inte = ?;";
	@Select(returns= "entity")
	public final static String selectEntityStr = "select entity_str from entity where entity_inte = ?;";
	@Select(returns="entity")
	public final static String selectEntity = "select * from entity where entity_inte = ?;";
	@Select(returns="entity")
	public final static String selectCountEntity = "select count(*) from entity;";
	@Select(returns="entity")
	public final static String selectCountSpecialEntity = "select count(*) from entity where entity_inte = ?;";
}
 