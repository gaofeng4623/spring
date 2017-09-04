package com.spring.transaction.support;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;

import com.spring.beans.common.utils.Consts;
import com.spring.transaction.manager.JdbcTransactionObjectSupport;
import com.spring.transaction.manager.TransactionHandler;

/***
 * 数据库连接的代理,主要用于处理嵌套事务的连接关闭问�?
 * 有引用，则不关闭，没引用则关�?
 * @author 高峰
 */
public class ConnectionWrapper implements InvocationHandler {

	private Connection target;

	private Connection object;

	public ConnectionWrapper(Connection conn) {
		setTarget(conn);
	}

	public Object getObject() {
		return object;
	}

	public void setObject(Connection object) {
		this.object = object;
	}

	public Connection getTarget() {
		return target;
	}

	public void setTarget(Connection target) {
		Class<?>[] interfaces = new Class<?>[] { Connection.class };
		this.target = (Connection) Proxy.newProxyInstance(target.getClass()
				.getClassLoader(), interfaces, this);
		this.object = target;
	}

	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		Object result = null;
		if (method.getName().equals(Consts.closeMethod)) {
			TransactionHandler handler = JdbcTransactionObjectSupport.stack.get();
			if (handler != null) {
				TransactionStatus status = handler.getCurrentStatus();
				if (status != null) {
					if (status.isFinalBlock()){
						System.out.println("finally关闭");
						method.invoke(this.getObject(), args); //关闭连接,意味�?��有最外层没有引用的连接才可以关闭
					}
				}
			}
		} else {
			result = method.invoke(this.getObject(), args);
		}
		return result;
	}

}
