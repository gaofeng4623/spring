package com.spring.aop.common.xml.pointcut;

import java.lang.reflect.Method;

import com.spring.aop.common.xml.inner.ClassFilter;
import com.spring.aop.common.xml.inner.MethodMatcher;
import com.spring.aop.common.xml.inner.Pointcut;

public abstract class AbstractPointcut implements Pointcut{

	public ClassFilter getClassFilter() {
		return null;
	}

	public MethodMatcher getMethodMatcher() {
		return null;
	}

	public boolean matchesClass(Class<?> clazz) {
		return false;
	}

	public abstract boolean matchesMethod(Method method, Class<?> targetClass, Object[] args);

}
