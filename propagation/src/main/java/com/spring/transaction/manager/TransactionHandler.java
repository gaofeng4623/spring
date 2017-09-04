package com.spring.transaction.manager;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.LinkedList;

import javax.sql.DataSource;

import com.spring.beans.common.exception.DataAccessException;
import com.spring.beans.common.utils.Consts;
import com.spring.transaction.common.exceptions.TransactionManDatoryException;
import com.spring.transaction.common.exceptions.TransactionNeverException;
import com.spring.transaction.support.ConnectionMapper;
import com.spring.transaction.support.ConnectionWrapper;
import com.spring.transaction.support.TransactionDefinition;
import com.spring.transaction.support.TransactionStatus;

public class TransactionHandler {
	private DataSource datasource;
	private LinkedList<TransactionStatus> statusHierarchy;
	private LinkedList<ConnectionMapper> connectionHierarchy;

	public TransactionHandler(DataSource datasource) {
		this.datasource = datasource;
		this.statusHierarchy = new LinkedList<TransactionStatus>();
		this.connectionHierarchy = new LinkedList<ConnectionMapper>();
	}
	


	/**
	 * 事务的核心处理机制
	 * 
	 * @param transDefinition 
	 * @return
	 * @throws DataAccessException
	 */
	public TransactionStatus synchroStatus(TransactionDefinition transDefinition)
			throws DataAccessException {
		Savepoint point = null;
		transDefinition.initDefine(); //初始化事务配置
		String propagation = transDefinition.getPropagation();
		TransactionStatus transactionStatus = new TransactionStatus();
		transactionStatus.setCurrentStatus(propagation);
		try {

			if (propagation.equals(TransactionDefinition.PROPAGATION_REQUIRED)) {

				if (hasTransaction()) {
					setCurrentTransactionAttributes(transDefinition);
				} else {
					appendNewTransaction(transDefinition);
				}

			} else if (propagation.equals(TransactionDefinition.PROPAGATION_SUPPORTS)) {
				
				if (hasTransaction()) {
					setCurrentTransactionAttributes(transDefinition);
				} else {
					appendNoTransaction(); //非事务方式,自动提交
				}
				
			} else if (propagation.equals(TransactionDefinition.PROPAGATION_MANDATORY)) {
				
				if (hasTransaction()) {
					setCurrentTransactionAttributes(transDefinition);
				} else {
					throw new TransactionManDatoryException(); // 子异常
				}

			} else if (propagation.equals(TransactionDefinition.PROPAGATION_REQUIRES_NEW)) {
				
				appendNewTransaction(transDefinition);
				
			} else if (propagation.equals(TransactionDefinition.PROPAGATION_NOT_SUPPORTED)) {
				appendNoTransaction(); //非事务方式,自动提交
			} else if (propagation.equals(TransactionDefinition.PROPAGATION_NEVER)) {
				
				if (hasTransaction()) {
					throw new TransactionNeverException();
				} else {
					appendNoTransaction(); //非事务方式,自动提交
				}
				
			} else if (propagation.equals(TransactionDefinition.PROPAGATION_NESTED)) {

				if (hasTransaction()) {
					setCurrentTransactionAttributes(transDefinition);
					point = getCurrentTransaction().setSavepoint();
				} else {
					appendNewTransaction(transDefinition);
				}

			}
			/**** 同步事务状态 ****/
			appendCurrentStatus(transactionStatus, point);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DataAccessException("事务执行异常");
		}
		return getCurrentStatus();
	}

	
	/*
	 * 初始化事务的基本属性，记录事务状态
	 * 
	 * @param connection
	 * @throws SQLException
	 */
	private void setCurrentTransactionAttributes(TransactionDefinition definition) throws SQLException {
		Connection connection = getCurrentTransaction();
		initConnectionParameters(connection, definition);
		this.addCounter(); //递增引用计数器
	}
	
	
	/*
	 * 初始化连接的只读、隔离级别属性
	 */
	private void initConnectionParameters(Connection connection, TransactionDefinition definition) throws SQLException {
		if (definition.isReadOnly()) {
			if (connection != null && !connection.isReadOnly()) {
				connection.setReadOnly(true);
			}
		}
		if (definition.getIsolation() != null) {
			if (definition.getIsolation().equals(TransactionDefinition.ISOLATION_DEFAULT)) {
				connection.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
			} else if (definition.getIsolation().equals(TransactionDefinition.ISOLATION_READ_COMMITTED)) {
				connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
			} else if (definition.getIsolation().equals(TransactionDefinition.ISOLATION_READ_UNCOMMITTED)) {
				connection.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
			} else if (definition.getIsolation().equals(TransactionDefinition.ISOLATION_REPEATABLE_READ)) {
				connection.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
			} else if (definition.getIsolation().equals(TransactionDefinition.ISOLATION_SERIALIZABLE)) {
				connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			}
		}
	}


