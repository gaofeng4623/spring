package com.spring.aop.cglib;

import java.lang.reflect.Method;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;


public class CglibTransactionInterceptor implements MethodInterceptor{
	private Object target;
	private Object transactionProxyFactoryBean;

	public Object getTarget() {
		return target;
	}
	public void setTarget(Object target) {
		this.target = target;
	}

	public Object getTransactionProxyFactoryBean() {
		return transactionProxyFactoryBean;
	}

	public void setTransactionProxyFactoryBean(
			Object transactionProxyFactoryBean) {
		this.transactionProxyFactoryBean = transactionProxyFactoryBean;
	}


	public Object getInstance(Object target) {
		setTarget(target);
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(this.target.getClass());
		// 回调方法
		enhancer.setCallback(this);
		// 创建代理对象
		return  enhancer.create();
	}


	public Object intercept(Object obj, Method method, Object[] args,
			MethodProxy proxy) throws Throwable {
		Method transactionManage = transactionProxyFactoryBean.getClass()
				.getMethod("transactionManage", new Class<?>[] {method.getClass(), args.getClass()});
		return transactionManage.invoke(transactionProxyFactoryBean, new Object[]{method, args});
	}

}
