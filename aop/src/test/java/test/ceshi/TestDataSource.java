package test.ceshi;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.spring.beans.factory.XmlBeanFactory;
import com.spring.beans.resource.ClassPathResource;

public class TestDataSource {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		XmlBeanFactory factory = new XmlBeanFactory(new ClassPathResource("applicationContext.xml"));
		PersonDao dao = (PersonDao) factory.getBean("personDao");
		/******
		List<Map<String,Object>> result = dao.listPersons();
		for (Map<String,Object> map : result) {
			for (Map.Entry<String, Object> en : map.entrySet()) {
				System.out.print("-" + en.getKey() + " : " + en.getValue() + "-");
			}
			System.out.println();
		}
		*****/
		//dao.addPerson("张三");
		System.out.println(dao.getValue().getClass().getName());
		factory.destroySingletons();
	}

}
