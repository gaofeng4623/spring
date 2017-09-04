package test.anno;

import test.ceshi.Ceshi;

import com.spring.beans.common.exception.BeanNotFoundException;
import com.spring.beans.factory.context.ApplicationContext;
import com.spring.context.ClassPathXmlApplicationContext;



public class TestContext {

	/**
	 * @param args 
	 * @throws BeanNotFoundException 
	 */
	public static void main(String[] args) throws Exception {
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		Cat cat = (Cat) context.getBean("cat007");
		cat.runCat("测试");
		//cat.init();
		//context.destroy();
		Ceshi ceshi = context.getBean(Ceshi.class);
		System.out.println(cat.getClass());
		ceshi.ceshi();
	}

}
