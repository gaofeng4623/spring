package com.spring.transaction.common;

import com.spring.transaction.support.TransactionDefinition;

/**@Transactional事务参数**/
public class Propagation {
	public static final String REQUIRED = TransactionDefinition.PROPAGATION_REQUIRED;
	public static final String SUPPORTS = TransactionDefinition.PROPAGATION_SUPPORTS;
	public static final String MANDATORY = TransactionDefinition.PROPAGATION_MANDATORY;
	public static final String REQUIRES_NEW = TransactionDefinition.PROPAGATION_REQUIRES_NEW;
	public static final String NEVER = TransactionDefinition.PROPAGATION_NEVER;
	public static final String NOT_SUPPORTED = TransactionDefinition.PROPAGATION_NOT_SUPPORTED;
	public static final String NESTED = TransactionDefinition.PROPAGATION_NESTED;
}
