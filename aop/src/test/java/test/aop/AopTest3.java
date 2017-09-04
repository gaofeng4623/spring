package test.aop;

import test.Cat;
import test.Service;
import test.bean.BigDog;


import com.spring.beans.factory.XmlBeanFactory;
import com.spring.beans.resource.ClassPathResource;

public class AopTest3 {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		/***
		 * 测试初始化功能
		 */
		XmlBeanFactory factory = new XmlBeanFactory(new ClassPathResource("springaop.xml"));
		Service service = (Service) factory.getBean("service");
		try {
			service.goTest();
		} catch (Exception e) {
			e.printStackTrace();
		}
		//System.out.println("运行结果 : "+result);
	}

}
