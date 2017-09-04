package com.spring.aop.resolver;

import java.lang.reflect.Method;

public abstract class AbstractClassNameMatcher implements Matcher{
	
	public abstract boolean matchClass(String className);
	
	public boolean matchMethod(Method method) {
		return false;
	}
	
	public boolean matched(Class cl, Method method) {
		return false;
	}
	
	public boolean matchedClass(Class cl){ 
		return false;
	}
	
	public boolean matchMethodParam(Class cl, Method method) {
		return false;
	}
}
