package com.spring.transaction.interceptor;

import java.lang.reflect.Method;

public class TransactionCglibFactory{
	private Object cglibInterceptor;

	public Object getCglibInterceptor() {
		return cglibInterceptor;
	}

	public void setCglibInterceptor(Object cglibInterceptor) {
		this.cglibInterceptor = cglibInterceptor;
	}
	
	public TransactionCglibFactory() {
		try {
			setCglibInterceptor(Class.forName(
					"com.spring.aop.cglib.CglibTransactionInterceptor").newInstance());
		} catch (Exception e) {
			System.err.println("the spring has not found com.spring.aop.cglib.CglibTransactionInterceptor");
		}
	}

	public Object getProxyBean(TransactionProxyFactoryBean transactionProxyFactoryBean, Object bean) {
		Object result = null;
		if (cglibInterceptor == null) return bean;
		try {
			Method[] methods = cglibInterceptor.getClass().getDeclaredMethods();
			for (int i= 0; i < methods.length; i++) {
				if ("getInstance".equals(methods[i].getName())) {
					result = methods[i].invoke(cglibInterceptor, bean);
				} else if ("setTransactionProxyFactoryBean".equals(methods[i].getName())) {
					methods[i].invoke(cglibInterceptor, transactionProxyFactoryBean);
				}
			}
		} catch (Exception e) {
			result = bean;
			System.err.println("the method getInstance of cglib can't invoke");
		}
		return result;
	}
	
}
