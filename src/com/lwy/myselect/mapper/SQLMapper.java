package com.lwy.myselect.mapper;

/**
 * Created by frank lee on 2016/7/21.
 */
public class SQLMapper {
    private String id;  //sql identify
    private String sql;  //sql string
    private Integer timeout;
    private String returnAlias;

    private SQLMapper(){}

    public String getId() {
        return id;
    }

    public String getSql() {
        return sql;
    }

    public Integer getTimeout() {
        return timeout;
    }

    public String getReturnAlias() {
        return returnAlias;
    }

    public static class Builder{
        private String id;  //sql identify
        private String sql;  //sql string
        private Integer timeout;
        private String returnAlias;

        public Builder(){}

        public SQLMapper build(){
            SQLMapper mapper = new SQLMapper();
            mapper.id = id;
            mapper.sql = sql;
            mapper.timeout = timeout;
            mapper.returnAlias = returnAlias;
            return mapper;
        }

        public Builder id(String id){
            this.id = id;
            return this;
        }

        public Builder sql(String sql){
            this.sql = sql;
            return this;
        }

        public Builder timeout(Integer timeout){
            this.timeout = timeout;
            return this;
        }

        public Builder returnAlias(String returnAlias){
            this.returnAlias = returnAlias;
            return this;
        }
    }

}
