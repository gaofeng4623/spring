package com.spring.beans.common.exception;

public class BeanDependOnException extends Exception{
	public BeanDependOnException(Exception e) {
		super(e);
	}
}
