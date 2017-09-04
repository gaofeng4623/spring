package com.spring.aop.resolver.xml;

import java.lang.reflect.Method;

import com.spring.aop.resolver.Matcher;

public class XmlExecution extends AbstractXmlExcuteInter implements Matcher{

	@Override
	public void initXMLConfig(String expression) {
		// TODO Auto-generated method stub
		
	}

	public boolean matchClass(String className) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public boolean matchMethod(Method method) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean matchMethodParam(Class cl, Method method) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean matched(Class cl, Method method) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean matchedClass(Class cl) {
		// TODO Auto-generated method stub
		return false;
	}

}
