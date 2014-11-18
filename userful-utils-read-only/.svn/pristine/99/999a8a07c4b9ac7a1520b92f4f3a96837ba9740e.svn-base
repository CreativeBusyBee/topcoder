package com.utils.json;

import java.io.IOException;

import org.codehaus.jackson.map.ObjectMapper;

/**
 * 这里是解析JSON数据的工具类，目的是把JSON数据变成对象使用�?
 * @author LZ
 */
public class JSONHelper {
	/**
	 * 解析JSON格式数据为JSONStepConfig对象
	 * 这里使用的是：Jackson
	 * @param json - 待解析数�?
	 * @return
	 * @throws IOException
	 */
	public static JSONStepConfig getStepConfig(String json) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(json, JSONStepConfig.class);
	}
}
