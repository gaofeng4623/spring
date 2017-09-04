package com.spring.mvc.common;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public class ModelMap {
	private Map<String, Object> parameters;

	public Map getParameters() {
		return parameters;
	}

	public void setParameters(Map parameters) {
		this.parameters = parameters;
	}
	
	public ModelMap() {
		this.parameters = new HashMap();
	}
	
	public void setAttribute(String name, Object value) {
		this.parameters.put(name, value);
	}
	
	public Object getAttribute(String key) {
		return this.parameters.get(key);
	}
	
	public void fillRequestAttributes(HttpServletRequest request) {
		for (Iterator it = parameters.keySet().iterator(); it.hasNext();) {
			String key = (String) it.next();
			request.setAttribute(key, parameters.get(key));
		}
	}
}
