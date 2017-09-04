package com.spring.mvc.common;

import java.util.HashMap;
import java.util.Map;

import com.spring.beans.common.utils.Consts;

public class ParameterAndDefualtMethod {

	public static ThreadLocal<Map<String, Object>> threadLocal = new ThreadLocal<Map<String, Object>>();

	public static void put(String name, Object object) {
		Map<String, Object> data = threadLocal.get();
		if (data == null) {
			data = new HashMap<String, Object>();
			threadLocal.set(data);
		}
		data.put(name, object);
	}

	public static String getParamName() {
		Map<String, Object> data = threadLocal.get();
		return data.get(Consts.paramName).toString();
	}

	public static String getDefaultMethodName() {
		Map<String, Object> data = threadLocal.get();
		return data.get(Consts.defaultMethodName).toString();
	}

}
