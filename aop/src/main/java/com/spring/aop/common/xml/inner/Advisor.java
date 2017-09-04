package com.spring.aop.common.xml.inner;

import java.lang.reflect.Method;

public interface Advisor {
	public boolean matchesClass(String key, Class<?> clazz);
	public boolean matchesMethod(Method method, Class<?> targetClass, Object[] args);
	public Object getAdvice();
	public Object clone();
}
