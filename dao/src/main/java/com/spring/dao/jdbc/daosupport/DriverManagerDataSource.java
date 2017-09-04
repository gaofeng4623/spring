package com.spring.dao.jdbc.daosupport;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

import javax.sql.DataSource;

public class DriverManagerDataSource implements DataSource{
	private String driverClassName;
	private String url;
	private String username;
	private String password;
	
	public DriverManagerDataSource() {
		
	}
	
	public String getDriverClassName() {
		return driverClassName;
	}

	public void setDriverClassName(String driverClassName) {
		this.driverClassName = driverClassName;
		try {
			Class.forName(driverClassName);
		} catch (ClassNotFoundException e) {
			System.out.println("找不到驱动包:" + e.getMessage());
		}
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Connection getConnection() throws SQLException {
		return DriverManager.getConnection(url, username, password);
	}

	public Connection getConnection(String username, String password)
			throws SQLException {
		return null;
	}

	public PrintWriter getLogWriter() throws SQLException {
		return null;
	}

	public int getLoginTimeout() throws SQLException {
		return 0;
	}

	public void setLogWriter(PrintWriter out) throws SQLException {
	}

	public void setLoginTimeout(int seconds) throws SQLException {
	}

	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return false;
	}

	public <T> T unwrap(Class<T> iface) throws SQLException {
		return null;
	}
	
	public void close() {
		System.out.println("数据源已关闭!");
	}

	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		return null;
	}
}
