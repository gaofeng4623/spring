package com.spring.mvc.controller.convers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.spring.beans.common.exception.BeanNotFoundException;
import com.spring.beans.common.utils.Server;
import com.spring.context.WebApplicationContext;
import com.spring.mvc.common.RequestContextUtils;

public class AnnotationConverterAdapter {
	private List<HttpMessageConverter> defaultConvers;
	
	public AnnotationConverterAdapter() {
		this.defaultConvers = new ArrayList<HttpMessageConverter>();
		defaultConvers.add(new JsonDataConver());
		defaultConvers.add(new XmlDataConver());
	}
	
	
	
	public HttpMessageConverter lookupMessageConverter(
			HttpServletRequest request, HttpServletResponse response, String accept) throws Exception {
		List<HttpMessageConverter> resultConver = null;
		//先从上下文中查找自定义容器
		WebApplicationContext mvcContext = 
			RequestContextUtils.getWebApplicationContext(request);
		try {
			MessageConverList convers = mvcContext.getBean(MessageConverList.class);
			resultConver = convers.getConvers();
		} catch (BeanNotFoundException e) {
			resultConver = this.defaultConvers;
		}
		String acceptType = Server.isEmpty(accept) ? request.getHeader("accept") : accept;
		for (Iterator<HttpMessageConverter> it = resultConver.iterator(); it.hasNext();) {
			HttpMessageConverter hc = it.next();
			if (hc.canRead(acceptType))
				return hc;
		}
		return null;
	}
	
}
