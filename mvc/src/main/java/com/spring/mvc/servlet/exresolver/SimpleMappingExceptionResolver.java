package com.spring.mvc.servlet.exresolver;

import java.util.Iterator;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.spring.mvc.common.ModelAndView;


public class SimpleMappingExceptionResolver extends AbstractHandlerExceptionResolver{

	private String defaultErrorView;
	private String exceptionAttribute;
	private Properties exceptionMappings;
	
	@Override
	public ModelAndView doResolveException(HttpServletRequest request,
			HttpServletResponse response, Object handler, Throwable ex) {
		if (exceptionMappings == null) return null;
		for (Iterator it = exceptionMappings.keySet().iterator();it.hasNext();) {
			String key = (String) it.next();
			if (key.equals(ex.getClass().getName()) 
				|| key.equals(ex.getClass().getSimpleName())) {
				return new ModelAndView(exceptionMappings.getProperty(key));
			}
		}
		return new ModelAndView(defaultErrorView);
	}

	@Override
	public void prepareResponse(Throwable ex, HttpServletRequest request) {
		if (exceptionAttribute != null) 
			request.setAttribute(exceptionAttribute, ex);
	}

	
	
	public String getDefaultErrorView() {
		return defaultErrorView;
	}

	public void setDefaultErrorView(String defaultErrorView) {
		this.defaultErrorView = defaultErrorView;
	}

	public String getExceptionAttribute() {
		return exceptionAttribute;
	}

	public void setExceptionAttribute(String exceptionAttribute) {
		this.exceptionAttribute = exceptionAttribute;
	}

	public Properties getExceptionMappings() {
		return exceptionMappings;
	}

	public void setExceptionMappings(Properties exceptionMappings) {
		this.exceptionMappings = exceptionMappings;
	}

}
