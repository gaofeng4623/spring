package com.spring.mvc.servlet.exresolver;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.spring.mvc.common.ModelAndView;


public class DefaultMappingExceptionResolver implements HandlerExceptionResolver{

	@Override
	public ModelAndView resolveException(HttpServletRequest request,
			HttpServletResponse response, Object handler, Throwable ex) {
		Throwable th = ex instanceof InvocationTargetException ? 
				((InvocationTargetException) ex).getTargetException() : ex;
		if (th instanceof IOException) {
			return new ModelAndView("ioexp");
		} else if (th instanceof SQLException) {
			return new ModelAndView("sqlexp");
		} else if (th instanceof NullPointerException) {
			return new ModelAndView("null");
		} else if (th instanceof NumberFormatException) {
			return new ModelAndView("num");
		}
		
		return new ModelAndView("exception");
	}


}
