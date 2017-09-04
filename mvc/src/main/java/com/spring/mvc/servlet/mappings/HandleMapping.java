package com.spring.mvc.servlet.mappings;

import javax.servlet.http.HttpServletRequest;

public interface HandleMapping {
	public Object lookupHandler(String path, HttpServletRequest request);
}
