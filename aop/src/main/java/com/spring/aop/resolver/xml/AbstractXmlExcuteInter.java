package com.spring.aop.resolver.xml;

import com.spring.aop.resolver.ExcuteInter;

public abstract class AbstractXmlExcuteInter implements ExcuteInter {

	public void initClassConfig(String expression) {

	}

	public abstract void initXMLConfig(String expression);

}
