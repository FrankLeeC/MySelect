package com.lwy.myselect.mapper.parser;

import com.lwy.myselect.annotation.*;
import com.lwy.myselect.datasource.Option;
import com.lwy.myselect.mapper.*;
import com.lwy.myselect.mapper.util.GetClass;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;


/**
 * parse
 * Created by frank lee on 2016/7/21.
 * Email: frankleecsz@gmail.com
 */
public class XMLParser {
    private static DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    private static DocumentBuilder db;
    private static List<String> entityPackageList;  //entity 所在的包    使用注解
    private static Map<String,String> locations;  //resource 所在处        使用配置文件
    private static boolean parsed = false;       //是否已经解析过
    private static Configuration configuration;
    private static boolean annotation = false;    //是否使用注解
    private static boolean sqlAnnotation = false; //sql是否使用注解

    public static Configuration parse(){
        if(!parsed){
            try {
                db = dbf.newDocumentBuilder();
                configuration = new Configuration();
                parseConfig();
                if(!annotation){ //if don't use annotation
                    parseEntityXml(); //该配置包含类名，属性名，属性类型，字段名，字段类型，策略,sql等信息
                }
                else{ // if use annotation
                    parseEntityAnnotation();
                }
                parsed = true;
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            }
        }
        return configuration;
    }

