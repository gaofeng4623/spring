package com.spring.mvc.common;

import javax.servlet.http.HttpServletRequest;

import com.spring.context.WebApplicationContext;
import com.spring.mvc.servlet.DispatcherServlet;

public class RequestContextUtils {

	public static WebApplicationContext getWebApplicationContext(
			HttpServletRequest request) {
		return (WebApplicationContext) request.getAttribute(DispatcherServlet.class.getName() + ".CONTEXT");
	}
}
