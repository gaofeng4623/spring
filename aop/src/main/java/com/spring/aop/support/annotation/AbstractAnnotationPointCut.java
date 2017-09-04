package com.spring.aop.support.annotation;

import java.lang.reflect.Method;

import com.spring.aop.resolver.ExcuteInter;
import com.spring.aop.resolver.Matcher;

public abstract class AbstractAnnotationPointCut implements ExcuteInter, Matcher{

	public abstract void initClassConfig(String expression);

	public void initXMLConfig(String expression) {}
	
	public boolean matchClass(String className) {
		return false;
	}
	public boolean matchMethod(Method method) {
		return false;
	}
	
	public abstract boolean matched(Class<?> cl, Method method);
	
	public abstract boolean matchedClass(Class<?> cl);

	public boolean matchMethodParam(Class<?> cl, Method method) {
		return false;
	}

}
