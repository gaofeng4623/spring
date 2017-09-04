package com.spring.aop.cglib;

import java.lang.reflect.Method;
import java.util.List;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import com.spring.aop.support.annotation.AnnotationExcutionChain;
import com.spring.aop.support.annotation.AnnotationModel;

public class CglibAspectInterceptor implements MethodInterceptor{
	private Object target;
	private List<AnnotationModel> interceptors;

	public Object getTarget() {
		return target;
	}
	public void setTarget(Object target) {
		this.target = target;
	}

	public List<AnnotationModel> getInterceptors() {
		return interceptors;
	}
	public void setInterceptors(List<AnnotationModel> interceptors) {
		this.interceptors = interceptors;
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
		Object result = null;
		AnnotationModel advice = null;
		AnnotationExcutionChain chain = new AnnotationExcutionChain();
		if (getInterceptors() == null) return null;
		for (int i = 0; i < getInterceptors().size(); i++) {
			advice = getInterceptors().get(i);
			if (advice.matched(target.getClass(), method)) {
				chain.fillChain(advice);
			}
		}
		chain.setTransactionProxy(null); //事务预留接口,注入事务代理对象
		result = chain.excuteChain(this, method, args, target);
		return result;
	}
	
}
