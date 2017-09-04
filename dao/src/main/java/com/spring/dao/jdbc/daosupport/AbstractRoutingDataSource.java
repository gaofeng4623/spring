package com.spring.dao.jdbc.daosupport;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import javax.sql.DataSource;


public abstract class AbstractRoutingDataSource extends AbstractDataSource {
	private DataSource defaultTargetDataSource;
	private Map<Object, DataSource> targetDataSources;

	public DataSource getDefaultTargetDataSource() {
		return defaultTargetDataSource;
	}

	public void setDefaultTargetDataSource(DataSource defaultTargetDataSource) {
		this.defaultTargetDataSource = defaultTargetDataSource;
	}
	
	public Map<Object, DataSource> getTargetDataSources() {
		return targetDataSources;
	}

	public void setTargetDataSources(Map<Object, DataSource> targetDataSources) {
		this.targetDataSources = targetDataSources;
	}

	@Override
	public Connection getConnection() throws SQLException {
		return determineTargetDataSource().getConnection();
	}
	
	@Override
	public DataSource determineTargetDataSource() {
		Object objectKey = determineCurrentLookupKey();
		if (objectKey != null) {
			return targetDataSources.get(objectKey);
		}
		return defaultTargetDataSource;
	}

	public abstract Object determineCurrentLookupKey();

}
