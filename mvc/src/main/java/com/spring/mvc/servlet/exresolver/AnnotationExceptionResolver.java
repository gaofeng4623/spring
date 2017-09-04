package com.spring.mvc.servlet.exresolver;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.spring.beans.factory.BeanFactory;
import com.spring.context.WebApplicationContext;
import com.spring.mvc.common.ModelAndView;
import com.spring.mvc.common.RequestContextUtils;
import com.spring.mvc.controller.HandleRequestResolver;

public class AnnotationExceptionResolver implements HandlerExceptionResolver {
	private Object controller;
	private String controllerId;
	private Method method;
	private Method dataBinder;
	private Class<?> prototype;
	private Class<?>[] exceptions;
	private BeanFactory factory;

	public AnnotationExceptionResolver() {
	}

	public AnnotationExceptionResolver(String controllerId, Method method,
			Method dataBinder, Class<?> prototype, Class<?>[] exceptions, BeanFactory factory) {
		this.controllerId = controllerId;
		this.method = method;
		this.dataBinder = dataBinder;
		this.prototype = prototype;
		this.exceptions = exceptions;
		this.factory = factory;
	}

	public ModelAndView resolveException(HttpServletRequest request,
			HttpServletResponse response, Object handler, Throwable th) {
		try {
			loadingController(request);
			return new HandleRequestResolver().handleRequestInternal(request,
				response, prototype, null, method, getDataBinder(), getController(), getFactory(), th, false, false, false);
		} catch (Exception e) {
			return null;
		}
	}

	public boolean matchException(Throwable ex) {
		if (exceptions == null || exceptions.length == 0)
			return true;
		for (Class<?> cl : exceptions) {
			if (cl.isAssignableFrom(ex.getClass()))
				return true;
		}
		return false;
	}
	

	public void loadingController(HttpServletRequest request) {
		/**
		 * AnnotationController的controller应该先为注解的ID
		 * 然后通过mvccontext获取,因为有可能出现非单例情况
		 **/
		try {
			WebApplicationContext mvcContext = RequestContextUtils
					.getWebApplicationContext(request);
			setController(mvcContext.getBean(getControllerId()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Object getController() {
		return controller;
	}

	public void setController(Object controller) {
		this.controller = controller;
	}

	public String getControllerId() {
		return controllerId;
	}

	public void setControllerId(String controllerId) {
		this.controllerId = controllerId;
	}

	public Method getMethod() {
		return method;
	}

	public void setMethod(Method method) {
		this.method = method;
	}

	public Class getPrototype() {
		return prototype;
	}

	public void setPrototype(Class prototype) {
		this.prototype = prototype;
	}

	public Class[] getExceptions() {
		return exceptions;
	}

	public void setExceptions(Class[] exceptions) {
		this.exceptions = exceptions;
	}

	public BeanFactory getFactory() {
		return factory;
	}

	public void setFactory(BeanFactory factory) {
		this.factory = factory;
	}

	public Method getDataBinder() {
		return dataBinder;
	}

	public void setDataBinder(Method dataBinder) {
		this.dataBinder = dataBinder;
	}
	
	

}
