package com.utils.xml.xmlPaser;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class ParseXML extends DefaultHandler {
    private HashMap<String, String> map;// 存储对象的属性名和属性值

    private StringBuffer currentPath = new StringBuffer("/");

    private String objElement;// 标识一个对象的标签

    private String elementName;// 元素名称

    public ParseXML(String objElement) {
        this.objElement = objElement;
    }

    public void reader(String pathfile) {
        long start = System.currentTimeMillis();
        InputStream inStream = null;
        try {
            inStream = new FileInputStream(pathfile);
            SAXParserFactory saxfac = SAXParserFactory.newInstance();
            SAXParser saxParser = saxfac.newSAXParser();
            saxParser.parse(inStream, this);
        } catch (FileNotFoundException e) {
            e.printStackTrace();

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        long end = System.currentTimeMillis();
        System.out.println("解析耗时：" + (end - start) + "ms");
    }

    StringBuffer text = new StringBuffer("");

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (map != null && elementName != null) {
            String content = new String(ch, start, length);
            if (content.trim().length() > 0) {
                text.append(new String(ch, start, length));
                map.put(elementName, text.toString());
            }
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        currentPath.delete(currentPath.lastIndexOf(qName), currentPath.length());
        if (qName.equals(objElement)) {
            // do something for map
            System.out.println("Parsed and object: " + map);
        }
        elementName = null;
        text = new StringBuffer("");
    }

    @Override
    public void startElement(String uri,
                             String localName,
                             String qName,
                             Attributes attributes) throws SAXException {
        currentPath.append(qName + "/");
        elementName = qName;
        if (qName.equals(objElement)) {
            map = new HashMap<String, String>();
        }
    }

    public String getElementContent(String elementName) {
        return this.map.get(elementName);
    }

    public static void main(String[] args) {
        ParseXML xml = new ParseXML("student");
        xml.reader("c:/student.xml");
        System.out.println(xml.getElementContent("id"));
    }
}
