package com.spring.mvc.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.spring.mvc.common.ModelAndView;

public abstract class AbstractController extends WebContextGenerator implements Controller{

	public abstract ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception;

	public ModelAndView handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		//有一些关于WebContextGenerator的处理
		return handleRequestInternal(request, response);
	}
	
}
