package com.spring.mvc.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.spring.mvc.common.ModelAndView;

public interface Controller {

	public ModelAndView handleRequest(HttpServletRequest request,
                                      HttpServletResponse response) throws Exception;
}   
