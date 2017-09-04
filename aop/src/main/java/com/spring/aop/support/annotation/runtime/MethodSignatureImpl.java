package com.spring.aop.support.annotation.runtime;

import java.lang.reflect.Method;

public class MethodSignatureImpl implements MethodSignature {
	private Object target;
	private Method method;
	private Class<?> returnType;
	private String[] parameterNames;
	private Class<?>[] parameterTypes;
	private Class<?>[] exceptionTypes;

	public MethodSignatureImpl(Object target, Method method, Class<?> returnType,
			String[] parameterNames, Class<?>[] parameterTypes,
			Class<?>[] exceptionTypes) {
		this.target = target;
		this.method = method;
		this.returnType = returnType;
		this.parameterNames = parameterNames;
		this.parameterTypes = parameterTypes;
		this.exceptionTypes = exceptionTypes;
	}
	
	@Override
	public Class<?> getReturnType() {
		return this.returnType;
	}

	@Override
	public Method getMethod() {
		return this.method;
	}
	
	@Override
	public Class<?>[] getParameterTypes() {
		return this.parameterTypes;
	}

	@Override
	public String[] getParameterNames() {
		return this.parameterNames;
	}

	@Override
	public Class<?>[] getExceptionTypes() {
		return this.exceptionTypes;
	}

	@Override
	public String toShortString() {
		return null;
	}

	@Override
	public String toLongString() {
		return null;
	}

	@Override
	public String getName() {
		return null;
	}

}
