package com.spring.aop.support.annotation.runtime;

public interface CodeSignature extends Signature{

	Class<?>[] getParameterTypes();

	String[] getParameterNames();

	Class<?>[] getExceptionTypes();
}
