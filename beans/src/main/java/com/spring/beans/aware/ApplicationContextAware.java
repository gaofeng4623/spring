package com.spring.beans.aware;

import com.spring.beans.factory.context.ApplicationContext;

public interface ApplicationContextAware {
	
	public void setApplicationContext(ApplicationContext context);
	
	public ApplicationContext getApplicationContext();
}
