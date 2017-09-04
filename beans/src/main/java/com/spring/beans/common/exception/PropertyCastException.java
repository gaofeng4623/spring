package com.spring.beans.common.exception;

public class PropertyCastException extends Exception{
	public PropertyCastException(Exception e) {
		super(e);
	}
	public PropertyCastException(Class<?> propertyType, Object target) {
		super("can't cast " + target.getClass().getName() + " to " + propertyType.getName());
	}
}
