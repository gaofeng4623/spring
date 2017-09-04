package com.spring.transaction.manager;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.regex.Pattern;

import javax.sql.DataSource;

import com.spring.beans.common.exception.DataAccessException;
import com.spring.transaction.support.TransactionDefinition;
import com.spring.transaction.support.TransactionStatus;

public class JdbcTransactionObjectSupport {
	public static ThreadLocal<TransactionHandler> stack = new ThreadLocal<TransactionHandler>();

	/**
	 * @info规则：优先匹配方法注解,然后匹配类注解
	 */
	public static TransactionDefinition getTransactionDefinition(Properties ps,
			Method method) {
		TransactionDefinition define = new TransactionDefinition();
		for (Enumeration<Object> en = ps.keys(); en.hasMoreElements();) {
			String key = String.valueOf(en.nextElement());
			if ("*".equals(key)) continue;
			key = key.replaceAll("[*]", ".*"); //通配符转化为正则表达式
			if (Pattern.matches(key, method.getName())) {
				define.setDefinition(ps.getProperty(key));
				break;
			}
		}
		if (define.getDefinition() == null) {
			if (ps.getProperty("*") != null) {
				define.setDefinition(ps.getProperty("*"));
			}
		}
		return define;
	}

	/***
	 * @param connection
	 * @throws SQLException
	 *  当前有事务抛出异常
	 */
	public static TransactionStatus getTransaction(DataSource dataSource,
			TransactionDefinition definition) throws DataAccessException {
		if (stack.get() == null)
			stack.set(new TransactionHandler(dataSource));
		TransactionHandler handler = stack.get();
		return handler.synchroStatus(definition);

	}


	public static void commitTransaction() throws SQLException {
		TransactionHandler handler = stack.get();
		handler.commitCurrentTransaction();
	}
	
	public static void rollbackTransaction(Throwable exception) throws Exception{
		TransactionHandler handler = stack.get();
		handler.rollbackCurrentTransaction(exception);
	}
	
	public static void closeTransaction() {
		Connection conn = null;
		TransactionHandler handler = stack.get();
		TransactionStatus status = handler.getCurrentStatus();
		System.out.println("counter = " + handler.getCounter());
		if (handler.doingTransaction() && handler.getCounter() == 0) {
			if (status != null) status.setFinalBlock(true);
			conn = handler.getCurrentTransaction();
			handler.restoreConnection(conn); //恢复连接属性
			if (conn != null) {
				try {
					conn.close(); //调用代理的关闭
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		if (handler.getCounter() == 0) { 
			handler.removeCurrentTransaction(); //有选择性的移除连接对象
		}
		if (handler.getCounter() > 0) handler.minuCounter(); //释放计数器的引用计算
		handler.removeCurrentStatus(); //完全移除事务状态
	}

	// 代理类获得连接对象
	public static Connection getConnection(DataSource datasource)
			throws SQLException {
		if (stack.get() == null)
			return datasource.getConnection();
		else
			return stack.get().getConnection();
	}
}
