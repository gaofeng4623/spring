package com.spring.aop.cglib;

import java.lang.reflect.Method;
import java.util.List;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import com.spring.aop.common.xml.inner.Advisor;
import com.spring.aop.support.ExcutionChain;
import com.spring.beans.factory.IocBaseContext;

public class CglibInterceptor implements MethodInterceptor{
	private Object target;
	private List<?> interceptors;
	
	

	public List<?> getInterceptors() {
		return interceptors;
	}


	public void setInterceptors(List<?> interceptors) {
		this.interceptors = interceptors;
	}


	public Object getTarget() {
		return target;
	}


	public void setTarget(Object target) {
		this.target = target;
	}


	public Object getInstance(Object target) {
		setTarget(target);
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(this.target.getClass());
		// 回调方法
		enhancer.setCallback(this);
		// 创建代理对象
		return  enhancer.create();
	}


	public Object intercept(Object obj, Method method, Object[] args,
			MethodProxy proxy) throws Throwable {
		Object result = null;
		if (getInterceptors() == null) return null;
		if (getInterceptors().size() == 1 
				&& IocBaseContext.isTransactionInterceptor(getInterceptors().get(0))) {
			Object transactionInterceptor = getInterceptors().get(0);
			if (IocBaseContext.matchesMethod(method, transactionInterceptor)) {
				result = IocBaseContext.execute(method, args, obj, transactionInterceptor);
			} else {
				result = method.invoke(obj, args); //常规运行
			}
		} else {
			Advisor advisor = null;
			ExcutionChain chain = new ExcutionChain();
			for (int i = 0; i < getInterceptors().size(); i++) {
				if (getInterceptors().get(i) == null) continue;
				advisor = (Advisor) getInterceptors().get(i);
				if (advisor.matchesMethod(method, target.getClass(), args)) {
					chain.fillChain(advisor.getAdvice());
				} 
			}
			result = chain.excuteChain(method, args, target);
		}
		
		return result;
	}

	public String toString() {
		return ".........";
	}
}
