package com.spring.aop.resolver.xml;

import java.lang.reflect.Method;

import com.spring.aop.resolver.ExcuteInter;
import com.spring.aop.resolver.Matcher;

public abstract class AbstractXmlPointCut implements ExcuteInter, Matcher {

	public void initClassConfig(String expression) {
	}

	public abstract void initXMLConfig(String expression);

	public abstract boolean matchClass(String className);

	public abstract boolean matchMethod(Method method);

	public boolean matched(Class<?> cl, Method method) {
		return false;
	}

	public boolean matchedClass(Class<?> cl) {
		return false;
	}

	public boolean matchMethodParam(Class<?> cl, Method method) {
		return false;
	}

}
