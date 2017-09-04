package com.spring.aop.support.annotation.runtime;

import java.lang.reflect.Method;

public interface MethodSignature extends CodeSignature{
	
	 Class<?> getReturnType();

	 Method getMethod();
}
