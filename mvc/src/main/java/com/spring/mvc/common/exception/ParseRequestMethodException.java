package com.spring.mvc.common.exception;

public class ParseRequestMethodException extends Exception{
	
	private static final long serialVersionUID = -8926760399472173238L;

	public ParseRequestMethodException(String message) {
		super(message);
	}
	
	public ParseRequestMethodException() {
		super("the request method is wrong !");
	}
}
