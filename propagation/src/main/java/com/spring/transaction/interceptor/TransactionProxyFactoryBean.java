package com.spring.transaction.interceptor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Properties;

import com.spring.aop.proxy.ProxyCore;
import com.spring.beans.aware.BeanFactoryAware;
import com.spring.beans.factory.BeanFactory;
import com.spring.beans.factory.XmlBeanFactory;
import com.spring.transaction.manager.InterfacePlatformTransactionManager;
import com.spring.transaction.manager.JdbcTransactionObjectSupport;
import com.spring.transaction.support.Excution;
import com.spring.transaction.support.NameMatchTransactionAttributeSource;
import com.spring.transaction.support.TransactionDefinition;

public class TransactionProxyFactoryBean extends ProxyCore implements BeanFactoryAware {
	private InterfacePlatformTransactionManager transactionManager;
	private Properties transactionAttributes;
	private NameMatchTransactionAttributeSource transactionAttributeSource;
	private BeanFactory beanFactory;
	private boolean proxyTargetClass;
	private TransactionCglibFactory cglib;
	
	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
		throws Throwable {
		return transactionManage(method, args); // 事务管理器
	}

	@Override
	public void setTarget(Object bean) {
		Class<?>[] interfaces = bean.getClass().getInterfaces(); // 获得其实现的接口类
		if (isProxyTargetClass() || noUsedInterface(interfaces)) {
			if (cglib == null) 
				cglib = new TransactionCglibFactory();
			this.setTargetProperty(cglib.getProxyBean(this, bean));
		} else {
			this.setTargetProperty(Proxy.newProxyInstance(
					bean.getClass().getClassLoader(), interfaces, this));
		}
		this.setObject(bean);
	}


	/**
	 * 事务的运行法则
	 * @param method
	 * @param args
	 * @return
	 * @throws Throwable
	 */
	public Object transactionManage(Method method, Object[] args) throws Throwable {
		Object result = null;
		TransactionDefinition definition = null;
		try {
			if (this.transactionAttributes != null) {
				definition = JdbcTransactionObjectSupport
						.getTransactionDefinition(transactionAttributes, method);
			} else if (this.transactionAttributeSource != null) {
				definition = JdbcTransactionObjectSupport
						.getTransactionDefinition(transactionAttributeSource
								.getProperties(), method);
			}
			if (definition == null || definition.getDefinition() == null) {
				definition.setDefinition(TransactionDefinition.PROPAGATION_REQUIRES_NEW); //没匹配到的方法以非事务方式运行
			}
			Excution excution = new Excution(method, getObject(), args);
			transactionManager.openTransaction(definition);
			result = transactionManager.execute(definition, excution);
			transactionManager.commit();
		} catch (Throwable e) {
			transactionManager.rollback(e);
			if (e instanceof RuntimeException || e instanceof Error) {
				throw e; 
			} else {
				e.printStackTrace();
			}
		} finally {
			transactionManager.closeTransaction();
		}
		
		return result;
	}

	public boolean isProxyTargetClass() {
		return proxyTargetClass;
	}

	public void setProxyTargetClass(boolean proxyTargetClass) {
		this.proxyTargetClass = proxyTargetClass;
	}
	
	private boolean noUsedInterface(Class<?>[] interfaces) {
		return interfaces == null || interfaces.length == 0;
	}
	
	
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

	public BeanFactory getBeanFactory() {
		return beanFactory;
	}

	public TransactionCglibFactory getCglib() {
		return cglib;
	}

	public void setCglib(TransactionCglibFactory cglib) {
		this.cglib = cglib;
	}

}
