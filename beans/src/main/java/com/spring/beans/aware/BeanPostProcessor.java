package com.spring.beans.aware;

public interface BeanPostProcessor {

	/**
	 * bean初始化前调用
	 * 
	 * @param bean
	 * @param beanName
	 */
	public Object postProcessBeforeInitialization(Object bean, String beanName);

	/**
	 * bean初始化后调用
	 * 
	 * @param bean
	 * @param beanName
	 */
	public Object postProcessAfterInitialization(Object bean, String beanName);

}
