package com.spring.aop.common.xml.inner;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import com.spring.aop.support.ExcutionChain;

public class MethodInvocation {
	private Iterator interceptorStack;
	private Method method;
	private Object[] arguments;
	private Object target;
	private Object result;
	private List throwAdvisors;
	private Method throwMethod;
	private Object[] throwArgs;
	private Object instance;

	/****
	 * 导入异常通知所需的元素
	 * 
	 * @param advisors
	 * @param throwMethod
	 * @param throwArgs
	 * @param instance
	 */
	public MethodInvocation(List throwAdvisors, Method throwMethod,
			Object[] throwArgs, Object instance) {
		this.throwAdvisors = throwAdvisors;
		this.throwMethod = throwMethod;
		this.throwArgs = throwArgs;
		this.instance = instance;
	}

	public Object getResult() {
		return result;
	}

	public Object getTarget() {
		return target;
	}

	public void setTarget(Object target) {
		this.target = target;
	}

	private Iterator getInterceptorStack() {
		return interceptorStack;
	}

	public void setInterceptorStack(List interceptorStack) {
		this.interceptorStack = interceptorStack.iterator();
	}

	public Method getMethod() {
		return method;
	}

	public void setMethod(Method method) {
		this.method = method;
	}

	public Object[] getArguments() {
		return arguments;
	}

	public void setArguments(Object[] arguments) {
		this.arguments = arguments;
	}

	public Object proceed() {
		try {
			if (interceptorStack.hasNext()) {
				MethodInterceptor interceptor = (MethodInterceptor) interceptorStack
						.next();
				interceptor.invoke(this);
			} else {
				try {
					if (method != null && target != null)
						this.result = method.invoke(target, this.arguments);
					} catch (Throwable e) {
						if (e instanceof InvocationTargetException)
							e = ((InvocationTargetException) e).getTargetException();
						new ExcutionChain().doThrowAdvice(throwAdvisors, throwMethod,
							throwArgs, instance, e);
				 }
			}
		} catch (Throwable e) {}
		return result;
	}
}
