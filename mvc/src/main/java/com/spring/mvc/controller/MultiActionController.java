package com.spring.mvc.controller;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.spring.mvc.common.ModelAndView;
import com.spring.mvc.common.ParameterAndDefualtMethod;

public class MultiActionController extends AbstractController{

	@Override
	public ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Object result = null;
		String methodName = null;
		String paramName = ParameterAndDefualtMethod.getParamName();
		if (paramName == null) return null; 
		methodName = request.getParameter(paramName);
		if (methodName == null || methodName.length() == 0)
			methodName = ParameterAndDefualtMethod.getDefaultMethodName();
		Class<?> cl = this.getClass();
		Method[] methods = cl.getMethods();
		for (Method m : methods) {
			if (m.getName().equals(methodName)) {
				result = m.invoke(this, new Object[]{request, response});
			}
		}
		return (ModelAndView) result;
	}

}
