package com.spring.transaction.common.exceptions;

import com.spring.beans.common.exception.DataAccessException;

public class TransactionNeverException extends DataAccessException{
	
	private static final long serialVersionUID = 1L;

	public TransactionNeverException() {
		super("因为当前存在事务，所以抛出异常!");
	}
	
}
