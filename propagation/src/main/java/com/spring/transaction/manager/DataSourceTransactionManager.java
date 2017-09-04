package com.spring.transaction.manager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Properties;

import javax.sql.DataSource;

import com.spring.beans.common.exception.DataAccessException;
import com.spring.transaction.support.Excution;
import com.spring.transaction.support.TransactionDefinition;
import com.spring.transaction.support.TransactionStatus;

public class DataSourceTransactionManager implements InterfacePlatformTransactionManager{
	private DataSource dataSource;
	

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	public boolean hasTransaction() {
		return false;
	}
	
	public TransactionDefinition getTransactionDefinition(Properties ps, Method method) {
		return JdbcTransactionObjectSupport.getTransactionDefinition(ps, method);
	}
	
	public TransactionStatus openTransaction(TransactionDefinition definition)
		throws DataAccessException {
		return JdbcTransactionObjectSupport.getTransaction(dataSource, definition);
	}
	
	
	// 根据definition的rollbackfor和norollbackfor属性判断是否抛出异常
	public Object execute(TransactionDefinition definition, Excution exc) throws Throwable {
		try {
			exc.execute();
		} catch (Throwable e) {
			e = getFinalTargetException(e);

			if (definition.wasRollBackException(e)) {
				throw e;
			} else {
				if (!definition.wasNoRollBackException(e)) {
					if (e instanceof RuntimeException || e instanceof Error) { /*运行异常和错误全部抛出*/
						throw e; 
					}
				}
			}
			e.printStackTrace();
		}
		return null;
	}
	
	public void commit() throws Exception {
		JdbcTransactionObjectSupport.commitTransaction();
	}

	public void rollback(Throwable exception) throws Exception{
		JdbcTransactionObjectSupport.rollbackTransaction(exception);
	}


	public void closeTransaction() {
		JdbcTransactionObjectSupport.closeTransaction();
	}


	/***
	 * 在这里我对嵌套事务异常进行剖析，支持五层嵌套事务
	 * @param e
	 * @return
	 */
	public Throwable getFinalTargetException(Throwable e) {
		for (int i = 0; i < 5; i++) {
			if (e instanceof InvocationTargetException) {
				e = ((InvocationTargetException) e).getTargetException();
			}
		}
		return e;
	}

	
}
