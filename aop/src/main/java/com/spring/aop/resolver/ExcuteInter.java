package com.spring.aop.resolver;

import java.lang.reflect.Method;

public interface ExcuteInter {
	
	//解析XML配置
	public void initXMLConfig(String expression);
	
	//解析类中的配置,只解析单个excution
	public void initClassConfig(String expression);
	
	//匹配类型
	public boolean matchClass(String className);
	
	//匹配方法
	public boolean matchMethod(Method method);
	
}