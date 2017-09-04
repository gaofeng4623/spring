package com.spring.aop.common.xml.advisor;

import java.lang.reflect.Method;
/**
 * 所有无需实现动态代理的advisor都应继承该适配器
 * @author lenovo
 *
 */
public abstract class AdvisorAdapter extends PointcutAdvisor{

	@Override
	public Object getTarget() {
		return null;
	}

	@Override
	public abstract boolean matchesMethod(Method method, Class<?> targetClass, Object[] args);

	@Override
	public void setTarget(Object target) {
	}

}
