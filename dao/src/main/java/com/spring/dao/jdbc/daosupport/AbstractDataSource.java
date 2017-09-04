package com.spring.dao.jdbc.daosupport;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

public abstract class AbstractDataSource implements DataSource {

	public PrintWriter getLogWriter() throws SQLException {
		return null;
	}

	public void setLogWriter(PrintWriter out) throws SQLException {

	}

	public void setLoginTimeout(int seconds) throws SQLException {

	}

	public int getLoginTimeout() throws SQLException {
		return 0;
	}

	public <T> T unwrap(Class<T> iface) throws SQLException {
		return null;
	}

	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return false;
	}
	
	public Connection getConnection(String username, String password)
			throws SQLException {
		return null;
	}

	public abstract Connection getConnection() throws SQLException; //微妙的设计
	
	
	public abstract DataSource determineTargetDataSource(); //切换数据源

}
