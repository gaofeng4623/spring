package com.spring.aop.common.xml.inner;

import java.lang.reflect.Method;

public interface Pointcut {
	
	public ClassFilter getClassFilter();
	
	public MethodMatcher getMethodMatcher();
	
	public boolean matchesClass(Class<?> clazz);
	
	public boolean matchesMethod(Method method, Class<?> targetClass, Object[] args);
	
}
