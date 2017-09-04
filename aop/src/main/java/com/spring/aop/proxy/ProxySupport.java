package com.spring.aop.proxy;

import com.spring.beans.aware.BeanFactoryAware;
import com.spring.beans.factory.BeanFactory;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public abstract class ProxySupport extends ProxyCore implements BeanFactoryAware{
	private boolean proxyTargetClass; //类的代理开关
	private List interceptorNames;
	private BeanFactory beanFactory;
	private CglibFactory cglib;
	
	public ProxySupport() {
	}
	
	@Override
	public void setTarget(Object bean) {
		Class[] interfaces = bean.getClass().getInterfaces(); // 获得其实现的接口类
		if (isProxyTargetClass() || noUsedInterface(interfaces)) {
			if (cglib == null) 
				cglib = new CglibBeanFactory();
			if (interceptorNames == null) return;
			cglib.setInterceptors(interceptorNames);
			this.setTargetProperty(cglib.getProxyBean(bean));
		} else {
			this.setTargetProperty(Proxy.newProxyInstance(
					bean.getClass().getClassLoader(), interfaces, this));
		}
		this.setObject(bean);
	}

	public List getInterceptorNames() {
		return interceptorNames;
	}

	public void setInterceptorNames(List interceptorNames) {
		List interceptors = new ArrayList();
		if (interceptorNames == null) return;
		for (Iterator it = interceptorNames.iterator();it.hasNext();) {
			String key = it.next().toString().trim();
			try {
				interceptors.add(beanFactory.getBean(key));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		this.interceptorNames = interceptors;
	}
	
	public abstract Object invoke(Object proxy, Method method, Object[] args)
		throws Throwable;

	public BeanFactory getBeanFactory() {
		return this.beanFactory;
	}
	
	public final void setBeanFactory(BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	public CglibFactory getCglib() {
		return cglib;
	}

	public void setCglib(CglibFactory cglib) {
		this.cglib = cglib;
	}

	public boolean isProxyTargetClass() {
		return proxyTargetClass;
	}

	public void setProxyTargetClass(boolean proxyTargetClass) {
		this.proxyTargetClass = proxyTargetClass;
	}
	
	private boolean noUsedInterface(Class[] interfaces) {
		return interfaces == null || interfaces.length == 0;
	}
}
