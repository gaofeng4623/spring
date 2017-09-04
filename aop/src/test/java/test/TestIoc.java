package test;
import java.sql.SQLException;

import com.spring.beans.factory.XmlBeanFactory;
import com.spring.beans.resource.ClassPathResource;
public class TestIoc {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		XmlBeanFactory factory = new XmlBeanFactory(new ClassPathResource("applicationContext.xml"));
//		Service service = (Service)factory.getBean("aopService");
//		service.todo("todo方法正在被执行");
//		service.test();
//		service.other();
		Engine engine = (Engine) factory.getBean("engine");
		engine.todo();
//		Person person1 = (Person) factory.getBean("factory1");
//		System.out.println(person1.getName());
//		Person person2 = (Person) factory.getBean("factory2");
//		System.out.println(person2.getName());
//		Dao dao1 = (Dao) factory.getBean("nametest");
//		System.out.println("dao1 " +dao1);
//		Dao dao2 = (Dao) factory.getBean("nametest");
//		System.out.println("dao2 " +dao2);
//		Dao dao3 = (Dao) factory.getBean("idtest");
//		System.out.println("dao3 " + dao3);
//		//测试集合转换数组
		//Cat cat = (Cat) factory.getBean("globalCat");
//		for (Object o : cat.getArr()) {
//			System.err.println(o);
//		}
//		
//		for (Object o : cat.getArr2()) {
//			System.err.println(o);
//		}
		//List list = (List) factory.getBean("list");
		//List list2 = cat.getList();
		//System.out.println(list.hashCode() + "-对比-" + list2.hashCode());
		Service service = (Service) factory.getBean("service");
		Service service2 = (Service) factory.getBean("service");
		//System.err.println(service + "---" + service2); //因为是克隆的 
		service.goTest();
		factory.destroySingletons();
	}

	
}
