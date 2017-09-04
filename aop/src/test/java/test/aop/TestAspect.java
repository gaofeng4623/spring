package test.aop;

import com.spring.beans.common.exception.BeanNotFoundException;
import com.spring.beans.factory.context.ApplicationContext;
import com.spring.context.ClassPathXmlApplicationContext;
import test.anno.Cat;

public class TestAspect {

	/**
	 * @param args 
	 * @throws BeanNotFoundException 
	 */
	public static void main(String[] args) throws Exception {
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		Cat cat = (Cat) context.getBean("cat007");
		System.out.println(cat.getClass().getName());
		cat.runCat("测试");
	}

}
