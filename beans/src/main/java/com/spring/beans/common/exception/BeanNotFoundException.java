package com.spring.beans.common.exception;

public class BeanNotFoundException extends Exception{
	public BeanNotFoundException(Object object) {
		super("can't found bean " + object.toString());
	}
}
