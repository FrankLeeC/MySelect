package com.lwy.myselect.datasource;

import java.util.Properties;

/** DataSource option
 * 数据源配置
 * Created by frank lee on 2016/7/21.
 */
public class Option {
    private String name;
    private Properties properties = new Properties();

    public String getName() {
        return name;
    }

    public Option(String name){
        this.name = name;
    }

    public void registerOption(String name,String value){
        properties.put(name,value);
    }
}
