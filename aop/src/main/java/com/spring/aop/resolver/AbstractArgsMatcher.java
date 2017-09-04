package com.spring.aop.resolver;

import java.lang.reflect.Method;

public abstract class AbstractArgsMatcher implements Matcher{

	public boolean matchClass(String className) {
		return false;
	}

	public boolean matchMethod(Method method) {
		return false;
	}

	public boolean matched(Class cl, Method method) {
		return false;
	}
	
	public boolean matchedClass(Class cl){ 
		return false;
	}
	
	public abstract boolean matchMethodParam(Class cl, Method method);

}
