package com.lwy.myselect.mapper;

/**
 * Created by frank lee on 2016/7/20.
 */
public class PropertyMapper {
    private String name;  //property name
    private String column;  //column name
    private boolean nullable;  //null or not-null
    private Class<?> type;  //java type

    private PropertyMapper(){}

    public String getName() {
        return name;
    }

    public String getColumn() {
        return column;
    }

    public boolean isNullable() {
        return nullable;
    }

    public Class<?> getType() {
        return type;
    }

    public static class Builder{
        private String name;  //property name
        private String column;  //column name
        private boolean nullable;  //null or not-null
        private Class<?> type;  //java type

        public Builder(){}

        public PropertyMapper build(){
            PropertyMapper mapper = new PropertyMapper();
            mapper.name = name;
            mapper.column = column;
            mapper.nullable = nullable;
            mapper.type = type;
            return mapper;
        }

        public Builder name(String name){
            this.name = name;
            return this;
        }

        public Builder column(String column){
            this.column = column;
            return this;
        }

        public Builder nullable(boolean nullable){
            this.nullable = nullable;
            return this;
        }

        public Builder type(Class<?> type){
            this.type = type;
            return this;
        }
    }

}
