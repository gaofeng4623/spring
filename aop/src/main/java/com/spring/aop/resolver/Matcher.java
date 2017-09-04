package com.spring.aop.resolver;

import java.lang.reflect.Method;

public interface Matcher {
	public boolean matchClass(String className);
	public boolean matchMethod(Method method);
	public boolean matched(Class<?> cl, Method method);
	public boolean matchedClass(Class<?> cl); //容器加载时对外判断是否匹配类
	public boolean matchMethodParam(Class<?> cl, Method method);
	
}
