package com.spring.aop.proxy;

import java.lang.reflect.Method;
import java.util.List;


//可以写到配置中
public class CglibBeanFactory implements CglibFactory{
	private Object cglibInterceptor;

	public Object getCglibInterceptor() {
		return cglibInterceptor;
	}

	public void setCglibInterceptor(Object cglibInterceptor) {
		this.cglibInterceptor = cglibInterceptor;
	}
	
	public CglibBeanFactory() {
		try {
			setCglibInterceptor(Class.forName("com.spring.aop.cglib.CglibInterceptor").newInstance());
		} catch (Exception e) {
			System.err.println("the spring has not found com.spring.aop.cglib.CglibInterceptor");
		}
	}
	
	public Object getProxyBean(Object bean) {
		Object result = null;
		if (cglibInterceptor == null) return bean;
		try {
			Method[] methods = cglibInterceptor.getClass().getDeclaredMethods();
			for (int i= 0; i < methods.length; i++) {
				if ("getInstance".equals(methods[i].getName()))
					result = methods[i].invoke(cglibInterceptor, bean);
			}
		} catch (Exception e) {
			result = bean;
			System.err.println("the method getInstance of cglib can't invoke");
		}
		return result;
	}
	
	public void setInterceptors(List<?> advisors) {
		if (cglibInterceptor == null) return;
		try {
			Method[] methods = cglibInterceptor.getClass().getDeclaredMethods();
			for (int i= 0; i < methods.length; i++) {
				if ("setInterceptors".equals(methods[i].getName()))
					methods[i].invoke(cglibInterceptor, advisors);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("the method setInterceptors of cglib can't invoke");
		}
	}
	
}
