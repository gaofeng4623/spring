package testTrans.support;

import testTrans.TransactionInner;

import com.spring.beans.factory.context.ApplicationContext;
import com.spring.context.ClassPathXmlApplicationContext;

public class TestTransaction {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		ApplicationContext context = new ClassPathXmlApplicationContext("testTransaction.xml");
		TransactionInner ti = (TransactionInner) context.getBean("transactionSupportA");
		ti.test();
	}
}
