package com.spring.aop.support.annotation;

import java.lang.reflect.Method;

public abstract class AnnotationModel {
	private Object aspect;
	private Method method; //切面中的方法
	private String argNames;
	private AbstractAnnotationPointCut pointCut; //注解或xml切入点
	
	public Object getAspect() {
		return aspect;
	}
	public void setAspect(Object aspect) {
		this.aspect = aspect;
	}
	public Method getMethod() {
		return method;
	}
	public void setMethod(Method method) {
		this.method = method;
	}
	public String getArgNames() {
		return argNames;
	}
	public void setArgNames(String argNames) {
		this.argNames = argNames;
	}
	
	public AbstractAnnotationPointCut getPointCut() {
		return pointCut;
	}
	public void setPointCut(AbstractAnnotationPointCut pointCut) {
		this.pointCut = pointCut;
	}
	
	
	public boolean matched(Class<?> target, Method method) {
		return getPointCut().matched(target, method);
	}
	
	/**
	 * 对外提供接口，容器加载时匹配代理
	 */
	public boolean matchedClass(Class<?> cl) {
		return getPointCut().matchedClass(cl);
	}
}
