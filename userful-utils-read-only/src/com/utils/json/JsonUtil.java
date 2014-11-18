package com.utils.json;

import java.io.Writer;
import java.util.List;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.json.JettisonMappedXmlDriver;
import com.thoughtworks.xstream.io.json.JsonHierarchicalStreamDriver;
import com.thoughtworks.xstream.io.json.JsonWriter;

public class JsonUtil {
	
	/**
	 * No instance.
	 */
	private JsonUtil() {
	}
	
	/**
	 * Parses an <code>Object</code> to the JSON string.
	 * 
	 * @param obj The input object.
	 * @return The parse result string.
	 */
	public static String toJson(Object obj) {
		XStream xstream = new XStream(new JsonHierarchicalStreamDriver() {
		    public HierarchicalStreamWriter createWriter(Writer writer) {
		        return new JsonWriter(writer, JsonWriter.DROP_ROOT_MODE);
		    }
		});
        xstream.setMode(XStream.NO_REFERENCES);
        return xstream.toXML(obj);
	}
	
	/**
	 * Parses a JSON string back to an <code>Object</code> with specific type.
	 * 
	 * @param <T> The expected type of result object.
	 * @param json The input JSON string.
	 * @param cls The class of result object.
	 * @return The parse result object with specific type.
	 */
	public static <T> T fromJson(String json, Class<T> cls) {
		XStream xstream = new XStream(new JettisonMappedXmlDriver());
		String className = cls.getSimpleName();
		xstream.alias(className, cls);
		String fullJson = "{\""+className+"\": " + json + "}";
		return (T) xstream.fromXML(fullJson);
	}
	
	/**
     * Parses a JSON string back to an <code>List</code> of <code>Object</code>s 
     * with specific type.
     * 
     * @param <T> The expected type of result object.
     * @param json The input JSON string.
     * @param cls The class of each result item.
     * @return The parse result List of object with specific type.
     */
	public static <T> List<T> fromJsonArray(String json, Class<T> cls) {
	    XStream xstream = new XStream(new JettisonMappedXmlDriver());
        String className = ListContainer.class.getSimpleName();
        xstream.alias(className, ListContainer.class );
        xstream.addImplicitCollection(ListContainer.class, "objects", cls);
        String fullJson = "{\""+className+"\": {\"objects\": " + json + "}}";
        ListContainer listContainer = (ListContainer)xstream.fromXML(fullJson);
        return (List<T>) listContainer.getObjects();
	}
	
	/**
	 * @deprecated Use {@link #fromJson(String, Class)} instead. Input JSON 
	 * 			   usually doesn't provide an concrete root.
	 */
	public static Object fromJson(String json) {
		XStream xstream = new XStream(new JettisonMappedXmlDriver());
        return xstream.fromXML(json);
	}
	
	private static class ListContainer {
	    private List objects;

        public List getObjects() {
            return objects;
        }

        public void setObjects(List objects) {
            this.objects = objects;
        }
	}
}
