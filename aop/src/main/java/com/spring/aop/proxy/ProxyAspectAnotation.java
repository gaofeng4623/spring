package com.spring.aop.proxy;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

import com.spring.aop.support.annotation.AnnotationExcutionChain;
import com.spring.aop.support.annotation.AnnotationModel;
import com.spring.beans.factory.IocBaseContext;

public class ProxyAspectAnotation extends ProxySupport{
	private boolean aspectCglibable;
	private Object result;
	private List<AnnotationModel> advices;
	private Object object;

	
	@Override
	public void setTarget(Object bean) {
		Class<?>[] interfaces = bean.getClass().getInterfaces(); // 获得其实现的接口类
		if (isAspectCglibable() || noUsedInterface(interfaces)) {
			if (getCglib() == null) 
				setCglib(new CglibAspectFactory());
			if (this.advices == null) return;
			getCglib().setInterceptors(advices);
			this.result = getCglib().getProxyBean(bean);
		} else {
			this.result = Proxy.newProxyInstance(
					bean.getClass().getClassLoader(), interfaces, this);
		}
		this.object = bean;
	}
	
	
	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		Object result = null;
		AnnotationModel advice = null;
		AnnotationExcutionChain chain = new AnnotationExcutionChain();
		if (getAdvices() == null) return null;
		for (int i = 0; i < getAdvices().size(); i++) {
			advice = getAdvices().get(i);
			if (advice.matched(object.getClass(), method)) {
				chain.fillChain(advice);
			}
		}
		createTransactionProxy(getObject(), chain); //注入事务代理对象
		result = chain.excuteChain(proxy, method, args, object);
		return result;
	}
	
	/**
	 * 事务代理接口
	 * @param object
	 * @param method
	 */
	private void createTransactionProxy(Object object, AnnotationExcutionChain chain) {
		//需要判断是否开启事务注解代理
		Object result = IocBaseContext.createTransactionProxy(object, getBeanFactory());
		chain.setTransactionProxy(result);  //注入事务代理
	}


	public List<AnnotationModel> getAdvices() {
		return advices;
	}
	
	public void setAdvices(List<AnnotationModel> advices) {
		this.advices = advices;
	}
	
	public Object getResult() {
		return result;
	}

	public boolean isAspectCglibable() {
		return aspectCglibable;
	}


	public void setAspectCglibable(boolean aspectCglibable) {
		this.aspectCglibable = aspectCglibable;
	}

	private boolean noUsedInterface(Class[] interfaces) {
		return interfaces == null || interfaces.length == 0;
	}
	
}
