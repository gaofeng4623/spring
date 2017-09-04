package com.spring.aop.common.xml.advisor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import com.spring.aop.common.xml.inner.Advisor;
import com.spring.aop.common.xml.inner.Pointcut;
import com.spring.aop.support.ExcutionChain;

public abstract class PointcutAdvisor implements Advisor, InvocationHandler,
		Cloneable {
	private Pointcut pointcut;
	private Object advice;
	private Object object;
	
	public Pointcut getPointcut() {
		return pointcut;
	}

	public void setPointcut(Pointcut pointcut) {
		this.pointcut = pointcut;
	}
	
	public Object getAdvice() {
		return advice;
	}

	public void setAdvice(Object advice) {
		this.advice = advice;
	}

	public Object getObject() {
		return object;
	}

	public void setObject(Object object) {
		this.object = object;
	}

	public abstract void setTarget(Object target);
	
	public abstract Object getTarget();

	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}

	public boolean matchesClass(String key, Class<?> clazz) {
		return false;
	}
	
	public abstract boolean matchesMethod(Method method, Class<?> targetClass,
			Object[] args);
	
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		Object result = null;
		ExcutionChain chain = new ExcutionChain();
		if (matchesMethod(method, object.getClass(), args)) {
			chain.fillChain(advice);
			result = chain.excuteChain(method, args, object);
		} else 
			return method.invoke(object, args);
		return result;
	}

}
