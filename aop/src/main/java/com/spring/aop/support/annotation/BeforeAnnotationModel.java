package com.spring.aop.support.annotation;

import java.lang.reflect.Method;

import com.spring.aop.support.annotation.runtime.JoinPoint;
import com.spring.beans.common.utils.ParameterNameDiscoverer;


public class BeforeAnnotationModel extends AnnotationModel{
	
	public void before(JoinPoint joinPoint, Object obj, Method method)
	{
	try {
		//通过getArgNames传参
		Object[] adviceArgs = null;
		String argNames = this.getArgNames();
		Object[] args = joinPoint.getArgs();
		String[] argNameList = null;
		ParameterNameDiscoverer pnd = new ParameterNameDiscoverer();
		String[] paramNames = pnd.getParameterNames(obj.getClass(), method);
		String[] adviceParamNames = pnd.getParameterNames(
				getAspect().getClass(), getMethod()); //通知中的方法参数名列表
		Class<?>[] types = getMethod().getParameterTypes(); //通知中的参数类型
		if (types.length > 0) {
			argNameList = argNames == null || "".equals(argNames) ? null :
			(argNames.contains(",") ? argNames.split(",") : new String[]{argNames});
			adviceArgs = new Object[types.length];
			for (int i = 0; i < types.length; i++) {
				if ((JoinPoint.class).isAssignableFrom(types[i])) {
					adviceArgs[i] = joinPoint;
				} else {
					if (parseNull(adviceParamNames)) continue;
					String name = adviceParamNames[i];
					if (inArgNameList(name, argNameList)) { //存在映射参数列表中
						for (int y = 0; y < paramNames.length; y++) {
							if (name.equals(paramNames[y])) {
								adviceArgs[i] = args[y];
							}
						}
					} else {
						System.err.println(name + " not in argNames"); //否则参数会失去赋值机会
					}
				}
			}
		}
		if (adviceArgs == null) {
			getMethod().invoke(getAspect());
		} else {
			getMethod().invoke(getAspect(), adviceArgs);
		}
		} catch (Exception e) {
		  e.printStackTrace();
		}
	}
	
	/**
	 * @info 匹配通知方法无参的情况
	 * @param adviceParamNames
	 * @return
	 */
	private boolean parseNull(String[] adviceParamNames) {
		return adviceParamNames == null || adviceParamNames.length == 0;
	}


	private boolean inArgNameList(String name, String[] argNameList) {
		if (argNameList == null) return false;
		for (String s : argNameList) {
			if (name.equals(s))
				return true;
		}
		return false;
	}
	
	
}
