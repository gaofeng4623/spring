package com.spring.transaction.support;

import java.lang.reflect.Method;

public class Excution {
	private Method method;
	private Object object;
	private Object[] args;

	public Excution(Method method, Object object, Object[] args) {
		this.method = method;
		this.object = object;
		this.args = args;
	}
	
	public Object execute() throws Exception {
		 return method.invoke(this.object, this.args);
	}

	public Method getMethod() {
		return method;
	}

	public void setMethod(Method method) {
		this.method = method;
	}

	public Object getObject() {
		return object;
	}

	public void setObject(Object object) {
		this.object = object;
	}

	public Object[] getArgs() {
		return args;
	}

	public void setArgs(Object[] args) {
		this.args = args;
	}

}
