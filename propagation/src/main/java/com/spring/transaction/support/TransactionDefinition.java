package com.spring.transaction.support;

import java.util.ArrayList;
import java.util.List;

import com.spring.beans.common.utils.Server;


public class TransactionDefinition {
	public static final String PROPAGATION_REQUIRED = "PROPAGATION_REQUIRED";
	public static final String PROPAGATION_SUPPORTS = "PROPAGATION_SUPPORTS";
	public static final String PROPAGATION_MANDATORY = "PROPAGATION_MANDATORY";
	public static final String PROPAGATION_REQUIRES_NEW = "PROPAGATION_REQUIRES_NEW";
	public static final String PROPAGATION_NEVER = "PROPAGATION_NEVER";
	public static final String PROPAGATION_NOT_SUPPORTED = "PROPAGATION_NOT_SUPPORTED";
	public static final String PROPAGATION_NESTED = "PROPAGATION_NESTED";
	public static final String ISOLATION_DEFAULT = "ISOLATION_DEFAULT";
	public static final String ISOLATION_READ_UNCOMMITTED = "ISOLATION_READ_UNCOMMITTED";
	public static final String ISOLATION_READ_COMMITTED = "ISOLATION_READ_COMMITTED";
	public static final String ISOLATION_REPEATABLE_READ = "ISOLATION_REPEATABLE_READ";
	public static final String ISOLATION_SERIALIZABLE = "ISOLATION_SERIALIZABLE";
	
	private String definition;
	public String propagation; //事务类型
	public String isolation; //隔离级别
	public List<String> rollbackForClassname;
	public List<String> noRollbackForClassname;
	public boolean readOnly;
	public long timeout;

	public TransactionDefinition() {
		this.rollbackForClassname = new ArrayList<String>();
		this.noRollbackForClassname = new ArrayList<String>();
	}

	public void initDefine() {
		if (!Server.isEmpty(this.definition)) {
			String[] define = definition.contains(",") ? 
				definition.split(",") : new String[]{definition};
				for (String s : define) {
					if (s.trim().startsWith("+")) {
						noRollbackForClassname.add(s.trim().replace("+", ""));
					} else if (s.trim().startsWith("-")) {
						rollbackForClassname.add(s.trim().replace("-", ""));
					} else {
						//事务开始
						if (s.trim().startsWith("PROPAGATION")) {
							this.propagation = s.trim();
						}
						//隔离级别
						if (s.trim().startsWith("ISOLATION")) {
							this.isolation = s.trim();
						}
					}
				}
		}
	}
	
	public boolean wasRollBackException(Throwable e) {
		if (this.rollbackForClassname.size() == 0) return false;
		for (String c : rollbackForClassname) {
			try {
				if (Class.forName(c).isAssignableFrom(e.getClass())) {
					return true;
				}
			} catch (ClassNotFoundException e1) {
			}
		}
		return false;
	}
	
	public boolean wasNoRollBackException(Throwable e) {
		if (this.noRollbackForClassname.size() == 0) return false;
		for (String c : noRollbackForClassname) {
			try {
				if (Class.forName(c).isAssignableFrom(e.getClass()))
					return true;
			} catch (ClassNotFoundException e1) {
			}
		}
		return false;
	}
	
	public String getDefinition() {
		return definition;
	}

	public void setDefinition(String definition) {
		this.definition = definition;
	}

	public boolean isReadOnly() {
		return this.definition.toUpperCase().contains("READONLY");
	}
	
	public String getPropagation() {
		return propagation;
	}

	public void setPropagation(String propagation) {
		this.propagation = propagation;
	}

	public String getIsolation() {
		return isolation;
	}

	public void setIsolation(String isolation) {
		this.isolation = isolation;
	}

	public List<String> getRollbackForClassname() {
		return rollbackForClassname;
	}

	public void setRollbackForClassname(List<String> rollbackForClassname) {
		this.rollbackForClassname = rollbackForClassname;
	}

	public List<String> getNoRollbackForClassname() {
		return noRollbackForClassname;
	}

	public void setNoRollbackForClassname(List<String> noRollbackForClassname) {
		this.noRollbackForClassname = noRollbackForClassname;
	}

	public long getTimeout() {
		return timeout;
	}

	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

}
