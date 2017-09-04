package testTrans;

import com.spring.beans.factory.context.ApplicationContext;
import com.spring.context.ClassPathXmlApplicationContext;

public class TestAware {
	public static void main(String[] args) throws Exception {
		ApplicationContext context = new ClassPathXmlApplicationContext("testTransaction.xml");
	}
}
