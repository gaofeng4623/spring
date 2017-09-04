package com.spring.aop.support.annotation;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.spring.aop.support.annotation.runtime.JoinPoint;
import com.spring.aop.support.annotation.runtime.JoinPointImpl;
import com.spring.aop.support.annotation.runtime.MethodSignatureImpl;
import com.spring.aop.support.annotation.runtime.ProceedingJoinPoint;
import com.spring.aop.support.annotation.runtime.Signature;
import com.spring.beans.common.utils.ParameterNameDiscoverer;

public class AnnotationExcutionChain {
	private List<BeforeAnnotationModel> methodBeforeChain;
	private List<AfterAnnotationModel> methodAfterChain;
	private List<AroundAnnotationModel> aroundChain;
	private List<AfterThrowingAnnotationModel> throwsAdviceChain;
	private List<AfterReturningAnnotationModel> returningChain;
	private Object transactionProxy; //事务代理bean
	
	public Object getTransactionProxy() {
		return transactionProxy;
	}

	public void setTransactionProxy(Object transactionProxy) {
		this.transactionProxy = transactionProxy;
	}

	public AnnotationExcutionChain() {
		this.methodBeforeChain = new ArrayList<BeforeAnnotationModel>();
		this.methodAfterChain = new ArrayList<AfterAnnotationModel>();
		this.aroundChain = new ArrayList<AroundAnnotationModel>();
		this.throwsAdviceChain = new ArrayList<AfterThrowingAnnotationModel>();
		this.returningChain = new ArrayList<AfterReturningAnnotationModel>();
	}
	
	public void fillChain(Object advice) {
		if (advice instanceof BeforeAnnotationModel) {
			this.methodBeforeChain.add((BeforeAnnotationModel)advice);
		} else if (advice instanceof AfterAnnotationModel) {
			this.methodAfterChain.add((AfterAnnotationModel)advice);
		} else if (advice instanceof AroundAnnotationModel) {
			this.aroundChain.add((AroundAnnotationModel)advice);
		} else if (advice instanceof AfterThrowingAnnotationModel) {
			this.throwsAdviceChain.add((AfterThrowingAnnotationModel)advice);
		} else if (advice instanceof AfterReturningAnnotationModel) {
			this.returningChain.add((AfterReturningAnnotationModel)advice);
		}
		
	}
	
	public Object excuteChain(Object proxy, Method method, Object[] args, Object instance) {
		/******连接点对象将被拦截方法与通知连接到一起******/
		Object result = null;
		Class<?> returnType = method.getReturnType();
		ParameterNameDiscoverer pnd = new ParameterNameDiscoverer();
		String[] parameterNames = pnd.getParameterNames(instance.getClass(), method);
		Class<?>[] parameterTypes = method.getParameterTypes();
		Class<?>[] exceptionTypes = method.getExceptionTypes();
		Signature signature = new MethodSignatureImpl(instance, method, returnType, parameterNames, parameterTypes, exceptionTypes);
		JoinPoint joinPoint = new JoinPointImpl(signature, proxy, instance, args);
		Object transactionProxy = getTransactionTargetProxy(instance); 	//事务代理
		doBeforeAdvice(joinPoint, methodBeforeChain, method, instance); //前置通知
		if (aroundChain.size() > 0) {
			ProceedingJoinPoint pjp = new ProceedingJoinPoint(signature, instance, transactionProxy, method, args);
			AroundAnnotationModel aroud = aroundChain.get(0);
			try {
				result = aroud.around(pjp, instance, method);
				doReturningAdvice(joinPoint, returningChain, method, instance, result); //返回通知
				doAfterAdvice(joinPoint, methodAfterChain, method, instance); //后置通知
			} catch (Throwable e) {
				doThrowAdvice(joinPoint, throwsAdviceChain, method, instance, e); //异常通知
			}
		} else {
			try {
				result = method.invoke(transactionProxy, args);
				doReturningAdvice(joinPoint, returningChain, method, instance, result); //返回通知
				doAfterAdvice(joinPoint, methodAfterChain, method, instance); //后置通知
			} catch (Throwable e) {
				doThrowAdvice(joinPoint, throwsAdviceChain, method, instance, e); //异常通知
			} 
		}
		return result;
	}
	
	//创建事务代理
	private Object getTransactionTargetProxy(Object instance) {
		return getTransactionProxy() == null ? instance : getTransactionProxy() ;
	}

	public void doBeforeAdvice(JoinPoint joinPoint, List<BeforeAnnotationModel> advices, Method method, Object instance) {
		for (Iterator<BeforeAnnotationModel> it = advices.iterator(); it.hasNext();) {
			BeforeAnnotationModel before = it.next();
			before.before(joinPoint, instance, method);
		}
	}
	
	public void doReturningAdvice(JoinPoint joinPoint, List<AfterReturningAnnotationModel> advices,
			Method method, Object instance, Object returnValue) {
		for (Iterator<AfterReturningAnnotationModel> it = advices.iterator(); it.hasNext();) {
			AfterReturningAnnotationModel ara = it.next();
			ara.afterReturning(joinPoint,instance, method, returnValue);
		}
	}
	
	
	public void doAfterAdvice(JoinPoint joinPoint, List<AfterAnnotationModel> advices, Method method, Object instance) {
		for (Iterator<AfterAnnotationModel> it = advices.iterator(); it.hasNext();) {
			AfterAnnotationModel after = it.next();
			after.after(joinPoint, instance, method);
		}
	}
	
	
	public void doThrowAdvice(JoinPoint joinPoint, List<AfterThrowingAnnotationModel> advices, Method method,
			Object instance, Throwable e) {
		if (e instanceof InvocationTargetException)
			e = ((InvocationTargetException) e).getTargetException();
		for (Iterator<AfterThrowingAnnotationModel> it = advices.iterator(); it.hasNext();) {
			AfterThrowingAnnotationModel ata = it.next();
			ata.afterThrowing(joinPoint, method, instance, e);
		}
	}
}
