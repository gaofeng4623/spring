package com.spring.mvc.servlet.exresolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.spring.mvc.common.ModelAndView;

public interface HandlerExceptionResolver {
	public ModelAndView resolveException(HttpServletRequest request,
                                         HttpServletResponse response, Object handler, Throwable ex);

}
