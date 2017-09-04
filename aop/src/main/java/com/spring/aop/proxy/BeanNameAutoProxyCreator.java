package com.spring.aop.proxy;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import com.spring.aop.common.xml.advisor.PointcutAdvisor;
import com.spring.aop.common.xml.inner.Advisor;
import com.spring.aop.support.ExcutionChain;
import com.spring.beans.aware.BeanFactoryAware;
import com.spring.beans.factory.BeanFactory;
import com.spring.beans.factory.IocBaseContext;
import com.spring.beans.factory.XmlBeanFactory;

/**
 * 重写了父接口的两个重要方法
 * 
 * @author 高峰
 * 
 */
public class BeanNameAutoProxyCreator extends PointcutAdvisor implements
		BeanFactoryAware {
	private String[] beanNames;
	private List<String> interceptorNames;
	private List<Object> interceptors;
	private boolean proxyTargetClass;
	private BeanFactory beanFactory;
	private CglibBeanFactory cglib;
	private Object target;

	public String[] getBeanNames() {
		return beanNames;
	}

	public void setBeanNames(String[] beanNames) {
		this.beanNames = beanNames;
	}

	public List<String> getInterceptorNames() {
		return interceptorNames;
	}
	
	public List<?> getInterceptors() {
		return interceptors;
	}

	public void setInterceptors(List<Object> interceptors) {
		this.interceptors = interceptors;
	}

	public boolean isProxyTargetClass() {
		return proxyTargetClass;
	}

	public void setProxyTargetClass(boolean proxyTargetClass) {
		this.proxyTargetClass = proxyTargetClass;
	}

	public CglibBeanFactory getCglib() {
		return cglib;
	}

	public void setCglib(CglibBeanFactory cglib) {
		this.cglib = cglib;
	}

	/****
	 * 增加拦截器配置器
	 * 
	 * @param interceptorNames
	 */
	public void setInterceptorNames(List<String> interceptorNames) {
		List<Object> interceptors = new ArrayList<Object>();
		if (interceptorNames == null) return;
		this.interceptorNames = interceptorNames;
		for (Iterator<String> it = interceptorNames.iterator(); it.hasNext();) {
			String key = it.next().trim();
			Object object;
			try {
				object = beanFactory.getBean(key);
				interceptors.add(object);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		this.interceptors = interceptors;

	}

	@Override
	public boolean matchesClass(String key, Class<?> clazz) {
		if (beanNames == null)
			return false;
		for (String regx : beanNames) {
			if (matches(regx, key)) return true;
		}
		return false;
	}

	@Override
	public boolean matchesMethod(Method method, Class<?> targetClass, Object[] args) {
		return false;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		Object result = null;
		Advisor advisor = null;
		if (getInterceptorNames() == null)
			return null;
		if (getInterceptors().size() == 1 
				&& IocBaseContext.isTransactionInterceptor(getInterceptors().get(0))) {
			Object transactionInterceptor = getInterceptors().get(0);
			if (IocBaseContext.matchesMethod(method, transactionInterceptor)) {
				result = IocBaseContext.execute(method, args, getObject(), transactionInterceptor);
			} else {
				result = method.invoke(getObject(), args); //常规运行
			}
		} else {
			ExcutionChain chain = new ExcutionChain();
			for (int i = 0; i < getInterceptors().size(); i++) {
				if (getInterceptors().get(i) == null) continue;
				advisor = (Advisor) getInterceptors().get(i);
				if (advisor.matchesMethod(method, getObject().getClass(), args)) {
					chain.fillChain(advisor.getAdvice());
				}
			}
			result = chain.excuteChain(method, args, getObject());
		}

		return result;
	}

	public void setBeanFactory(BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	@Override
	public Object getTarget() {
		return this.target;
	}

	@Override
	public void setTarget(Object target) {
		Class<?>[] interfaces = target.getClass().getInterfaces(); // 获得其实现的接口类
		if (isProxyTargetClass() || noUsedInterface(interfaces)) {
			if (cglib == null)
				cglib = new CglibBeanFactory();
			if (this.interceptors == null) return;
			cglib.setInterceptors(interceptors);
			this.target = cglib.getProxyBean(target);
		} else {
			this.target = Proxy.newProxyInstance(target.getClass()
					.getClassLoader(), interfaces, this);
		}
		setObject(target);

	}

	private boolean noUsedInterface(Class<?>[] interfaces) {
		return interfaces == null || interfaces.length == 0;
	}

	// 通配符转化成正则表达式
	public boolean matches(String regx, String name) {
		if (regx.contains("*")) {
			regx = regx.replace("*", ".*");
		}
		return Pattern.matches(regx, name);
	}
}
