package test;

import com.spring.beans.factory.context.ApplicationContext;
import com.spring.context.ClassPathXmlApplicationContext;


public class TestContext {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		Engine engine = (Engine) context.getBean("engine");
		//engine.todo();
		System.out.println(engine);
		Engine engine2 = (Engine) context.getBean("engine");
		System.out.println(engine2); //不是单例,所以每次创建
		Dao dao = (Dao) context.getBean("nametest");
		dao.print("通过name获得");
		context.destroy();
	}

}
