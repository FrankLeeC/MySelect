package com.lwy.myselect.mapper;

import com.lwy.myselect.datasource.Option;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * entityMappers
 *  key:class name
 *  value:entity mapper
 *
 * aliases
 *  key:alias
 *  value:class name
 * Created by frank lee on 2016/7/20.
 */
public class Configuration {
    private Map<String,Option> options = new ConcurrentHashMap<>();
    private Map<String,EntityMapper> entityMappers = new ConcurrentHashMap<>();
    private Map<String,String> aliases = new ConcurrentHashMap<>();
    private static Map<String,Class<?>> classTypes = new HashMap<>();

    public Configuration(){
        registerType("byte",byte.class);
        registerType("int",int.class);
        registerType("char",char.class);
        registerType("short",short.class);
        registerType("long",long.class);
        registerType("float",float.class);
        registerType("double",double.class);
        registerType("boolean",boolean.class);
        registerType("java.lang.Byte",Byte.class);
        registerType("java.lang.Integer",Integer.class);
        registerType("java.lang.Character",Character.class);
        registerType("java.lang.Short",Short.class);
        registerType("java.lang.Long",Long.class);
        registerType("java.lang.Float",Float.class);
        registerType("java.lang.Double",Double.class);
        registerType("java.lang.Boolean",Boolean.class);
    }

    public void registerType(String name,Class<?> clazz){
        classTypes.put(name,clazz);
    }

    public void registerOption(Option option){
        options.put(option.getName(),option);
    }

    public void registerEntity(String name,EntityMapper mapper){
        entityMappers.put(name,mapper);
    }

    public void registerAlias(String alias,String name){
        aliases.put(alias,name);
    }

    public EntityMapper getEntity(String name){
        return entityMappers.get(name);
    }

    public String getClassName(String alias){
        return aliases.get(alias);
    }

    public Option getOption(String name){
        return options.get(name);
    }

    public Class<?> getClass(String name){
        return classTypes.get(name);
    }
}
