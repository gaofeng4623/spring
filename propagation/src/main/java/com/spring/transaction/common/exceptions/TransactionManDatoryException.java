package com.spring.transaction.common.exceptions;

import com.spring.beans.common.exception.DataAccessException;

public class TransactionManDatoryException extends DataAccessException{
	
	private static final long serialVersionUID = 1L;

	public TransactionManDatoryException() {
		super("has no Transaction !");
	}
	
}
