package test;

import com.spring.beans.factory.XmlBeanFactory;
import com.spring.beans.resource.ClassPathResource;

public class TestXml {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		XmlBeanFactory factory = new XmlBeanFactory(
				new ClassPathResource(new String[]{"xmlTest.xml","applicationContext.xml"}));
		Engine engine = (Engine) factory.getBean("engine");
		engine.todo();

	}

}
