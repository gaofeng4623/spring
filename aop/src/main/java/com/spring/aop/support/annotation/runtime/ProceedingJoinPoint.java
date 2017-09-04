package com.spring.aop.support.annotation.runtime;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ProceedingJoinPoint implements JoinPoint{
	private Object proxy;
	private Object target;
	private Method targetMethod;
	private boolean hasArgs;
	private Object[] args;
	private Signature signature;

	public void setArgs(Object[] args) {
		this.args = args;
	}
	
	public Object getTarget() {
		return target;
	}

	public void setTarget(Object target) {
		this.target = target;
	}

	public boolean hasArgs() {
		return hasArgs;
	}

	public void setHasArgs(String[] paramNames) {
		this.hasArgs = (paramNames != null && paramNames.length > 0);
	}

	public ProceedingJoinPoint(){}
	
	public ProceedingJoinPoint(Signature signature, Object target, Object proxy, Method targetMethod, Object[] args) {
		this.signature = signature;
		this.proxy = proxy;
		this.target = target;
		this.args = args;
		this.targetMethod = targetMethod;
	}
	
	public Object[] getArgs() {
		return this.args;
	}
	
	//aspect中用户手工传入参数调用
	public Object proceed(Object[] args) throws Throwable{
		try {
			Object result = hasArgs() ? targetMethod.invoke(target, args)
				: targetMethod.invoke(target);
			return result;
				
		} catch (Throwable e) {
			if (e instanceof InvocationTargetException)
				e = ((InvocationTargetException) e).getTargetException(); 
			throw e;
		}
	}
	
	//手工执行代理对象中无参数的方法调用
	public Object proceed() throws Throwable{
		try {
			if (hasArgs()) 
				System.err.println("the method " 
				   + targetMethod.getName() + " must input args");
			return (!hasArgs()) ? targetMethod.invoke(target) : null;
		} catch (Throwable e) {
			if (e instanceof InvocationTargetException)
				e = ((InvocationTargetException) e).getTargetException(); 
			throw e;
		}
	}

	@Override
	public Object getThis() {
		return this.proxy;
	}

	@Override
	public Signature getSignature() {
		return this.signature;
	}

	@Override
	public String toShortString() {
		return target.getClass().getSimpleName();
	}

	@Override
	public String toLongString() {
		return target.getClass().getName();
	}
	
}
