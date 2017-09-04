package com.spring.transaction.interceptor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class TransactionFilter implements InvocationHandler{

	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		return null;
	}

}
