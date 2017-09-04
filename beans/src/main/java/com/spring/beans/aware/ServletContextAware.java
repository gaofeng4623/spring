package com.spring.beans.aware;

import javax.servlet.ServletContext;

public interface ServletContextAware {
	
	public void setServletContext(ServletContext context);
	
}
