package com.spring.mvc.controller.convers;

import java.util.List;

public class MessageConverList {
	private List<HttpMessageConverter> convers;

	public List<HttpMessageConverter> getConvers() {
		return convers;
	}

	public void setConvers(List<HttpMessageConverter> convers) {
		this.convers = convers;
	}
	
	
	
}
