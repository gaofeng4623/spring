package com.spring.aop.common.xml.inner;
//环绕通知的定义接口,源自aopalliance(Aop联盟)
public interface MethodInterceptor {
	 public Object invoke(MethodInvocation methodInvocation) throws Throwable;
}
