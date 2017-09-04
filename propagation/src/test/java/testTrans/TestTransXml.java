package testTrans;

import com.spring.beans.factory.context.ApplicationContext;
import com.spring.context.ClassPathXmlApplicationContext;

public class TestTransXml {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		ApplicationContext context = new ClassPathXmlApplicationContext("transaction.xml");
		try {
			InsertBean insert = (InsertBean)context.getBean("transactionRun");
			insert.insert("张三", 29);
			System.out.println(insert.getClass().getName());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
