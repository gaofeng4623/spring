package com.spring.aop.resolver.annotation;

import java.lang.reflect.Method;

import com.spring.aop.resolver.Matcher;

public class AnnotationWithin extends AbstractAnnotationExcuteInter implements Matcher {
	private boolean not = false; //当前表达式的外层运算 ！
	private String model = "normal"; //匹配的运算模式
	private Matcher[] classNameMatchers = null; // 类匹配器
	
	
	public AnnotationWithin(String expression) {
		initClassConfig(expression);
	}
	
	@Override
	public void initClassConfig(String expression) {
		System.out.println("within expression = " + expression);
		String[] classNameRegxs = null; //可能带有&&或||的类匹配条件
		//处理类匹配的运算符
		if (expression.contains("&&")) {
			model = "&&";
			classNameRegxs = expression.split("&&");
		} else if (expression.contains("||")) {
			model = "||";
			classNameRegxs = expression.split("||");
		} else {
			classNameRegxs = new String[]{expression};
		}
		classNameMatchers = getClassNameMatchers(classNameRegxs);
	}
	
	public boolean matched(Class cl, Method method) {
		return isNot() ? !matchClass(cl.getName()) 
				: matchClass(cl.getName());
	}

	/**
	 * 对外暴露接口，容器加载时匹配代理
	 */
	public boolean matchedClass(Class cl){ 
		return isNot() ? !matchClass(cl.getName())
				: matchClass(cl.getName());
	}
	
	public boolean matchClass(String className) {
		Matcher cnm = null;
		if ("normal".equals(model)) {
			cnm = classNameMatchers[0];
			return cnm.matchClass(className);
		} else if ("&&".equals(model)) {
			for (int i = 0 ; i < classNameMatchers.length; i++) {
				cnm = classNameMatchers[i];
				if (!cnm.matchClass(className))
					return false;
			}
			return true;
		} else if ("||".equals(model)){
			for (int i = 0 ; i < classNameMatchers.length; i++) {
				cnm = classNameMatchers[i];
				if (cnm.matchClass(className))
					return true;
			}
			return false;
		}
		return false;
	}
	
	
	
	public boolean matchMethod(Method method) {
		return true;
	}

	public boolean matchMethodParam(Class cl, Method method) {
		return false;
	}


	public boolean isNot() {
		return not;
	}

	public void setNot(boolean not) {
		this.not = not;
	}


	public Matcher[] getClassNameMatchers() {
		return classNameMatchers;
	}

	public void setClassNameMatchers(Matcher[] classNameMatchers) {
		this.classNameMatchers = classNameMatchers;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}
	
	
}
