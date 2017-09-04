package com.spring.beans.common.exception;

public class DataAccessException extends RuntimeException{
	
	private static final long serialVersionUID = -6337044751120993649L;

	public DataAccessException() {
		super();
	}
	
	public DataAccessException(String message) {
		super(message);
	}
	
	public DataAccessException(Throwable e) {
		super(e);
	}
}
