package com.lwy.myselect.mapper;

import java.util.*;

import static com.sun.org.apache.xml.internal.security.keys.keyresolver.KeyResolver.iterator;

/**
 * properties
 *  key:PropertyMapper.column
 *  value:PropertyMapper
 *
 * sqls
 *  key:SQLMapper.id
 *  value:SQLMapper
 * Created by frank lee on 2016/7/20.
 */
public class EntityMapper {
    private String className;
    private String alias;
    private String table;
    private String strategy;
    private Map<String,PropertyMapper> properties;
    private Map<String,SQLMapper> sqls;

    private EntityMapper(){}

    public String getClassName() {
        return className;
    }

    public String getAlias() {
        return alias;
    }

    public String getTable() {
        return table;
    }

    public String getStrategy() {
        return strategy;
    }

    public PropertyMapper getPropertyMapper(String name){
        return properties.get(name);
    }

//    public List<String> getProperties(){
//        List<String> propertyList = new ArrayList<>();
//        Collection<PropertyMapper> propertyMappers = properties.values();
//        Iterator<PropertyMapper> iterator = propertyMappers.iterator();
//        while(iterator.hasNext())
//            propertyList.add(iterator.next().getName());
//        return propertyList;
//    }

    public Set<String> getColumns(){
        return properties.keySet();
    }

    public SQLMapper getSQLMapper(String name){
        return sqls.get(name);
    }

    public static class Builder{
        private String className;
        private String alias;
        private String table;
        private String strategy;
        private Map<String,PropertyMapper> properties = new HashMap<>();
        private Map<String,SQLMapper> sqls = new HashMap<>() ;
        public Builder(){}
        public EntityMapper build(){
            EntityMapper mapper = new EntityMapper();
            mapper.className = className;
            mapper.alias = alias;
            mapper.table = table;
            mapper.strategy = strategy;
            mapper.properties = properties;
            mapper.sqls = sqls;
            return mapper;
        }
        public Builder className(String className){
            this.className = className;
            return this;
        }

        public Builder alias(String alias){
            this.alias = alias;
            return this;
        }

        public  Builder table(String table){
            this.table = table;
            return this;
        }

        public Builder strategy(String strategy){
            this.strategy = strategy;
            return this;
        }

        public Builder properties(List<PropertyMapper> properties){
            for (PropertyMapper pm:properties) {
                this.properties.put(pm.getColumn(),pm);
            }
            return this;
        }

        public Builder sqls(List<SQLMapper> sqls){
            for (SQLMapper sm:sqls) {
                this.sqls.put(sm.getId(),sm);
            }
            return this;
        }
    }
}
