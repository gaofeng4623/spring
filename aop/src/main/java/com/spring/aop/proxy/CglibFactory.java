package com.spring.aop.proxy;

import java.util.List;

public interface CglibFactory {

	public Object getProxyBean(Object bean);
	
	public void setInterceptors(List<?> advisors);
	
	
	
}
