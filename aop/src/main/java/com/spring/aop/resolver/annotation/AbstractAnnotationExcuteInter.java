package com.spring.aop.resolver.annotation;

import com.spring.aop.resolver.ClassNameMatcher;
import com.spring.aop.resolver.ExcuteInter;
import com.spring.aop.resolver.Matcher;

public abstract class AbstractAnnotationExcuteInter implements ExcuteInter{

	public abstract void initClassConfig(String expression);
		

	public void initXMLConfig(String expression) {
		
	}
	
	
	//生成类型、接口匹配器
	public Matcher[] getClassNameMatchers(String[] matchers) {
		if (matchers == null || matchers.length == 0) return null;
		Matcher[] classMatchers = new ClassNameMatcher[matchers.length];
		for (int i = 0; i < matchers.length; i++ ) {
			String classNameRegx = matchers[i].trim();
			ClassNameMatcher cnmt = new ClassNameMatcher();
			if (classNameRegx.contains("!")) {
				cnmt.setNot(true);
				classNameRegx = classNameRegx.replace("!", "");
			}
			if (classNameRegx.contains("+")) {
				cnmt.setInterfaceModel(true);
				classNameRegx = classNameRegx.replace("+", "");
			}
			classNameRegx = classNameRegx.replace("*", ".*"); 
			classNameRegx = classNameRegx.replace("..", "(.|..*.)");
			cnmt.setClassNameRegx(classNameRegx);
			classMatchers[i] = cnmt;
		}
		return classMatchers;
	}
	
}
