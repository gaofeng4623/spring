package com.spring.transaction.manager;

import com.spring.beans.common.exception.DataAccessException;
import com.spring.transaction.support.Excution;
import com.spring.transaction.support.TransactionDefinition;
import com.spring.transaction.support.TransactionStatus;

public interface InterfacePlatformTransactionManager {
	public TransactionStatus openTransaction(TransactionDefinition definition)
		throws DataAccessException;
	public Object execute(TransactionDefinition definition, Excution exc) throws Throwable;
	public void commit() throws Throwable;
	public void rollback(Throwable exception) throws Throwable;
	public void closeTransaction();
}