package com.spring.context;

import java.util.Iterator;
import java.util.Map;

import com.spring.beans.common.SourceItem;
import com.spring.beans.common.exception.BeanNotFoundException;
import com.spring.beans.common.utils.Consts;
import com.spring.beans.factory.IocBaseContext;
import com.spring.beans.factory.XmlBeanFactory;
import com.spring.beans.factory.context.ApplicationContext;
import com.spring.beans.resource.ClassPathResource;
import com.spring.context.loader.ContextLoader;

public class ClassPathXmlApplicationContext implements ApplicationContext {
	private XmlBeanFactory beanFactory;

	public ClassPathXmlApplicationContext(Object xmls) throws Exception {
		String key = null;
		SourceItem result = null;
		XmlBeanFactory factory = null;
		if (xmls instanceof String)
			factory = new XmlBeanFactory(new ClassPathResource((String) xmls));
		else if (xmls instanceof String[])
			factory = new XmlBeanFactory(new ClassPathResource((String[]) xmls));
		this.beanFactory = factory;
		ContextLoader loader = new ContextLoader();
		loader.fillContextStack(xmls, factory); //装载适配器和通知
		factory.setContextModel(true); //启动context容器模式
		//注意：XmlBeanFactory已经完成AOP代理
		for (Iterator<String> it = loader.getBeanList().iterator(); it.hasNext();) { 
			key = it.next();
			factory.getBean(key);
			result = factory.getBeanInfo(key);
			if (result == null) continue;
			loader.reflectContextAware(result, null, this);
		}
		if (factory.iocScanable()) { //开启IOC注解扫描
			Map<String, SourceItem> annotations = factory.getAnnotationBeansByDirectory(factory.getBasePackage());
			for (Iterator<String> it = annotations.keySet().iterator(); it.hasNext();) {
				key = it.next();
				SourceItem si = annotations.get(key);
				loader.reflectContextAware(si, null, this);
				IocBaseContext.doAopFilter(si, factory); //注解代理
			}
		}
	}


	public XmlBeanFactory getBeanFactory() {
		return beanFactory;
	}

	public void setBeanFactory(XmlBeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	/*****
	 * 从上下文中获取bean对象,参数key应改为Object类型
	 * @throws Exception **
	 * */
	public Object getBean(Object param) throws Exception {
		Object bean = this.getBeanFactory().getBean(param);
		if (bean == null) {
			throw new BeanNotFoundException("the bean " 
					+ param.toString() + " has not found");
		}
		return bean;
	}
	
	/****
	 * 查找指定类型的bean
	 * @param <T>
	 * @param type
	 * @return
	 * @throws Exception 
	 */
	public <T> T getBean(Class<T> type) throws Exception {
		Object bean = getBean((Object)type);
		return type.cast(bean);
	}
	
	public boolean isSingleton(String scope) {
		return scope != null && !scope.equals(Consts.prototype);
	}
	
	
	/****
	 * **容器销毁时调用*** *
	 ******/
	public void destroy() {
		this.beanFactory.destroySingletons();
	}
	
}
