package com.spring.mvc.common;

public class ParameterMethodResolver {
	private String paramName;
	private String defaultMethodName;
	
	public String getParamName() {
		return paramName;
	}
	public void setParamName(String paramName) {
		this.paramName = paramName;
	}
	public String getDefaultMethodName() {
		return defaultMethodName;
	}
	public void setDefaultMethodName(String defaultMethodName) {
		this.defaultMethodName = defaultMethodName;
	}
	
}
