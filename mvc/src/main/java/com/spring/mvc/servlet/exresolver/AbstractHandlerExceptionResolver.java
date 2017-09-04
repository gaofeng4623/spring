package com.spring.mvc.servlet.exresolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.spring.mvc.common.ModelAndView;


public abstract class AbstractHandlerExceptionResolver implements
		HandlerExceptionResolver {

	public ModelAndView resolveException(HttpServletRequest request,
			HttpServletResponse response, Object handler, Throwable ex) {
		prepareResponse(ex, request);
		return doResolveException(request, response, handler, ex);
	}
	
	public abstract void prepareResponse(Throwable ex, HttpServletRequest request);
	
	public abstract ModelAndView doResolveException(HttpServletRequest request,
			HttpServletResponse response, Object handler, Throwable ex);

}
