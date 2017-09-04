package com.spring.mvc.controller;

import javax.servlet.ServletContext;

import com.spring.beans.aware.ServletContextAware;


public class WebApplicationObjectSupport extends ApplicationObjectSupport implements ServletContextAware{

	public void setServletContext(ServletContext context) {
		// TODO Auto-generated method stub
		
	}

	public ServletContext getServletContext() {
		// TODO Auto-generated method stub
		return null;
	}

}
