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
    private Parser parser;
    private List<String> entityPackageList;  //entity 所在的包    使用注解
    private Map<String,String> locations;  //resource 所在处        使用配置文件
    private boolean parsed = false;       //是否已经解析过
    private Configuration configuration;
    private boolean annotation = false;    //是否使用注解
    private boolean sqlAnnotation = false; //sql是否使用注解

    public XMLParser(){
        try {
            parser = new Parser();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    public Configuration parse() throws IOException, SAXException {
        if(!parsed){
            configuration = new Configuration();
            parseConfig();
            if(!annotation){ //if don't use annotation
                parseEntityXml(); //该配置包含类名，属性名，属性类型，字段名，字段类型，策略,sql等信息
            }
            else{ // if use annotation
                parseEntityAnnotation();
            }
            parsed = true;
        }
        return configuration;
    }

    /**
     * 如果没有配置，则使用默认的，不报错
     * @param pool node pool
     */
    private void registerPoolType(Node pool){
        if(pool != null)
            configuration.registerPoolType(pool.getTextContent());
        else
            configuration.registerPoolType("default");
    }

    /**
     * 如果没有配置，则使用默认的，不报错    configuration在获取option时，如果是null,则新建默认的option
     * @param dataSource node dataSource
     */
    private void registerDataSource(Node dataSource){
        if(dataSource != null){
            Element dataSourceElement = (Element) dataSource;
            Element optionElement = (Element) parser.evalNode(dataSourceElement,"option",NodeType.ELEMENT);
            String name = parser.evalAttribute(optionElement,"name");
            Option option = new Option(name);
            NodeList list = parser.evalChileNodes(optionElement);
            Properties properties = parser.evalAttributeValue(list);
            option.registerProperties(properties);
            configuration.registerOption(option);
        }
    }

    private void registerAnnotationOrXml(Node annotationNode,Element root){
        if(annotationNode != null){             //if use annotation configuration
            annotation = true;
            Element entityPackageElement = (Element) parser.evalNode(annotationNode,"entity",NodeType.ELEMENT);
            String sqlAnnotationStr = parser.evalAttribute((Element) annotationNode,"sql");
            if(sqlAnnotationStr !=null && "true".equalsIgnoreCase(sqlAnnotationStr))
                sqlAnnotation = true;
            NodeList entityPackageNodeList = parser.evalChileNodes(entityPackageElement);
            int len = entityPackageNodeList.getLength();
            entityPackageList = new ArrayList<>();          //entity 所在的包    使用注解
            for (int i = 0; i < len; i++) {
                Object node = entityPackageNodeList.item(i);
                if(node instanceof Element){
                    Element entityPackage = (Element) node;
                    entityPackageList.add(parser.evalContent(entityPackage));
                }
            }
        }
        else{
            locations = new HashMap<>();  //resource 所在处        使用配置文件
            NodeList resourceList = parser.evalNodeList(root,"resource",NodeType.ELEMENT);
            int aliasLen = resourceList.getLength();
            for (int i = 0; i < aliasLen; i++) {
                Element entity = (Element) resourceList.item(i);
                String className = parser.evalAttribute(entity,"type");
                String aliasName = parser.evalAttribute(entity,"alias");
                configuration.registerAlias(aliasName,className);
                String location = parser.evalAttribute(root,"location");
                locations.put(className,location);
            }

        }
    }

    private void registerCache(Node cacheNode){
        if(cacheNode != null){
            NodeList list = parser.evalNodeList(cacheNode,"entity",NodeType.ELEMENT);
            if(list != null && list.getLength() > 0){
                int len = list.getLength();
                for (int i = 0; i < len; i++) {
                    Element entityCache = (Element) list.item(i);
                    String strategy = parser.evalAttribute(entityCache,"strategy");
                    String className = parser.evalContent(entityCache);
                    configuration.registerCacheStrategy(className,strategy);
                }
            }
        }
    }

    /**
     * parse config.xml
     * 该配置包含类名，别名，资源配置地址,数据源配置
     */
    private void parseConfig() throws IOException, SAXException {
        Document document = parser.createDocument("src/config.xml");
        Element root = (Element) parser.evalNode(document,"mapper",NodeType.DOCUMENT);
        registerPoolType(parser.evalNode(root,"connectionpool",NodeType.ELEMENT));
        registerDataSource(parser.evalNode(root,"datasource",NodeType.ELEMENT));
        registerAnnotationOrXml(parser.evalNode(root,"annotation",NodeType.ELEMENT),root);
        registerCache(parser.evalNode(root,"cache",NodeType.ELEMENT));
    }

    /**
     * 通过xml配置实体类和sql
     */
    private void parseEntityXml(){
        Iterator<String> iterator = locations.keySet().iterator();
        while(iterator.hasNext()){
            String location = "src/" + locations.get(iterator.next());
            try {
                Document document = parser.createDocument(location);
                Element root = (Element) parser.evalNode(document,"config",NodeType.DOCUMENT);
                Element entity = (Element) parser.evalNode(root,"entity",NodeType.ELEMENT);
                String alias = parser.evalAttribute(entity,"name");
                String className = parser.evalAttribute(entity,"class");
                String tableName = parser.evalAttribute(entity,"table");
                String strategy = null;
                String key = null;
                List<PropertyMapper> propertyMapperList = new ArrayList<>();
                EntityMapper.Builder eb = new EntityMapper.Builder().alias(alias).className(className).table(tableName);
                EntityMapper entityMapper;
                NodeList propertyNodeList = parser.evalChileNodes(entity);
                for(int j=0;j<propertyNodeList.getLength();j++){
                    Node n = propertyNodeList.item(j);
                    if(n instanceof Element){
                        Element property = (Element) n;
                        if("id".equals(property.getNodeName())){    //id主键
                            String name = parser.evalAttribute(property,"name");
                            key = name;
                            String column = parser.evalAttribute(property,"column");
                            String type = parser.evalAttribute(property,"javaType");
                            Class<?> classType = configuration.getClass(type);
                            strategy = parser.evalAttribute(property,"strategy"); //主键策略
                            PropertyMapper propertyMapper = new PropertyMapper.Builder().name(name)
                                    .column(column).type(classType).build();
                            propertyMapperList.add(propertyMapper);
                        }
                        else{
                            String name = parser.evalAttribute(property,"name");
                            String column = parser.evalAttribute(property,"column");
                            String nullable = parser.evalAttribute(property,"not-null");
                            String type = parser.evalAttribute(property,"javaType");
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
    private List<SQLMapper> parseSqlXml(Element root){
        List<SQLMapper> sqlMapperList = new ArrayList<>();
        sqlMapperList.addAll(parser.evalSql(root,"insert"));
        sqlMapperList.addAll(parser.evalSql(root,"delete"));
        sqlMapperList.addAll(parser.evalSql(root,"update"));
        sqlMapperList.addAll(parser.evalSql(root,"select"));
        return sqlMapperList;
    }

    /**
     * 通过注解解析实体类和sql
     */
    private void parseEntityAnnotation(){
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
                        configuration.registerCacheStrategy(className,cacheStrategy);
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
                        String path = "src/" + (className.substring(0,index) + "." + className.substring(index+1))
                                .replaceAll("\\.","/") + ".sql.xml";
                        Document sqlMapperDocument;
                        try{
                            sqlMapperDocument = parser.createDocument(path);
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
