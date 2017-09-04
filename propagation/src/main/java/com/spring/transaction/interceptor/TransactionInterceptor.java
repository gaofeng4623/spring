package com.spring.transaction.interceptor;

import java.lang.reflect.Method;
import java.util.Properties;

import com.spring.beans.aware.BeanFactoryAware;
import com.spring.beans.factory.BeanFactory;
import com.spring.beans.factory.XmlBeanFactory;
import com.spring.transaction.manager.InterfacePlatformTransactionManager;
import com.spring.transaction.manager.JdbcTransactionObjectSupport;
import com.spring.transaction.support.NameMatchTransactionAttributeSource;
import com.spring.transaction.support.TransactionDefinition;

public class TransactionInterceptor implements BeanFactoryAware{
	private InterfacePlatformTransactionManager transactionManager;
	private Properties transactionAttributes;
	private NameMatchTransactionAttributeSource transactionAttributeSource;
	private BeanFactory beanFactory;
	
	public InterfacePlatformTransactionManager getTransactionManager() {
		return transactionManager;
	}

	public void setTransactionManager(
			InterfacePlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	public Properties getTransactionAttributes() {
		return transactionAttributes;
	}

	public BeanFactory getBeanFactory() {
		return beanFactory;
	}

	public void setTransactionAttributes(Properties transactionAttributes) {
		this.transactionAttributes = transactionAttributes;
	}

	public NameMatchTransactionAttributeSource getTransactionAttributeSource() {
		return transactionAttributeSource;
	}

	public void setTransactionAttributeSource(
			NameMatchTransactionAttributeSource transactionAttributeSource) {
		this.transactionAttributeSource = transactionAttributeSource;
	}
	
	public void setBeanFactory(BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	public boolean matchesMethod(Method method) {
		TransactionDefinition definition = null;
		if (this.transactionAttributes != null) {
			definition = JdbcTransactionObjectSupport
					.getTransactionDefinition(transactionAttributes, method);
		} else if (this.transactionAttributeSource != null) {
			definition = JdbcTransactionObjectSupport
					.getTransactionDefinition(transactionAttributeSource
							.getProperties(), method);
		}
		return definition != null && definition.getDefinition() != null;
	}
	
	//执行点
	public Object execute(Method method, Object[] args, Object object) {
		Object result = null;
		TransactionProxyFactoryBean tpfb = new TransactionProxyFactoryBean();
		if (this.transactionAttributes != null) {
			tpfb.setTransactionAttributes(transactionAttributes);
		} else if (this.transactionAttributeSource != null) {
			tpfb.setTransactionAttributeSource(transactionAttributeSource);
		}
		tpfb.setTransactionManager(this.transactionManager);
		tpfb.setProxyTargetClass(this.beanFactory.isAspectCglibable());
		tpfb.setTarget(object); // cglib代理
		Object proxy = tpfb.getTarget();
		try {
			result = method.invoke(proxy, args);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return result;
	}


}
