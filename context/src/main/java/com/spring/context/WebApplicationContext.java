package com.spring.context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.spring.beans.common.exception.BeanNotFoundException;
import com.spring.beans.common.utils.Consts;
import com.spring.beans.factory.IocBaseContext;
import com.spring.beans.factory.XmlBeanFactory;
import com.spring.beans.factory.context.ApplicationContext;

public class WebApplicationContext implements ApplicationContext {
	public static final String 
		ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE = "WebApplicationContext";
	private List handleMappings; //控制检索器
	private Map<String, Object> controllers;
	private List exceptionHandles;
	private List beanIdList;
	private XmlBeanFactory beanFactory;
	private IocBaseContext iocContext;
	
	public List getBeanIdList() {
		return beanIdList;
	}

	public void setBeanIdList(List beanIdList) {
		this.beanIdList = beanIdList;
	}

	public IocBaseContext getIocContext() {
		return iocContext;
	}

	public void setIocContext(IocBaseContext iocContext) {
		this.iocContext = iocContext;
	}
	
	public Map<String, Object> getControllers() {
		return controllers;
	}

	public void setControllers(Map<String, Object> controllers) {
		this.controllers = controllers;
	}

	public List getExceptionHandles() {
		return exceptionHandles;
	}

	public void setExceptionHandles(List exceptionHandles) {
		this.exceptionHandles = exceptionHandles;
	}

	public WebApplicationContext() {
		super();
		this.handleMappings = new ArrayList();
		this.iocContext = new IocBaseContext();
	}
	
	public XmlBeanFactory getBeanFactory() {
		return beanFactory;
	}

	public void setBeanFactory(XmlBeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	public List getHandleMappings() {
		return handleMappings;
	}
	
	public void addHandleMapping(Object handler) {
		this.handleMappings.add(handler);
	}
	
	
	/*****
	 * 从上下文中获取bean对象,参数key应改为Object类型
	 * *
	 * @throws Exception ******/
	public Object getBean(Object param) throws Exception {
		Object bean = this.getBeanFactory().getBean(param);
		if (bean == null) {
			throw new BeanNotFoundException("the bean " 
					+ param.toString() + " has not found");
		}
		return bean;
	}
	
	/****
	 * **容器销毁时调用***
	 * *******/
	public void destroy() {
		this.beanFactory.destroySingletons();
	}
	
	public boolean isSingleton(String scope) {
		return scope != null && !scope.equals(Consts.prototype);
	}

	
	/****
	 * 查找指定类型的bean
	 * @param <T>
	 * @param type
	 * @return
	 * @throws Exception 
	 */
	public <T> T getBean(Class<T> type) throws Exception {
		 Object bean = getBean((Object)type);
		 return type.cast(bean);
	}

	
	public <T> T getBean(String beanId, Class<T> type) throws Exception {
		Object bean = null;
		try {
			bean = getBean(beanId);
		} catch (BeanNotFoundException e) {
			e.printStackTrace();
		}
		return type.cast(bean);
	}
	
	public boolean isHandleMapping(String protoType) {
		try {
			Class<? extends Object> cls = Class.forName(protoType);
			return IocBaseContext.isHandleMapping(cls);
		} catch (ClassNotFoundException e) {
			return false;
		}
	}

}
