package com.spring.mvc.common;

import java.lang.annotation.Annotation;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.spring.beans.common.utils.Server;
import com.spring.mvc.common.annotation.ModelAttribute;
import com.spring.mvc.common.annotation.PathVariable;
import com.spring.mvc.common.annotation.RequestParam;
import com.spring.mvc.common.annotation.SessionAttributes;

public class Util {
	/**
	 * 处理参数上的属性注解
	 * @param num
	 * @param parameters
	 * @param annotations
	 */
	public static void handleAttributesByParamAnnotation(int num, Object[] parameters, Annotation[][] annotations,
			HttpServletRequest request) {
		Object paramValue = parameters[num];
		if (paramValue != null) {
			Annotation[] ano = annotations[num];
			for (Annotation a : ano) {
				if (SessionAttributes.class.isAssignableFrom(a.annotationType())) {
					request.getSession().setAttribute(((SessionAttributes)a).value(), paramValue);
				}
			}
		}
	}
	
	/***
	 * 解析路径参数注解
	 * @param key
	 * @param annotations
	 * @param pathVariableMap
	 * @return
	 */
	public static String getPathVariableValue(String key, Annotation[] annotations,
			Map<String, String> pathVariableMap) {
		String value = null;
		for (Annotation an : annotations) {
			if (PathVariable.class.isAssignableFrom(an.annotationType())) {
				PathVariable pv = (PathVariable) an;
				if (!Server.isEmpty(pv.value())) {
					value = pathVariableMap.get(pv.value());
				} else {
					value = pathVariableMap.get(key);
				}
			}
		}
		return value;
	}
	
	/**
	 * 判断类型是否一致
	 * @param inter
	 * @param target
	 * @return
	 */
	public static boolean isSameType(Class<?> inter, Class<?> target) {
		try {
			return inter.isAssignableFrom(target);
		} catch (Exception e) {
			return false;
		}
	}
	
	/**
	 * 获得controller方法参数实例
	 * 
	 */
	public static Object getInstanceOfParamPojo(ModelMap modelMap, String paramName, 
			Annotation[] anno, Class<?> paramType) throws Exception {
		Annotation annotation = getModelAttributeAnno(anno);
		if (annotation != null) {
			Object target = null;
			ModelAttribute modelAttribute = (ModelAttribute) annotation;
			if (!Server.isEmpty(modelAttribute.value())) {
				target = modelMap.getAttribute(modelAttribute.value());
			} else {
				target = modelMap.getAttribute(paramName);
			}
			return target != null ? target : paramType.newInstance();
		} else {
			return paramType.newInstance();
		}
	}
	
	private static Annotation getModelAttributeAnno(Annotation[] anno) {
		for (Annotation an : anno) {
			if (ModelAttribute.class.isAssignableFrom(an.annotationType())) {
				return an;
			}
		}
		return null;
	}
	
	//获取@RequestParam注解默认值
	public static String getParamDefaultValue(int num, Annotation[][] annotations) {
		Annotation[] ans = annotations[num];
		for (Annotation an : ans) {
			if (RequestParam.class.isAssignableFrom(an.annotationType())) {
				RequestParam param = (RequestParam) an;
				return param.defaultValue();
			}
		}
		return null;
	}
}
