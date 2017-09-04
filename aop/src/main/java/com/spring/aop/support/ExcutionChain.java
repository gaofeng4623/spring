package com.spring.aop.support;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.spring.aop.common.xml.inner.AfterReturningAdvice;
import com.spring.aop.common.xml.inner.MethodBeforeAdvice;
import com.spring.aop.common.xml.inner.MethodInterceptor;
import com.spring.aop.common.xml.inner.MethodInvocation;
import com.spring.aop.common.xml.inner.ThrowsAdvice;
import com.spring.beans.common.utils.Consts;

public class ExcutionChain {
	private List<MethodBeforeAdvice> methodBeforeChain;
	private List<AfterReturningAdvice> methodAfterChain;
	private List<MethodInterceptor> aroundChain;
	private List<ThrowsAdvice> throwsAdviceChain;
	
	public ExcutionChain() {
		this.methodBeforeChain = new ArrayList<MethodBeforeAdvice>();
		this.methodAfterChain = new ArrayList<AfterReturningAdvice>();
		this.aroundChain = new ArrayList<MethodInterceptor>();
		this.throwsAdviceChain = new ArrayList<ThrowsAdvice>();
	}

	public void fillChain(Object advice) {
		if (advice instanceof MethodBeforeAdvice) {
			methodBeforeChain.add((MethodBeforeAdvice)advice);
		} else if (advice instanceof AfterReturningAdvice) {
			methodAfterChain.add((AfterReturningAdvice)advice);
		} else if (advice instanceof MethodInterceptor) {
			aroundChain.add((MethodInterceptor)advice);
		} else if (advice instanceof ThrowsAdvice) {
			throwsAdviceChain.add((ThrowsAdvice)advice);
		}
	}

	public Object excuteChain(Method method, Object[] args, Object instance) {
		Object result = null;
		for (Iterator<MethodBeforeAdvice> it = methodBeforeChain.iterator(); it.hasNext();) {
			MethodBeforeAdvice mba = it.next();
			try {
				mba.before(method, args, instance);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (aroundChain.size() > 0) {
			MethodInvocation mic = new MethodInvocation(throwsAdviceChain,
					method, args, instance);
			mic.setTarget(instance);
			mic.setMethod(method);
			mic.setArguments(args);
			mic.setInterceptorStack(aroundChain);
			result = mic.proceed();
		} else {
			try {
				result = method.invoke(instance, args);
				for (Iterator<AfterReturningAdvice> it = methodAfterChain.iterator(); it.hasNext();) {
					AfterReturningAdvice ara = it.next();
					try {
						ara.afterReturning(result, method, args, instance);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} catch (Throwable e) {
				if (e instanceof InvocationTargetException)
					e = ((InvocationTargetException) e).getTargetException();
				doThrowAdvice(throwsAdviceChain, method, args, instance, e);
			}
		}
		return result;

	}

	public void doThrowAdvice(List<ThrowsAdvice> advices, Method md, Object[] args,
			Object obj, Throwable throwable) {
		if (throwable == null)
			return;
		Method[] methods = null;
		for (Iterator<ThrowsAdvice> it = advices.iterator(); it.hasNext();) {
			ThrowsAdvice ta = it.next();
			methods = ta.getClass().getDeclaredMethods();
			for (int i = 0; i < methods.length; i++) {
				Method throwmd = methods[i];
				parseMethod(throwmd, ta, md, args, obj, throwable);
			}
		}
	}

	public void parseMethod(Method throwmd, ThrowsAdvice ta, Method md,
			Object[] args, Object obj, Throwable throwable) {
		int count = 0;
		boolean hasThrowable = false;
		Class<?>[] classes = throwmd.getParameterTypes();
		if (classes == null) return;
		if (classes.length <= 4) {
			try {
				Object[] params = new Object[classes.length];
				for (int i = 0; i < classes.length; i++) {
					if (classes[i].getName().equals("java.lang.reflect.Method")) {
						count++;
						params[i] = md;
					} else if (classes[i].getName().equals(
							"[Ljava.lang.Object;")
							|| classes[i].getName().equals(
									"[Ljava.lang.String;")) {
						count++;
						params[i] = args;
					} else if (classes[i].getName().equals("java.lang.Object")) {
						count++;
						params[i] = obj;
					} else if (parseException(classes[i], throwable)) {
						count++;
						hasThrowable = true;
						params[i] = throwable;
					}
				}
				if (hasThrowable && classes.length == count)
					throwmd.invoke(ta, params);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	public boolean parseException(Class<?> cl, Throwable throwable) {
		if (cl == null)
			return false;
		if (cl.getName().equals(Consts.EXCEPTION))
			return true;
		return cl.getName().equals(throwable.getClass().getName());
	}
}
