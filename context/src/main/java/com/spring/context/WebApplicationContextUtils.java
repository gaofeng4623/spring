package com.spring.context;

import javax.servlet.ServletContext;

public class WebApplicationContextUtils {

	public static WebApplicationContext getWebApplicationContext(
			ServletContext context) {
		return (WebApplicationContext) context
				.getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
	}
}
