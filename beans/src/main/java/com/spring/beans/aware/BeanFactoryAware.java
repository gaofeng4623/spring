package com.spring.beans.aware;

import com.spring.beans.factory.BeanFactory;

public interface BeanFactoryAware {
	
	public void setBeanFactory(BeanFactory beanFactory);
}
