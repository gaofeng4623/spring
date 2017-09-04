package test.aop;

import test.Cat;
import test.bean.BigDog;


import com.spring.beans.factory.XmlBeanFactory;
import com.spring.beans.resource.ClassPathResource;

public class AopTest {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		XmlBeanFactory factory = new XmlBeanFactory(new ClassPathResource("springaop.xml"));
		BigDog dog = factory.getBean(BigDog.class);
		try {
			dog.testDog();
		} catch (Exception e) {
			//e.printStackTrace();
		}
		//System.out.println("运行结果 : "+result);
	}

}
