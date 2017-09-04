package com.spring.transaction.interceptor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import javax.sql.DataSource;

import com.spring.beans.common.utils.Consts;
import com.spring.transaction.manager.JdbcTransactionObjectSupport;

public class DataSourceManager implements InvocationHandler{
	private Object target;
	private Object object;
	
	public DataSourceManager(Object bean) {
		setTarget(bean);
	}
	
	public static Object newInstance(Object bean) {
		return new DataSourceManager(bean).getTarget();
	}
	
	public Object getTarget() {
		return target;
	}

	public void setTarget(Object bean) {
		Class<?>[] interfaces = bean.getClass().getInterfaces();
		this.target = Proxy.newProxyInstance(
				bean.getClass().getClassLoader(), interfaces, this);
		this.object = bean;
	}

	public Object getObject() {
		return object;
	}

	public void setObject(Object object) {
		this.object = object;
	}



	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		String methodName = method.getName();
		if (methodName.equals(Consts.connectionMethod)) {
			if (JdbcTransactionObjectSupport.getConnection((DataSource)object) == null) {
				if (getObject() != null)
					return ((DataSource) getObject()).getConnection();
			} else {
				return JdbcTransactionObjectSupport.getConnection((DataSource) getObject());
			}
			
		} else {
			return method.invoke(getObject(), args);
		}
		
		return null;
	}

	
}