	/***
	 * 更新记录当前事务的运行状态
	 * @param transactionStatus
	 * @param point
	 */
	private void appendCurrentStatus(TransactionStatus transactionStatus, Savepoint point) {
		TransactionStatus status = getCurrentStatus();
		String previousStatus = status == null ? Consts.SUPER : status
				.getCurrentStatus();
		transactionStatus.setPreviousStatus(previousStatus);
		transactionStatus.setPoint(point);
		this.statusHierarchy.addLast(transactionStatus);
	}


	/***
	 * 提交当前事务
	 * @throws SQLException
	 */
	public void commitCurrentTransaction() throws SQLException {
		ConnectionMapper mapper = this.getCurrentMapper();
		if (mapper.isTransaction() && mapper.getCounter() == 0) {
			Connection connection = mapper.getConnection();
			if (connection == null) return;
			connection.commit();
		}
	}
	
	
	/*
	 * 回滚当前事务
	 */
	public void rollbackCurrentTransaction(Throwable exception) throws Exception{
		TransactionStatus status = this.getCurrentStatus();
		ConnectionMapper mapper = this.getCurrentMapper();
		if (mapper.isTransaction()) {
			Connection connection = mapper.getConnection();
			if (connection == null) return;
			try {
				if (getCounter() > 0 && matchedStatus(status.getCurrentStatus())) {
					throw new DataAccessException(exception);
				} else {
					if (status.getPoint() != null) {
						connection.rollback(status.getPoint());
					} else {
						connection.rollback();
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	private boolean matchedStatus(String status) {
		return status.equals(TransactionDefinition.PROPAGATION_REQUIRED)
				|| status.equals(TransactionDefinition.PROPAGATION_SUPPORTS)
				|| status.equals(TransactionDefinition.PROPAGATION_MANDATORY);
	}
	
	
	//新建一个非事务
	public void appendNoTransaction() throws SQLException {
		Connection connection = datasource.getConnection();
		ConnectionMapper mapper = new ConnectionMapper(connection, false);
		this.connectionHierarchy.addLast(mapper);
	}
	
	//新建一个代理事务
	public void appendNewTransaction(TransactionDefinition definition)
			throws SQLException {
		Connection connection = datasource.getConnection();
		connection.setAutoCommit(false);
		initConnectionParameters(connection, definition);
		connection = new ConnectionWrapper(connection).getTarget();
		ConnectionMapper mapper = new ConnectionMapper(connection, true);
		this.connectionHierarchy.addLast(mapper);
	}
	

	public Connection getConnection() throws SQLException {
		
		if (connectionHierarchy.size() > 0) {
			return connectionHierarchy.getLast().getConnection();
		} else {
			return datasource.getConnection();
		}
	}
	
	public Connection getCurrentTransaction() {
		if (hasTransaction()) {
			return connectionHierarchy.getLast().getConnection();
		}
		return null;
	}
	
	public void removeCurrentTransaction() {
		if (connectionHierarchy.size() > 0) {
			connectionHierarchy.removeLast();
		}
	}

	public TransactionStatus getCurrentStatus() {
		if (statusHierarchy.size() > 0) {
			return statusHierarchy.getLast();
		}
		return null;
	}
	
	private boolean hasTransaction() {
		if (this.connectionHierarchy.size() > 0) {
			ConnectionMapper mapper = connectionHierarchy.getLast();
			if (mapper.isTransaction()) {
				return true;
			}
		}
		return false;
	}
	
	//获取引用计数器
	public int getCounter() {
		if (this.connectionHierarchy.size() > 0) {
			ConnectionMapper mapper = connectionHierarchy.getLast();
			return mapper.getCounter();
		}
		return 0;
	}
	
	//递增引用计数器
	public void addCounter() {
		if (this.connectionHierarchy.size() > 0) {
			ConnectionMapper mapper = connectionHierarchy.getLast();
			mapper.addCounter();
		}
	}
	//递减引用计数器
	public void minuCounter() {
		if (this.connectionHierarchy.size() > 0) {
			ConnectionMapper mapper = connectionHierarchy.getLast();
			mapper.minuCounter();
		}
	}
	
	//返回一个ConnectionMapper
	public ConnectionMapper getCurrentMapper() {
		if (this.connectionHierarchy.size() > 0) {
			return connectionHierarchy.getLast();
		}
		return null;
	}
	
	//判断当前进行的工作是否以事务方式进行
	public boolean doingTransaction() {
		if (this.connectionHierarchy.size() > 0) {
			ConnectionMapper mapper = connectionHierarchy.getLast();
			return mapper.isTransaction();
		}
		return false;
	}
	
	/*
	 * 移除当前事务的状态
	 * @param status
	 */
	public void removeCurrentStatus() {
		if (statusHierarchy.size() > 0) {
			statusHierarchy.removeLast();
		}
	}
	
	
	/*恢复连接原有状态属性*/
	public void restoreConnection(Connection connection){
		try {
			if (connection == null) return;
			if (connection.isReadOnly()) {
				connection.setReadOnly(false);
			}
			connection.setAutoCommit(true);
			connection.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
}
