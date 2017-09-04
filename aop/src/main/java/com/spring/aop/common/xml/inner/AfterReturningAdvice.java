package com.spring.aop.common.xml.inner;
import java.lang.reflect.Method;
public interface AfterReturningAdvice extends Advice{
	//value是返回值
	public void afterReturning(Object value, Method method, Object[] args, Object instance);
}
