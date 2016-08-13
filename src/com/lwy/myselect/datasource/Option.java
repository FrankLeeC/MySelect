package com.lwy.myselect.datasource;

import java.util.Properties;


/** DataSource option
 * 数据源配置
 * Created by frank lee on 2016/7/21.
 * Email: frankleecsz@gmail.com
 */
public class Option {
    private String name; //该option的name
    private Properties properties = new Properties();  //各项参数

    public String getName() {
        return name;
    }

    public Option(String name){
        this.name = name;
    }

    public void registerOption(String name,String value){
        properties.put(name,value);
    }

    public Properties getProperties(){
        return properties;
    }

    public boolean contains(String name){
        return properties.containsKey(name);
    }

}
