package com.spring.beans.factory;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.sql.DataSource;

import com.spring.beans.common.SourceItem;

/***
 * IOC集成环境,集成AOP、DAO层及事务
 * 
 * @author Administrator
 * 
 */
public class IocBaseContext {
	private static Properties ps;
	static {
		try {
			ps = new Properties();
			InputStream is = IocBaseContext.class.getClassLoader()
					.getResourceAsStream("com/spring/beans/factory/context.properties");
			ps.load(is);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static String getProperty(String name) {
		return ps.getProperty(name);
	}

	// 粗略获得方法，必须没有重构的方法。
	private static Method getMethod(Class<?> cl, String methodName) {
		Method[] methods = cl.getMethods();
		for (Method md : methods) {
			if (md.getName().equals(methodName)) {
				return md;
			}
		}
		return null;
	}

	/************ Aspect开始 **************/

	/***
	 * 筛选aspect切面对象
	 * @param set
	 * @param globalPointcuts
	 * @return
	 */
	public static List<?> getAspectClasses(Set<?> set,
			Map<String, Object> globalPointcuts) {
		try {
			Class<?> serviceCl = Class
					.forName(getProperty("aspectCoreService"));
			Object service = serviceCl.newInstance();
			Method method = getMethod(serviceCl, "getAspectClasses");
			return (List<?>) method.invoke(service, new Object[] { set,
					globalPointcuts });
		} catch (Exception e) {
			return null;
		}

	}

	/***
	 * 根据方法名获取各种通知
	 * @param cl
	 * @param aspect
	 * @param globalPointcuts
	 * @return
	 */
	public static List<?> getAdviceByMethods(Class<?> cl, Object aspect,
			Map<String, Object> globalPointcuts) {
		try {
			Class<?> serviceCl = Class
					.forName(getProperty("aspectCoreService"));
			Object setvice = serviceCl.newInstance();
			Method method = getMethod(serviceCl, "getAdviceByMethods");
			if (method != null) {
				return (List<?>) method.invoke(setvice, new Object[] { cl,
						aspect, globalPointcuts });
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/************ Aspect结束 **************/

	/************ Aop代理开始 *****************/
	public static boolean isProxyFactoryBean(Class<?> target) {
		try {
			Class<?> proxy = Class.forName(getProperty("proxyCore"));
			return proxy.isAssignableFrom(target);
		} catch (ClassNotFoundException e) {
			return false;
		}
	}

	public static boolean isAbstractDataSource(Class<?> target) {
		try {
			Class<?> proxy = Class.forName(getProperty("abstractDataSource"));
			return proxy.isAssignableFrom(target);
		} catch (ClassNotFoundException e) {
			return false;
		}
	}

	public static Object getReflectProxyBean(Object bean) {
		try {
			Class<?> proxy = Class.forName(getProperty("proxyCore"));
			if (isProxyFactoryBean(bean.getClass())) {
				Method method = getMethod(proxy, "getTarget");
				return method.invoke(bean);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bean;
	}

	public static Object getObject(Object bean) {
		Method method = getMethod(bean.getClass(), "getObject");
		if (method != null) {
			try {
				return method.invoke(bean);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/***
	 * 进行AOP切面代理
	 * @param si
	 * @param beanFactory
	 */
	public static void doAopFilter(SourceItem si, XmlBeanFactory beanFactory) {
		try {
			Class<?> proxy = Class.forName(getProperty("proxyFactoryBean"));
			Method method = getMethod(proxy, "doAopFilter");
			method.invoke(proxy.newInstance(), new Object[] { si, beanFactory });
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	/**
	 * 此处进行所有AOP的代理
	 * @param key
	 * @param si
	 * @param advisors
	 */
	public void getAopTarget(String key, SourceItem si, List<?> advisors) {
		try {
			Class<?> proxy = Class.forName(getProperty("proxyFactoryBean"));
			Method method = getMethod(proxy, "getAopTarget");
			method.invoke(proxy.newInstance(),
					new Object[] { key, si, advisors });
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/***
	 * 进行aspect切面代理
	 * 
	 * @param si
	 * @param factory
	 */
	public void setAspectTarget(SourceItem si, List advices,
			XmlBeanFactory factory) {
		try {
			Class<?> proxy = Class.forName(getProperty("proxyFactoryBean"));
			Method method = getMethod(proxy, "setAspectTarget");
			method.invoke(proxy.newInstance(), new Object[] { si, advices,
					factory });
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/************ Aop代理结束 *****************/

	/************ Spring事务开始 ********/

	// 判断适配器是否是TransactionInterceptor
	public static boolean isTransactionInterceptor(Object object) {
		try {
			Class<?> proxy = Class
					.forName(getProperty("transactionInterceptor"));
			return proxy.isAssignableFrom(object.getClass());
		} catch (ClassNotFoundException e) {
			return false;
		}
	}

	// 用于TransactionInterceptor的方法匹配
	public static boolean matchesMethod(Method method,
			Object transactionInterceptor) {
		try {
			Class<?> proxy = Class
					.forName(getProperty("transactionInterceptor"));
			Method md = getMethod(proxy, "matchesMethod");
			Object result = md.invoke(transactionInterceptor, method);
			return (Boolean) result;
		} catch (Exception e) {
			return false;
		}
	}

	// 创建并执行事务代理
	public static Object execute(Method method, Object[] args, Object object,
			Object transactionInterceptor) {
		try {
			Class<?> proxy = Class
					.forName(getProperty("transactionInterceptor"));
			Method md = getMethod(proxy, "execute");
			return md.invoke(transactionInterceptor, new Object[] { method,
					args, object });
		} catch (Exception e) {
			return null;
		}
	}

	public static Object getTransactionDataSource(DataSource dataSource) {
		try {
			Class<?> proxy = Class.forName(getProperty("proxyDatasource"));
			Method method = getMethod(proxy, "newInstance");
			return method.invoke(null, dataSource);
		} catch (Exception e) {
			return dataSource; // 非事务运行
		}
	}

	// 创建事务代理
	public static Object createTransactionProxy(Object object,
			BeanFactory factory) {
		try {
			Class<?> proxy = Class
					.forName(getProperty("transactionProxyCreater"));
			Method md = getMethod(proxy, "createTransactionProxy");
			return md.invoke(null, new Object[] { object, factory }); // 生成代理
		} catch (Exception e) {
			return object; // 非事务运行
		}
	}

	/*
	 * 注入容器事務代理
	 * 
	 * @param SourceItem
	 * 
	 * @param XmlBeanFactory
	 */
	public static void setTransactionProxy(SourceItem si, XmlBeanFactory factory) {
		try {
			Class<?> proxy = Class
					.forName(getProperty("transactionProxyCreater"));
			Method method = getMethod(proxy, "setTransactionProxy");
			method.invoke(null, new Object[] { si, factory });
		} catch (Exception e) {
		}
	}

	/*
	 * 判断一个类是否是事务管理器
	 */
	public static boolean isTransactionManager(Class<?> cls) {
		try {
			Class<?> transactionManager = Class
					.forName(getProperty("transactionManager"));
			return transactionManager.isAssignableFrom(cls);
		} catch (ClassNotFoundException e) {
			return false;
		}
	}

	/******************** Spring事务结束 *****************/

	/***************** controller装载开始 *****************/

	/**
	 * 装载controller配置信息
	 * 
	 * @param beanId
	 * @param cl
	 * @param controllers
	 */
	public static void fillControllerPool(String beanId, Class<?> cl,
			Map<String, Object> controllers, List<?> exceptionHandles,
			BeanFactory factory) {
		try {
			Class<?> proxy = Class.forName(getProperty("annotationController"));
			Method method = getMethod(proxy, "fillControllerPool");
			method.invoke(null, new Object[] { beanId, cl, controllers,
					exceptionHandles, factory });
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	/**
	 * 检测一个类型是否实现mvc
	 * 模块的HandleMapping接口
	 * @param cls
	 * @return
	 */
	public static boolean isHandleMapping(Class<?> cls) {
		try {
			Class<?> proxy = Class.forName(getProperty("handleMapping"));
			return proxy.isAssignableFrom(cls);
		} catch (ClassNotFoundException e) {
			return false;
		}
	}

	/***
	 * 检测一个类型是否实现aop
	 * 模块的PointcutAdvisor接口
	 * @param cls
	 * @return
	 */
	public static boolean isPointcutAdvisor(Class<?> cls) {
		try {
			Class<?> proxy = Class.forName(getProperty("pointcutAdvisor"));
			return proxy.isAssignableFrom(cls);
		} catch (ClassNotFoundException e) {
			return false;
		}
	}

	/***************** controller装载结束 *****************/

}
