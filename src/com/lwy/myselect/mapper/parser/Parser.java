package com.lwy.myselect.mapper.parser;

import com.lwy.myselect.mapper.SQLMapper;
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
import java.util.Properties;

/**
 * Created by frank lee on 2016/8/14 16:34.
 * Email: frankleecsz@gmail.com
 */
class Parser {

    private DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    private DocumentBuilder db = dbf.newDocumentBuilder();

    Parser() throws ParserConfigurationException {

    }

    Document createDocument(String path) throws IOException, SAXException {
        return createDocument(new File(path));
    }

    Document createDocument(File file) throws IOException, SAXException {
        return db.parse(file);
    }

    Node evalNode(Object doc, String name, NodeType type){
        NodeList list = evalNodeList(doc, name, type);
        if(list != null)
            return list.item(0);
        return null;
    }

    NodeList evalNodeList(Object obj, String name, NodeType type){
        NodeList list = null;
        switch (type){
            case DOCUMENT:
                list = ((Document)obj).getElementsByTagName(name);
                break;
            case ELEMENT:
                list = ((Element)obj).getElementsByTagName(name);
                break;
        }
        if(list != null && list.getLength()>0)
            return list;
        return null;
    }

    SQLMapper evalSql(Element root, String type){
        NodeList insertList =evalNodeList(root,type,NodeType.ELEMENT);
        if(insertList != null && insertList.getLength()>0) {
            int insertLen = insertList.getLength();
            for (int i = 0; i < insertLen; i++) {
                Element sqlElement = (Element) insertList.item(i);
                String id = evalAttribute(sqlElement,"id");
                String sql = evalContent(sqlElement).trim();
                if (!sql.endsWith(";"))
                    sql = sql + ";";
                String t = evalAttribute(sqlElement,"timeout");
                String time = t == null? "0" : t;
                int timeout = Integer.valueOf(time);
                String returnAlias = evalAttribute(sqlElement,"return");
                return new SQLMapper.Builder().id(id).sql(sql).timeout(timeout)
                        .returnAlias(returnAlias).build();
            }
        }
        return null;
    }

    NodeList evalChileNodes(Node node){
        return node.getChildNodes();
    }

    String evalContent(Element element){
        return element.getTextContent();
    }

    Properties evalAttributeValue(NodeList list){
        Properties properties = new Properties();
        int len = list.getLength();
        for (int i = 0; i < len; i++) {
            Object node = list.item(i);
            if(node instanceof Element){
                Element element = (Element) node;
                String attribute = evalAttribute(element,"name");
                String value = evalContent(element);
                properties.put(attribute,value);
            }
        }
        return properties;
    }

    String evalAttribute(Element element, String attribute){
        return element.getAttribute(attribute);
    }
}
