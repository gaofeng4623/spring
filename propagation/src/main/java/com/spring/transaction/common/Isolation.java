package com.spring.transaction.common;

import com.spring.transaction.support.TransactionDefinition;

/**@Transactional隔离级别**/
public class Isolation {
	public static final String NONE = TransactionDefinition.ISOLATION_DEFAULT;
	public static final String COMMITTED = TransactionDefinition.ISOLATION_READ_COMMITTED;
	public static final String UNCOMMITTED = TransactionDefinition.ISOLATION_READ_UNCOMMITTED;
	public static final String REPEATABLE_READ = TransactionDefinition.ISOLATION_REPEATABLE_READ;
	public static final String SERIALIZABLE = TransactionDefinition.ISOLATION_SERIALIZABLE;
}
