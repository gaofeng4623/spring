package com.spring.mvc.servlet.view;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.spring.mvc.controller.convers.JsonDataConver;

/**
 * @author json视图转换器
 *
 */
public class MappingJackson2JsonView implements XmlJsonView{

	public void converToJsonXml(Object data, HttpServletRequest request,
			HttpServletResponse response) {
			if (data == null) {
				System.err.println("the data to json is null");
				return;
			}
			new JsonDataConver().write(data, request, response);
	}

}
