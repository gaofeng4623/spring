package com.spring.aop.resolver.xml;

import java.lang.reflect.Method;

public class DefaultXmlPointCut extends AbstractXmlPointCut{

	@Override
	public void initXMLConfig(String expression) {
		
	}

	@Override
	public boolean matchClass(String className) {
		return false;
	}

	@Override
	public boolean matchMethod(Method method) {
		return false;
	}

}
