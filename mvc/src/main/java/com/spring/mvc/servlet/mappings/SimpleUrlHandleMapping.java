package com.spring.mvc.servlet.mappings;

import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

public class SimpleUrlHandleMapping implements HandleMapping{
	private Properties urlMap;

	public Properties getMappings() {
		return urlMap;
	}

	public void setMappings(Properties mappings) {
		this.urlMap = mappings;
	}
	
	public String lookupHandler(String path, HttpServletRequest request) {
		return this.urlMap.getProperty(path);
	}
}
