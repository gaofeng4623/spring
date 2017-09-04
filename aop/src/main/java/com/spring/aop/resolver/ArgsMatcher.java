package com.spring.aop.resolver;

import java.lang.reflect.Method;

import com.spring.beans.common.utils.ParameterNameDiscoverer;

public class ArgsMatcher extends AbstractArgsMatcher{
	private String paramName;
	
	public ArgsMatcher() {}
	
	public ArgsMatcher(String paramName) {
		this.paramName = paramName;
	}
	
	public String getParamName() {
		return paramName;
	}

	public void setParamName(String paramName) {
		paramName = paramName.replace("args", "").trim();
		paramName = paramName.replaceAll("\\(|\\)", "");
		this.paramName = paramName;
	}
	
	
	@Override
	public boolean matchMethodParam(Class cl, Method method) {
		String result = null;
		StringBuffer sb = new StringBuffer();
		if (paramName == null) return false;
		ParameterNameDiscoverer pnd = new ParameterNameDiscoverer();
		String[] parameters = pnd.getParameterNames(cl, method);
		if (parameters.length == 0) return false;
		if (parameters.length > 1) {
			for (String s : parameters) {
				sb.append(s).append(",");
			}
			result = sb.toString();
			result = result.substring(0, result.length() - 1);
		} else {
			result = parameters[0];
		}
		return this.paramName.equals(result);
	}
	
	
}
