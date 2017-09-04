package test;
import com.spring.beans.factory.XmlBeanFactory;
import com.spring.beans.resource.ClassPathResource;

public class TestDepends {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		XmlBeanFactory factory = new XmlBeanFactory(new ClassPathResource("applicationContext.xml"));
		Engine engine = (Engine) factory.getBean("engine");
		Engine engine2 = (Engine) factory.getBean("engine");
		Service sc = (Service) factory.getBean("service");
		System.out.println("对比:" + engine.getServer() + " -- " + engine2.getServer()
				+ " -- " + sc);
		factory.destroySingletons();
		System.out.println("单例和依赖关系验证通过");
	}

}
