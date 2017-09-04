package com.spring.aop.support.annotation;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.spring.aop.common.annotation.After;
import com.spring.aop.common.annotation.AfterReturning;
import com.spring.aop.common.annotation.AfterThrowing;
import com.spring.aop.common.annotation.Around;
import com.spring.aop.common.annotation.Aspect;
import com.spring.aop.common.annotation.Before;

/**
 * AOP切面核心功能
 * @author Administrator
 *
 */
public class AspectCoreService {
	
	
	/***
	 * 筛选aspect切面对象
	 * @param set
	 * @param pointcuts
	 * @return
	 */
	public List<Class<?>> getAspectClasses(Set<Class<?>> set, Map<Object, AbstractAnnotationPointCut> globalPointcuts) {
		Class<?> cl = null;
		List<Class<?>> total = new ArrayList<Class<?>>();
		for (Iterator<Class<?>> it = set.iterator(); it.hasNext();) {
			cl = it.next();
			if (cl.isAnnotationPresent(Aspect.class)) {
				total.add(cl);
				globalPointcuts.putAll(getPointcuts(cl, true)); //装填全局切入点对象,便于切入点重用
			}
		}
		return total;
	}

	/***
	 * 根据方法名获取各种通知
	 * @param cl
	 * @param aspect
	 * @param pointcuts
	 * @return
	 */
	public List<AnnotationModel> getAdviceByMethods(Class<?> cl, Object aspect, 
			Map<Object, AbstractAnnotationPointCut> globalPointcuts) {
		String value = "";
		String argNames = "";
		List<AnnotationModel> advices = new ArrayList<AnnotationModel>();
		AbstractAnnotationPointCut atp = null;
		Map<Object, AbstractAnnotationPointCut> pointcuts = getPointcuts(cl, false); //当前aspect的切入点集合
		Method[] methods = cl.getDeclaredMethods();
		for (Method md : methods) {
			if (md.isAnnotationPresent(Before.class)) {
				Before before = md.getAnnotation(Before.class);
				value = before.pointcut();
				value = value == null || "".equals(value)
					? before.value() : value; 
				argNames = before.argNames();
				BeforeAnnotationModel adv = new BeforeAnnotationModel();
				atp = getPointCut(pointcuts, globalPointcuts, value);
				initAdvice(adv, argNames, aspect, md, atp);
				advices.add(adv);
			} else if (md.isAnnotationPresent(After.class)) {
				After after = md.getAnnotation(After.class);
				value = after.pointcut();
				value = value == null || "".equals(value) 
					? after.value() : value;
				argNames = after.argNames();
				AfterAnnotationModel adv = new AfterAnnotationModel();
				atp = getPointCut(pointcuts, globalPointcuts, value);
				initAdvice(adv, argNames, aspect, md, atp);
				advices.add(adv);
			} else if (md.isAnnotationPresent(AfterReturning.class)) {
				AfterReturning arn = md.getAnnotation(AfterReturning.class);
				value = arn.pointcut();
				value = value == null || "".equals(value) 
					? arn.value() : value;
				argNames = arn.argNames();
				String returning = arn.returning();
				AfterReturningAnnotationModel ara = new AfterReturningAnnotationModel();
				atp = getPointCut(pointcuts, globalPointcuts, value);
				initAdvice(ara, argNames, aspect, md, atp);
				ara.setReturning(returning);
				advices.add(ara);
			} else if (md.isAnnotationPresent(AfterThrowing.class)) {
				AfterThrowing atw = md.getAnnotation(AfterThrowing.class);
				value = atw.pointcut();
				value = value == null || "".equals(value) 
					? atw.value() : value;
				argNames = atw.argNames();
				String throwing = atw.throwing();
				AfterThrowingAnnotationModel ata = new AfterThrowingAnnotationModel();
				atp = getPointCut(pointcuts, globalPointcuts, value);
				initAdvice(ata, argNames, aspect, md, atp);
				ata.setThrowing(throwing);
				advices.add(ata);
			} else if (md.isAnnotationPresent(Around.class)) {
				Around around = md.getAnnotation(Around.class);
				value = around.pointcut();
				value = value == null || "".equals(value) 
					? around.value() : value;
				argNames = around.argNames();
				AroundAnnotationModel avc = new AroundAnnotationModel();
				atp = getPointCut(pointcuts, globalPointcuts, value);
				initAdvice(avc, argNames, aspect, md, atp);
				advices.add(avc);
			}
		}
		return advices;
	}
	
	
	

	/***
	 * 获取Aspect中定义的切入点对象
	 * @param cl
	 * @return
	 */
	private Map<Object, AbstractAnnotationPointCut> getPointcuts(Class<?> cl, boolean global) {
		String expression = null;
		String argNames = null;
		DefaultAnnotationPointCut dap = null;
		Map<Object, AbstractAnnotationPointCut> pointcuts 
			= new HashMap<Object, AbstractAnnotationPointCut>();
		String className = cl.getName();
		Method[] methods = cl.getDeclaredMethods();
		for (Method md : methods) {
			if (md.isAnnotationPresent(
					com.spring.aop.common.annotation.Pointcut.class)) {
				com.spring.aop.common.annotation.Pointcut pt 
					= md.getAnnotation(com.spring.aop.common.annotation.Pointcut.class);
				expression = pt.value();
				argNames = pt.argNames();
				dap = new DefaultAnnotationPointCut();
				dap.initClassConfig(expression); //初始化表达式解析器
				String id = global ? className + "." + md.getName() + "(" + argNames + ")"
					: md.getName() + "(" + argNames + ")";	
				pointcuts.put(id, dap);
			}
		}
		return pointcuts;
	}
	
	
	/**
	 * 获得切入点对象
	 * @return
	 */
	private AbstractAnnotationPointCut getPointCut(Map<Object, AbstractAnnotationPointCut> pointcuts
			, Map<Object, AbstractAnnotationPointCut> globalPointcuts, String value) {
		DefaultAnnotationPointCut dap = null;
		if (isPointCutExpression(value)) {
			dap = new DefaultAnnotationPointCut();
			dap.initClassConfig(value);
		} else {
			dap = (DefaultAnnotationPointCut) pointcuts.get(value);
			if (dap == null)
				dap = (DefaultAnnotationPointCut) globalPointcuts.get(value);
		}
		return dap;
	}
	
	
	/****
	 * 初始化各种通知的基础参数
	 */
	private void initAdvice(AnnotationModel advice, String argNames, Object aspect,
			Method md, AbstractAnnotationPointCut atp) {
		advice.setArgNames(argNames);
		advice.setAspect(aspect);
		advice.setMethod(md);
		if (atp != null) advice.setPointCut(atp);
	}
	

	//判断切点表达式
	private boolean isPointCutExpression(String expression) {
		expression = expression.replaceFirst("^\\s+", ""); //去开头空格
		return expression.startsWith("execution"); 
	}
	
}
