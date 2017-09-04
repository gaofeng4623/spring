package com.spring.transaction.support;

import java.sql.Savepoint;

public class TransactionStatus {
	private String currentStatus;
	private String previousStatus;
	private Savepoint point;
	private boolean finalBlock; //判断是否运行到finally的代码块
	
	public Savepoint getPoint() {
		return point;
	}

	public void setPoint(Savepoint point) {
		this.point = point;
	}

	public TransactionStatus() {}
	
	public TransactionStatus(String previousStatus
			, String currentStatus) {
		this.currentStatus = currentStatus;
		this.previousStatus = previousStatus;
	}

	public String getCurrentStatus() {
		return currentStatus;
	}

	public void setCurrentStatus(String currentStatus) {
		this.currentStatus = currentStatus;
	}

	public String getPreviousStatus() {
		return previousStatus;
	}

	public void setPreviousStatus(String previousStatus) {
		this.previousStatus = previousStatus;
	}

	public boolean isFinalBlock() {
		return finalBlock;
	}

	public void setFinalBlock(boolean finalBlock) {
		this.finalBlock = finalBlock;
	}
	
	
	
}
