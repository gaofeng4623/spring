package com.spring.aop.proxy;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;

import com.spring.aop.common.xml.advisor.PointcutAdvisor;
import com.spring.aop.common.xml.inner.Advisor;
import com.spring.aop.support.ExcutionChain;
import com.spring.aop.support.annotation.AnnotationModel;
import com.spring.beans.common.SourceItem;
import com.spring.beans.factory.IocBaseContext;
import com.spring.beans.factory.XmlBeanFactory;

public class ProxyFactoryBean extends ProxySupport {
	
	public ProxyFactoryBean() {
		super();
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		Object result = null;
		Advisor advisor = null;
		ExcutionChain chain = new ExcutionChain();
		if (getInterceptorNames() == null) return null;
		for (int i = 0; i < getInterceptorNames().size(); i++) {
			if (getInterceptorNames().get(i) == null) continue;
			advisor = (Advisor) getInterceptorNames().get(i);
			if (advisor.matchesMethod(method, getObject().getClass(), args)) {
				chain.fillChain(advisor.getAdvice());
			} 
		}
		result = chain.excuteChain(method, args, getObject());
		
		return result;
	}
	
	/******************************AOP接口开始**********************************/

	/***
	 * 进行AOP切面代理,用于对非单例的bean进行过滤,此处可以进行并行整合，把aop和事务都并列执行，而不是目前只能执行之一。
	 * @param si
	 * @param beanFactory
	 */
	public void doAopFilter(SourceItem si, XmlBeanFactory beanFactory) {
		/**** 切面代理开始 ****/
		try {
			getAopTarget(si, beanFactory.getAdvisors());
			if (beanFactory.aspectScanable()) {
				setAspectTarget(si, beanFactory);
			}
			IocBaseContext.setTransactionProxy(si, beanFactory); //织入事务代理
		} catch (Exception e) {
			e.printStackTrace();
		}
		/**** 切面代理结束 ****/
	}

	/***
	 * 此处进行所有AOP的代理
	 * @param si
	 * @param advisors
	 */
	public void getAopTarget(SourceItem si, List<?> advisors) {
		Object target = si.getSource();
		if (si.isUpdated() || target instanceof PointcutAdvisor) return;
			for (Iterator<?> ite = advisors.iterator(); ite.hasNext();) {
				PointcutAdvisor advisor = (PointcutAdvisor) ite.next();
				if (advisor.matchesClass(si.getSourceId(), target.getClass())) {
					advisor = (PointcutAdvisor) advisor.clone();
					advisor.setTarget(target);
					target = advisor.getTarget();
					si.setSource(target); // 更新代理对象
					si.setUpdated(true); // 代理记录
					break;
				}
			}
	}

	
	/*** 
	 * 进行aspect切面代理
	 *  
	 * @param si
	 * @param factory
	 */
	public void setAspectTarget(SourceItem si, XmlBeanFactory factory) {
		try {
			if (allowableProxy(si, factory)) {
				List<AnnotationModel> advices = (List<AnnotationModel>) factory.getAnnotationAdvices();
				if (matchedClass(si.getSource(), advices)) { //匹配容器的类,选择性代理
					ProxyAspectAnotation paa = new ProxyAspectAnotation();
					paa.setAspectCglibable(factory.isAspectCglibable());
					paa.setAdvices(advices);
					paa.setTarget(si.getSource());
					si.setSource(paa.getResult()); // 更新代理完成
					paa.setBeanFactory(factory);
					si.setUpdated(true);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	private boolean matchedClass(Object target, List<AnnotationModel> advices) {
		AnnotationModel advice = null;
		for (int i = 0; i < advices.size(); i++) {
			advice = advices.get(i);
			if (advice.matchedClass(target.getClass())) {
				return true;
			}
		}
		return false;
	}
	
	private boolean allowableProxy(SourceItem si, XmlBeanFactory factory) {
		return factory.aspectScanable() && !si.isUpdated() 
				&& !(si.getSource() instanceof PointcutAdvisor);
	}
		
	/******************************AOP接口结束**********************************/
}
