package test.anno;

import java.util.Iterator;
import java.util.Set;

import com.spring.beans.factory.XmlBeanFactory;
import com.spring.beans.resource.ClassPathResource;

public class TestAll {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {  //报错了，检查！
		XmlBeanFactory factory = new XmlBeanFactory(new ClassPathResource("testAnnotation.xml"));
		Test1 test1 = (Test1) factory.getBean("test1");
		test1.runCat();
		test1.runDog();
		test1.runPig(); 
		//test1.service(); //内部实例化了aopService
		//factory.destroySingletons();
		//Test2 test2 = factory.getBean(Test2.class);
		//test2.test();

		factory.getBean("service"); //模拟上下文装载入容器
		factory.getBean("aopService");//模拟上下文装载入容器
		//测试代理
		test.Service service = factory.getBean(test.Service.class);
		System.out.println(":::::::" + service.getClass());   //测试代理有问题,找到两个目标是按理说要抛出异常的！，后期完善
		service.goTest();
		Set names = factory.getDataSourceNames();
		for (Iterator it = names.iterator(); it.hasNext(); ) {
			System.out.println("name -- " + it.next());
		}
	}
}
