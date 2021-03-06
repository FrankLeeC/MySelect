package com.lwy.myselect.resultset;

import com.lwy.myselect.mapper.Configuration;
import com.lwy.myselect.mapper.EntityMapper;
import com.lwy.myselect.reflection.Reflection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by frank lee on 2016/8/15 19:09.
 * Email: frankleecsz@gmail.com
 */
public class ResultHandler {

//    private Configuration configuration;
    private EntityMapper entityMapper;    //mapper of class returned
    private ResultSet resultSet;


    public ResultHandler(EntityMapper entityMapper, ResultSet resultSet) {
//        this.configuration = configuration;
        this.entityMapper = entityMapper;
        this.resultSet = resultSet;
    }

    /**
     *
     * @param columnList       name of column
     * @param className        class name
     * @return                 list
     * @throws SQLException
     */
    public Object handlerResult(List<String> columnList,String className) throws SQLException {
        List<Object> result = new ArrayList<>();
        int columnLen = columnList.size();
        while(resultSet.next()){
            List<Object> columnResult = new ArrayList<>();
            for(int i=0;i<columnLen;i++){
                columnResult.add(resultSet.getObject(columnList.get(i)));
            }
            Object o = Reflection.reflectToCreateEntity(columnResult, className, columnList,entityMapper);
//            EntityMapper em = configuration.getEntity(className);
//            Object id = Reflection.reflectToGetId(em,o);
            //如果id属性不为空，则将其缓存起来
//            if(id != null) {
//                sessionFactory.cache(className, id, o);
//            }
            result.add(o);
        }
        return result;
    }


}
