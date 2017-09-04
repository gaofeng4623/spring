package com.spring.aop.common.xml.inner;
import java.lang.reflect.Method;
public interface MethodBeforeAdvice extends Advice{
	public Object before(Method method, Object[] args, Object instance);
}