    /**
     * parse config.xml
     * 该配置包含类名，别名，资源配置地址,数据源配置
     */
    private static void parseConfig(){
        try{
            Document document = db.parse(new File("src/config.xml"));
            NodeList nl = document.getElementsByTagName("mapper");
            Element root = (Element) nl.item(0);
            NodeList poolList = root.getElementsByTagName("connectionpool");
            if(poolList != null){
               configuration.registerPoolType(poolList.item(0).getTextContent());
            }
            else{
                configuration.registerPoolType("default");
            }
            NodeList dataSourceList = root.getElementsByTagName("datasource");
            if(dataSourceList != null && dataSourceList.getLength()>0){
                Element dataSource = (Element) dataSourceList.item(0);
                NodeList options = dataSource.getElementsByTagName("option");
                if(options != null && options.getLength()>0){
                    Element option = (Element) options.item(0);
                    String name = option.getAttribute("name");
                    Option op = new Option(name);
                    NodeList properties = option.getChildNodes();
                    int propertyNum = properties.getLength();
                    for(int j=0;j<propertyNum;j++){
                        Node node = properties.item(j);
                        if( node instanceof Element){
                            Element property = (Element) node;
                            String propertyName = property.getAttribute("name");
                            String value = property.getTextContent();
                            op.registerOption(propertyName,value);
                        }
                    }
                    configuration.registerOption(op);
                }
            }

            NodeList annotationList = root.getElementsByTagName("annotation");
            if(annotationList != null && annotationList.getLength()>0){          //if use annotation configuration
                entityPackageList = new ArrayList<>();  //entity 所在的包    使用注解
                annotation = true;
                Element configTypeElement = (Element) annotationList.item(0);
                Element entityPackageElement = (Element) configTypeElement.getElementsByTagName("entity").item(0);
                NodeList entityPackageNodeList = entityPackageElement.getChildNodes();
                for(int i=0;i<entityPackageNodeList.getLength();i++){
                    Object o = entityPackageNodeList.item(i);
                    if(o instanceof Element){
                        Element value = (Element) o;
                        entityPackageList.add(value.getTextContent());
                    }
                }
                String sqlAnnotationStr = configTypeElement.getAttribute("sql");
                if(sqlAnnotationStr != null && "true".equalsIgnoreCase(sqlAnnotationStr)){     // sql使用注解配置
                    sqlAnnotation = true;
                }
            }

            else{                             //if use xml configuration
                locations = new HashMap<>();  //resource 所在处        使用配置文件
                NodeList resourceList = root.getElementsByTagName("resource");
                int aliasLen = resourceList.getLength();
                for(int i=0;i<aliasLen;i++){
                    Element entity = (Element) resourceList.item(i);
                    String className = entity.getAttribute("type");
                    String aliasName = entity.getAttribute("alias");
                    configuration.registerAlias(aliasName,className);
                    String location = entity.getAttribute("location");
                    locations.put(className,location);
                }
            }

            NodeList cacheList = root.getElementsByTagName("cache");
            if(cacheList != null && cacheList.getLength()>0){      //使用cache
                Element cacheElements = (Element) cacheList.item(0);
                NodeList entityList = cacheElements.getElementsByTagName("entity");
                if(entityList != null && entityList.getLength()>0){
                    for(int i=0;i<entityList.getLength();i++){
                        Element entity = (Element) entityList.item(i);
                        String strategy = entity.getAttribute("keyStrategy");
                        String className = entity.getTextContent();
                        configuration.registerKeyStrategy(className,strategy);
                    }
                }
            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 通过xml配置实体类和sql
     */
    private static void parseEntityXml(){
        Iterator<String> iterator = locations.keySet().iterator();
        while(iterator.hasNext()){
            String location = "src/" + locations.get(iterator.next());
            try {
                Document document = db.parse(new File(location));
                NodeList nl = document.getElementsByTagName("config");
                Element root = (Element) nl.item(0);
                //parse property
                NodeList entityList = root.getElementsByTagName("entity");
                Element entity = (Element) entityList.item(0);
                String alias = entity.getAttribute("name");
                String className = entity.getAttribute("class");
                String tableName = entity.getAttribute("table");
                String strategy = null;
                String key = null;
                List<PropertyMapper> propertyMapperList = new ArrayList<>();
                EntityMapper.Builder eb = new EntityMapper.Builder().alias(alias).className(className).table(tableName);
                EntityMapper entityMapper;
                NodeList propertyNodeList = entity.getChildNodes();
                for(int j=0;j<propertyNodeList.getLength();j++){
                    Node n = propertyNodeList.item(j);
                    if(n instanceof Element){
                        Element property = (Element) n;
                        if("id".equals(property.getNodeName())){    //id主键
                            String name = property.getAttribute("name");
                            key = name;
                            String column = property.getAttribute("column");
                            String type = property.getAttribute("javaType");
                            Class<?> classType = configuration.getClass(type);
                            strategy = property.getAttribute("keyStrategy"); //主键策略
                            PropertyMapper propertyMapper = new PropertyMapper.Builder().name(name)
                                    .column(column).type(classType).build();
                            propertyMapperList.add(propertyMapper);
                        }
                        else{
                            String name = property.getAttribute("name");
                            String column = property.getAttribute("column");
                            String nullable = property.getAttribute("not-null");
                            String type = property.getAttribute("javaType");
                            Class<?> classType = configuration.getClass(type);
                            PropertyMapper propertyMapper = new PropertyMapper.Builder().name(name)
                                    .column(column).nullable(Boolean.valueOf(nullable)).type(classType).build();
                            propertyMapperList.add(propertyMapper);
                        }
                    }
                }
                //parse sql
                List<SQLMapper> sqlMapperList = parseSqlXml(root);
                entityMapper = eb.keyStrategy(strategy).key(key).properties(propertyMapperList).sqls(sqlMapperList).build();
                configuration.registerEntity(className,entityMapper);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    /**
     * 通过xml解析sql
     * @param root   root
     * @return    sql mapper list
     */
    private static List<SQLMapper> parseSqlXml(Element root){
        List<SQLMapper> sqlMapperList = new ArrayList<>();
        NodeList insertList = root.getElementsByTagName("insert");
        if(insertList != null && insertList.getLength()>0) {
            int insertLen = insertList.getLength();
            for (int i = 0; i < insertLen; i++) {
                Element sqlElement = (Element) insertList.item(i);
                String id = sqlElement.getAttribute("id");
                String sql = sqlElement.getTextContent().trim();
                if (!sql.endsWith(";"))
                    sql = sql + ";";
                String time = sqlElement.getAttribute("timeout") == null? "0" : sqlElement.getAttribute("timeout");
                int timeout = Integer.valueOf(time);
                String returnAlias = sqlElement.getAttribute("return");
                SQLMapper sqlMapper = new SQLMapper.Builder().id(id).sql(sql).timeout(timeout)
                        .returnAlias(returnAlias).build();
                sqlMapperList.add(sqlMapper);
            }
        }

        NodeList deleteList = root.getElementsByTagName("delete");
        if(deleteList != null && deleteList.getLength()>0){
            int deleteLen = deleteList.getLength();
            for(int i=0;i<deleteLen;i++){
                Element sqlElement = (Element) deleteList.item(i);
                String id = sqlElement.getAttribute("id");
                String time = sqlElement.getAttribute("timeout") == null? "0" : sqlElement.getAttribute("timeout");
                int timeout = Integer.valueOf(time);
                String returnAlias = sqlElement.getAttribute("return");
                String sql = sqlElement.getTextContent().trim();
                if(!sql.endsWith(";"))
                    sql = sql + ";";
                SQLMapper sqlMapper = new SQLMapper.Builder().id(id).timeout(timeout)
                        .returnAlias(returnAlias).sql(sql).build();
                sqlMapperList.add(sqlMapper);
            }
        }

        NodeList updateList = root.getElementsByTagName("update");
        if(updateList != null && updateList.getLength()>0){
            int updateLen = updateList.getLength();
            for(int i=0;i<updateLen;i++){
                Element sqlElement = (Element) updateList.item(i);
                String id = sqlElement.getAttribute("id");
                String sql = sqlElement.getTextContent().trim();
                if(!sql.endsWith(";"))
                    sql = sql + ";";
                String time = sqlElement.getAttribute("timeout") == null? "0" : sqlElement.getAttribute("timeout");
                int timeout = Integer.valueOf(time);
                String returnAlias = sqlElement.getAttribute("return");
                SQLMapper sqlMapper = new SQLMapper.Builder().id(id).sql(sql).timeout(timeout)
                        .returnAlias(returnAlias).build();
                sqlMapperList.add(sqlMapper);
            }
        }

        NodeList selectList = root.getElementsByTagName("select");
        if(selectList != null && selectList.getLength()>0){
            int selectLen = selectList.getLength();
            for(int i=0;i<selectLen;i++){
                Element sqlElement = (Element) selectList.item(i);
                String id = sqlElement.getAttribute("id");
                String returnType = sqlElement.getAttribute("return");
                String sql = sqlElement.getTextContent().trim();
                if(!sql.endsWith(";"))
                    sql = sql + ";";
                String time = sqlElement.getAttribute("timeout") == null? "0" : sqlElement.getAttribute("timeout");
                int timeout = Integer.valueOf(time);
                SQLMapper sqlMapper = new SQLMapper.Builder().id(id).sql(sql).timeout(timeout)
                        .returnAlias(returnType).build();
                sqlMapperList.add(sqlMapper);
            }
        }
        return sqlMapperList;
    }

    /**
     * 通过注解解析实体类和sql
     */
    private static void parseEntityAnnotation(){
        List<String> entityPathList = new ArrayList<>();
        for(String str:entityPackageList){ //get all class name
            GetClass gc = new GetClass();
            entityPathList.addAll(gc.getClass(str));
        }
        try {
            for(String className:entityPathList){ //reflect all class
                Class<?> clazz;
                try{
                    clazz = Class.forName(className);
                } catch(ClassNotFoundException e){
                    continue;  //说明当前类并不是需要注册的实体类
                }
                Table table = clazz.getDeclaredAnnotation(Table.class); //must use getDeclaredAnnotation
                if(table != null){ //this class is annotated
                    configuration.registerAlias(table.alias(),className);
                    String tableName = table.value();
                    EntityMapper.Builder eb = new EntityMapper.Builder()
                            .alias(table.alias()).className(className).table(tableName);
                    Field[] fields = clazz.getDeclaredFields(); //must use getDeclaredFields,it means declared fields
                    List<PropertyMapper> propertyMapperList = new ArrayList<>();
                    for(int i=0;i<fields.length;i++){
                        Field field = fields[i];
                        Fields fieldAnnotation = field.getDeclaredAnnotation(Fields.class); //must use getDeclaredAnnotation
                        if(fieldAnnotation != null){ //if not id property
                            boolean nullable = fieldAnnotation.nullable();
                            String column = fieldAnnotation.value();
                            Class<?> type = fieldAnnotation.type();
                            String name = field.getName();
                            PropertyMapper propertyMapper = new PropertyMapper.Builder().name(name)
                                    .nullable(nullable).column(column).type(type).build();
                            propertyMapperList.add(propertyMapper);
                        }
                        else{
                            KeyProperty keyAnnotation = field.getDeclaredAnnotation(KeyProperty.class);
                            if(keyAnnotation != null){ // if id property
                                String column = keyAnnotation.value();
                                Class<?> type = keyAnnotation.type();
                                String strategy = keyAnnotation.strategy();
                                String name = field.getName();
                                PropertyMapper propertyMapper = new PropertyMapper.Builder().name(name).column(column)
                                        .nullable(false).type(type).build();
                                eb.keyStrategy(strategy).key(name);
                                propertyMapperList.add(propertyMapper);
                            }
                        }
                    }
                    eb.properties(propertyMapperList);

                    Cache cache = clazz.getDeclaredAnnotation(Cache.class);
                    if(cache != null){
                        String cacheStrategy = cache.value();
                        configuration.registerKeyStrategy(className,cacheStrategy);
                    }

                    List<SQLMapper> sqlMapperList;
                    if(sqlAnnotation){          //sql使用注解配置
                        sqlMapperList = new ArrayList<>();
                        int index = className.lastIndexOf(".");
                        String packageName = className.substring(0,index);
                        String sqlClassName = packageName + "." + className.substring(index+1) + "SqlMapper";
                        try{
                            Class<?> sqlClass = Class.forName(sqlClassName);
                            Object sqlMapperInstance = sqlClass.newInstance();
                            SQL sqlAnnotation = sqlClass.getDeclaredAnnotation(SQL.class);
                            if(sqlAnnotation != null){
                                String sqlStatement;
                                String sqlName;
                                String returnAlias = null;
                                Integer timeout = 0;
                                Field[] sqlFields = sqlClass.getDeclaredFields();
                                for(int i=0;i<sqlFields.length;i++){
                                    Field field = sqlFields[i];
                                    Insert insertAnnotation = field.getDeclaredAnnotation(Insert.class);
                                    Delete deleteAnnotation = field.getDeclaredAnnotation(Delete.class);
                                    Update updateAnnotation = field.getDeclaredAnnotation(Update.class);
                                    Select selectAnnotation = field.getDeclaredAnnotation(Select.class);
                                    if(insertAnnotation != null){
                                        returnAlias = insertAnnotation.returns();
                                        timeout = insertAnnotation.timeout();
                                    }
                                    else if(deleteAnnotation != null){
                                        returnAlias = deleteAnnotation.returns();
                                        timeout = deleteAnnotation.timeout();
                                    }
                                    else if(updateAnnotation != null){
                                        returnAlias = updateAnnotation.returns();
                                        timeout = updateAnnotation.timeout();
                                    }
                                    else if(selectAnnotation != null){
                                        returnAlias = selectAnnotation.returns();
                                        timeout = selectAnnotation.timeout();
                                    }
                                    sqlStatement = field.get(sqlMapperInstance).toString().trim();
                                    if(!sqlStatement.endsWith(";"))
                                        sqlStatement = sqlStatement + ";";
                                    sqlName = field.getName();
                                    SQLMapper sqlMapper = new SQLMapper.Builder().id(sqlName).sql(sqlStatement)
                                            .returnAlias(returnAlias).timeout(timeout).build();
                                    sqlMapperList.add(sqlMapper);
                                }
                            }
                        } catch (ClassNotFoundException e){
                            continue;           //说明这个entity没有配置sql,直接进行下一个
                        }
                    }
                    else{                 //sql使用xml配置
                        int index = className.lastIndexOf(".");
                        String path = (className.substring(0,index) + "." + className.substring(index+1))
                                .replaceAll("\\.","/") + ".sql.xml";
                        Document sqlMapperDocument;
                        try{
                            sqlMapperDocument = db.parse(new File(path));
                        } catch (SAXException | IOException e){
                            continue;   //说明当前实例没有配置sql
                        }
                        Element root = (Element) sqlMapperDocument.getElementsByTagName("sql").item(0);
                        sqlMapperList = parseSqlXml(root);
                    }
                    EntityMapper entityMapper = eb.sqls(sqlMapperList).build();
                    configuration.registerEntity(entityMapper.getClassName(),entityMapper);
                }
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
    }
}
