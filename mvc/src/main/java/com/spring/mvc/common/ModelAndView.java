package com.spring.mvc.common;

import javax.servlet.http.HttpServletRequest;


public class ModelAndView {
	private String pageview;
	private ModelMap data;
	private Object pojo;
	
	public ModelAndView(String pageview, Object data) {
		if (data instanceof ModelMap)
			this.data = (ModelMap)data;
		else
			this.pojo = data;
		this.pageview = pageview;
	}
	
	public ModelAndView(String pageview) {
		this.pageview = pageview;
	}

	public String getPageview() {
		return pageview;
	}

	public void setPageview(String pageview) {
		this.pageview = pageview;
	}

	public ModelMap getData() {
		return data;
	}

	public void setData(ModelMap data) {
		this.data = data;
	}
	
	public Object getPojo() {
		return pojo;
	}

	public void setPojo(Object pojo) {
		this.pojo = pojo;
	}

	public void fillRequestAttributes(HttpServletRequest request) {
		if (data != null)
			this.data.fillRequestAttributes(request);
	}
}
