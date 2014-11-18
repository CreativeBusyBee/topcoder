package com.utils.xml.xmlWriter;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class WriteXML {
    SAXTransformerFactory fac = (SAXTransformerFactory) SAXTransformerFactory.newInstance();

    private TransformerHandler handler = null;

    private OutputStream outStream = null;

    private String fileName;

    private AttributesImpl atts;

    private String rootElement;

    public WriteXML(String fileName, String rootElement) {
        this.fileName = fileName;
        this.rootElement = rootElement;
        init();
    }

    public void init() {
        try {
            handler = fac.newTransformerHandler();
            Transformer transformer = handler.getTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");//
            // 设置输出采用的编码方式
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");// 是否自动添加额外的空白
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");// 是否忽略xml声明

            outStream = new FileOutputStream(fileName);
            Result resultxml = new StreamResult(outStream);
            handler.setResult(resultxml);

            atts = new AttributesImpl();

            start();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void start() {
        try {
            handler.startDocument();
            handler.startElement("", "", rootElement, atts);
        } catch (SAXException e) {
            e.printStackTrace();
        }
    }

    public void write(HashMap<String, String> map, String objectElement) throws SAXException {
        Set<String> keys = map.keySet();
        Iterator it = keys.iterator();
        if (objectElement != null) {
            handler.startElement("", "", objectElement, atts);
        }
        while (it.hasNext()) {
            String key = (String) it.next();
            String value = map.get(key);
            handler.startElement("", "", key, atts);
            handler.characters(value.toCharArray(), 0, value.length());
            handler.endElement("", "", key);
        }
        if (objectElement != null) {
            handler.endElement("", "", objectElement);
        }
    }

    public void end() {
        try {
            handler.endElement("", "", rootElement);
            handler.endDocument();// 文档结束,同步到磁盘
            outStream.close();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        WriteXML xml = new WriteXML("c:/student.xml", "students");
        try {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("id", "20050505");
            map.put("name", "zhaobenshan");
            map.put("age", "21");
            map.put("classes", "Act051");

            xml.write(map, "student");

            map = new HashMap<String, String>();
            map.put("id", "20050506");
            map.put("name", "songdandan");
            map.put("age", "20");
            map.put("classes", "Act052");

            xml.write(map, "student");

            map = new HashMap<String, String>();
            map.put("id", "20050507");
            map.put("name", "fanchushi");
            map.put("age", "21");
            map.put("classes", "Act051");

            xml.write(map, "student");

            xml.end();
        } catch (SAXException e) {
            e.printStackTrace();
        }
    }

}
