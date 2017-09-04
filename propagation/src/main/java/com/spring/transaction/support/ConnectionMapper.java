package com.spring.transaction.support;

import java.sql.Connection;

/**
 * 连接的载体类，用于记录当前连接是否具有事务状态
 * 是事务连接，还是非事务连接
 * @author Administrator
 *
 */
public class ConnectionMapper {
	private Connection connection;
	private boolean transaction;
	private int counter = 0; //引用计数器
	
	public ConnectionMapper() {
		
	}
	
	public ConnectionMapper(Connection connection, boolean transaction) {
		this.connection = connection;
		this.transaction = transaction;
	}

	public Connection getConnection() {
		return connection;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}

	public boolean isTransaction() {
		return transaction;
	}

	public void setTransaction(boolean transaction) {
		this.transaction = transaction;
	}

	public int getCounter() {
		return counter;
	}
	
	public void setCounter(int counter) {
		this.counter = counter;
	}
	
	public void addCounter() {
		this.counter ++;
	}
	
	public void minuCounter() {
		this.counter --;
	}
	
}
