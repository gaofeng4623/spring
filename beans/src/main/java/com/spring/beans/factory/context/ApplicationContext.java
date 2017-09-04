package com.spring.beans.factory.context;

import com.spring.beans.factory.BeanFactory;

public interface ApplicationContext {
	
	public abstract Object getBean(Object param) throws Exception;
	
	public <T> T getBean(Class<T> type) throws Exception;
	
	public abstract void destroy();
	
	public BeanFactory getBeanFactory();
	
}
