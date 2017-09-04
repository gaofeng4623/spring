package test;

import com.spring.beans.factory.XmlBeanFactory;
import com.spring.beans.resource.ClassPathResource;

public class TestAop {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		//ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		XmlBeanFactory factory = new XmlBeanFactory(new ClassPathResource("applicationContext.xml"));
		Service service = (Service) factory.getBean("aopService");
		//service.todo("1111111");
		service.toTest();
		service.goTest();
		//service.todo("success");
		//service.other();
		System.out.println(service instanceof Service);
		System.out.println(service.getClass().getName());
	}

}
