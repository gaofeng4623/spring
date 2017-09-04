package com.spring.aop.common.xml.advisor;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

import com.spring.aop.proxy.CglibBeanFactory;

public class DefaultPointcutAdvisor extends PointcutAdvisor{
	private boolean proxyTargetClass;
	private CglibBeanFactory cglib;
	private Object target;
	
	
	public CglibBeanFactory getCglib() {
		return cglib;
	}

	public void setCglib(CglibBeanFactory cglib) {
		this.cglib = cglib;
	}
	
	public boolean isProxyTargetClass() {
		return proxyTargetClass;
	}

	public void setProxyTargetClass(boolean proxyTargetClass) {
		this.proxyTargetClass = proxyTargetClass;
	}

	/*** 通过beanFactory匹配 ***/
	@Override
	public boolean matchesClass(String key, Class clazz) {
		return getPointcut() != null && getPointcut().matchesClass(clazz);
	}
	
	@Override
	public boolean matchesMethod(Method method, Class targetClass, Object[] args) {
		return getPointcut().matchesMethod(method, targetClass, args);
	}

	@Override
	public Object getTarget() {
		return this.target;
	}

	@Override
	public void setTarget(Object target) {
		Class[] interfaces = target.getClass().getInterfaces(); // 获得其实现的接口类
		if (isProxyTargetClass() || noUsedInterface(interfaces)) {
			if (cglib == null) 
				cglib = new CglibBeanFactory();
			List advisors = new ArrayList();
			advisors.add(this);
			cglib.setInterceptors(advisors);
			this.target = cglib.getProxyBean(target); 
		} else {
			this.target = Proxy.newProxyInstance(
					target.getClass().getClassLoader(), interfaces, this);
		}
		setObject(target);
	}
	
	private boolean noUsedInterface(Class[] interfaces) {
		return interfaces == null || interfaces.length == 0;
	}
}
