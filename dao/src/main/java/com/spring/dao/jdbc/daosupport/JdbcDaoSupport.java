package com.spring.dao.jdbc.daosupport;

import javax.sql.DataSource;

import com.spring.beans.aware.BeanFactoryAware;
import com.spring.beans.factory.BeanFactory;


public abstract class JdbcDaoSupport implements BeanFactoryAware{
	private DataSource dataSource;
	private JdbcTemplate jdbcTemplate;
	private BeanFactory beanFactory;

	public DataSource getDataSource() {
		return dataSource;
	}

	public final void setDataSource(DataSource dataSource) {
		if (this.jdbcTemplate == null
				|| dataSource != this.jdbcTemplate.getDataSource()) {
			this.jdbcTemplate = createJdbcTemplate(dataSource);
			initTemplateConfig();
		}
	}
	
	
	private JdbcTemplate createJdbcTemplate(DataSource dataSource) {
		return new JdbcTemplate(dataSource, beanFactory);
	}
	
	//初始化配置
	private void initTemplateConfig() {
		
	}
	
	/***对数据库的支持****/
	private boolean paseValue(String value) {
		 if (value.equals("oracle.jdbc.driver.OracleDriver")||
			 value.equals("com.microsoft.jdbc.sqlserver.SQLServerDriver")|| 
			 value.equals("com.mysql.jdbc.Driver") || 
			 value.equals("com.ibm.db2.jdbc.app.DB2Driver") ||
			 value.equals("com.sybase.jdbc.SybDriver") ||
			 value.equals("com.pointbase.jdbc.jdbcUniversalDriver")
			)
			return true;
		return false;
	}

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

}
