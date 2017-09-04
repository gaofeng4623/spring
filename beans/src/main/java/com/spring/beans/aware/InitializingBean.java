package com.spring.beans.aware;

public interface InitializingBean {
	public void afterPropertiesSet() throws Exception;
}
