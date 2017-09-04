package com.spring.mvc.controller.convers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class AbsHttpMessageConverter implements HttpMessageConverter {
	private String defaultAccept = "application/x-ms-application";

	public abstract boolean canRead(String accept);

	public abstract String dataConver(Object data);

	public void write(Object data, HttpServletRequest request,
			HttpServletResponse response) {
		try {
			String result = dataConver(data);
			response.setContentType(getContentType(request));
			response.setCharacterEncoding(request.getCharacterEncoding());
			response.getWriter().print(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String getContentType(HttpServletRequest request) {
		String accept = request.getHeader("accept");
		if (accept.contains(",")) {
			return accept.split(",")[0];
		}
		return this.defaultAccept;
	}

}
