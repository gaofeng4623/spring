package com.spring.mvc.controller.convers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface HttpMessageConverter {

	public boolean canRead(String accept);
	
	public void write(Object data, HttpServletRequest request, HttpServletResponse response);	
	
}
