package com.lwy.myselect.mapper;

public enum KeyNames {
	ALIAS("ALIAS"), //别名
	COLUMN("COLUMN"), //字段名
	NULLABLE("NULLABLE"), //字段是否可以为空
	STRATEGY("STRATEGY"), //主键策略
	JAVATYPE("JAVATYPE"), //java类型
	PROPERTY("PROPERTY"), //属性名
	CLASS("CLASS"), //类名
	LOCATION("LOCATION"), //配置文件地址
	TABLE("TABLE"), //表名
	SQLID("SQLID"), //sql id
	INSERT("INSERT"), 
	DELETE("DELETE"),
	UPDATE("UPDATE"),
	SELECT("SELECT"),
	RETURNTYPE("RETURNTYPE"), //sql返回类型
	SQLSTATEMENT("SQLSTATEMENT"); //sql语句
	private String description;
	KeyNames(String description){
		this.description = description;
	}
	public String getDescription(){
		return description;
	}
}
