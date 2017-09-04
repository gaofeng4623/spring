package com.spring.mvc.controller.convers;


public class XmlDataConver extends AbsHttpMessageConverter {
	private String contentType = "application/xml";

	public boolean canRead(String accept) {
		return accept.equals(contentType)
			|| accept.startsWith(contentType);
	}
	
	@Override
	public String dataConver(Object data) {
		// TODO Auto-generated method stub
		return null;
	}

}
