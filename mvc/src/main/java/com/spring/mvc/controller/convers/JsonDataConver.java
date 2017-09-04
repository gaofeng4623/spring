package com.spring.mvc.controller.convers;

import com.spring.beans.common.utils.JsonConver;

public class JsonDataConver extends AbsHttpMessageConverter{
	private String contentType = "application/json";

	public boolean canRead(String accept) {
		return accept.equals(contentType)
			|| accept.startsWith(contentType);
	}
	
	
	@Override
	public String dataConver(Object data) {
		return new JsonConver().createJson(data);
	}
	
}
