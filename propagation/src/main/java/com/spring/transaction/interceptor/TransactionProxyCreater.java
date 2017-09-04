package com.spring.transaction.interceptor;

import java.lang.reflect.Method;
import java.util.Properties;

import com.spring.beans.common.SourceItem;
import com.spring.beans.factory.XmlBeanFactory;
import com.spring.transaction.common.annotation.Transactional;
import com.spring.transaction.manager.InterfacePlatformTransactionManager;

/**
 * @author 高峰
 * @info载入事务注解的配置
 * @info动态的为对象创建事务代理
 * 
 */
public class TransactionProxyCreater {

	/***
	 * 根据@Transactional创建事务代理
	 * @param object
	 * @param factory
	 * @return
	 * @throws Exception 
	 */
	public static Object createTransactionProxy(Object object, XmlBeanFactory factory) throws Exception {
		Class<?> cl = object.getClass();
		if (!hasTransactionAnnotation(cl)
				|| factory.getTransaction_manager() == null) {
			return object; // 没有注解的情况
		}
		Transactional tc = null;
		Properties transactionConfig = new Properties();
		Method[] methods = cl.getDeclaredMethods();
		for (Method method : methods) {
			if (method.isAnnotationPresent(Transactional.class)) {
				tc = (Transactional) method.getAnnotation(Transactional.class);
				String transactionAttribute = createTransactionAttribute(tc);
				transactionConfig.put(method.getName(), transactionAttribute);
			}
		}
		if (hasTransactionClassAnnotation(cl)) {
			tc = getTransactional(cl);
			String transactionAttribute = createTransactionAttribute(tc);
			transactionConfig.put("*", transactionAttribute);
		}
		TransactionProxyFactoryBean tpfb = new TransactionProxyFactoryBean();
		tpfb.setTransactionAttributes(transactionConfig);
		InterfacePlatformTransactionManager iptm = (InterfacePlatformTransactionManager) factory
				.getBean(factory.getTransaction_manager());
		tpfb.setTransactionManager(iptm);
		tpfb.setProxyTargetClass(factory.isAspectCglibable());
		tpfb.setTarget(object); // cglib代理
		return tpfb.getTarget();
	}
	
	/****
	 * 以上的方法中，如果两个方法名相同，而且都标记了@Transactional
	 * 在配置时就会产生两个同样的attribute,事务应用中方法名最好有一定规则。
	 */
	
	
	/*
	 * 处理@Transactional注解的继承关系
	 */
	@SuppressWarnings("unused")
	private static Transactional getTransactional(Class<?> cl) {
		if (cl.isAnnotationPresent(Transactional.class)) {
			return cl.getAnnotation(Transactional.class);
		} else if(cl.getSuperclass().isAnnotationPresent(Transactional.class)) {
			return cl.getSuperclass().getAnnotation(Transactional.class);
		} else {
			Class<?>[] classs = cl.getInterfaces();
			if (classs != null) {
				for (Class<?> c : classs) {
					if (c.isAnnotationPresent(Transactional.class)) {
						return c.getAnnotation(Transactional.class);
					}
				}
			}
			return null;
		}
	}
	
	
	/**
	 * @info 载入事务配置
	 * @param tc
	 * @return
	 */
	public static String createTransactionAttribute(Transactional tc) {
		boolean readOnly = tc.readOnly();
		Class<?>[] rollBack = tc.rollbackFor();
		String[] rfc = tc.rollbackForClassname();
		Class<?>[] noRollBack = tc.noRollbackFor();
		String[] nrc = tc.noRollbackForClassname();
		String isolation = tc.isolation();
		String propagation = tc.propagation();
		long timeout = tc.timeout();
		StringBuffer attributes = new StringBuffer();
		if (readOnly) {
			attributes.append("readOnly,");
		}
		if (!isEmpty(propagation)) {
			attributes.append(propagation).append(",");
		}
		if (!isEmpty(isolation)) {
			attributes.append(isolation).append(",");
		}
		if (timeout > 0) {
			attributes.append("timeout_").append(timeout).append(",");
		}
		if (rollBack != null && rollBack.length > 0) {
			for (Class<?> e : rollBack) {
				attributes.append("-").append(e.getName()).append(",");
			}
		}
		if (rfc != null && rfc.length > 0) {
			for (String s : rfc) {
				attributes.append("-").append(s).append(",");
			}
		}
		if (noRollBack != null && noRollBack.length > 0) {
			for (Class<?> e : noRollBack) {
				attributes.append("+").append(e.getName()).append(",");
			}
		}
		if (nrc != null && nrc.length > 0) {
			for (String s : nrc) {
				attributes.append("+").append(s).append(",");
			}
		}
		String value = attributes.toString();
		if (value.endsWith(",")) {
			value = value.substring(0, value.length() - 1);
		}
		return value;
	}

	public static boolean isEmpty(String test) {
		return test == null || test.trim().length() == 0;
	}

	public static boolean hasTransactionAnnotation(Class<?> cl) {
		boolean hasMethodAnnotation = false;
		boolean hasClasssAnnotation = hasTransactionClassAnnotation(cl);
		Method[] methods = cl.getDeclaredMethods();
		for (Method md : methods) {
			if (md.isAnnotationPresent(Transactional.class)) {
				hasMethodAnnotation = true;
				break;
			}
		}
		return hasClasssAnnotation || hasMethodAnnotation;

	}
	
	public static boolean hasTransactionClassAnnotation(Class<?> cl) {
		if (cl.isAnnotationPresent(Transactional.class)) {
			return true;
		} else if(cl.getSuperclass().isAnnotationPresent(Transactional.class)) {
			return true;
		} else {
			Class<?>[] classs = cl.getInterfaces();
			if (classs != null) {
				for (Class<?> c : classs) {
					if (c.isAnnotationPresent(Transactional.class)) {
						return true;
					}
				}
			}
		 return false;
	  }
   }
	

	//织入事务代理
	public static void setTransactionProxy(SourceItem si, XmlBeanFactory factory) {
		try {
			if (!si.isUpdated()) {
				if (hasTransactionAnnotation(si.getSource().getClass())) {
					//创建事务代理
					Object proxy = createTransactionProxy(si.getSource(), factory);
					si.setSource(proxy);
					si.setUpdated(true);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
