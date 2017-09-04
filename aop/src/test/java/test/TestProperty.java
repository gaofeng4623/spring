package test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import test.anno.Pig;

import com.spring.beans.factory.XmlBeanFactory;
import com.spring.beans.factory.config.CustomEditorConfigurer;
import com.spring.beans.propertyeditors.CustomDateEditor;
import com.spring.beans.resource.ClassPathResource;

public class TestProperty {
	private List list;
	private List listc; // 构造器集合
	private List<Person> persons;
	private Person person;
	private Set personSet;
	private Map maps;
	private Properties properties;

	public TestProperty() {
	}

	public TestProperty(List list, Person person, Set personSet, Map maps) {
		this.list = list;
		this.person = person;
		this.personSet = personSet;
		this.maps = maps;
	}

	public Map getMaps() {
		return maps;
	}

	public void setMaps(Map maps) {
		this.maps = maps;
	}

	public Set getPersonSet() {
		return personSet;
	}

	public void setPersonSet(Set personset) {
		this.personSet = personset;
	}

	public List getList() {
		return list;
	}

	public void setList(List list) {
		this.list = list;
	}

	public List<Person> getPersons() {
		return persons;
	}

	public void setPersons(List<Person> persons) {
		this.persons = persons;
	}

	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	public Properties getProperties() {
		return properties;
	}

	public void setProperties(Properties properties) {
		this.properties = properties;
	}

	public List getListc() {
		return listc;
	}

	public void setListc(List listc) {
		this.listc = listc;
	}

	public void testList() {
		System.out.println("-------------测试list集合基本数据类型-------------");
		for (int i = 0; i < list.size(); i++) {
			System.out.println(list.get(i));
		}
	}

	public void testPersons() {
		System.out.println("-------------测试list集合内置对象-------------");
		for (int i = 0; i < persons.size(); i++) {
			Person p = persons.get(i);
			System.out.println(p.getName() + "-" + p.getAge() + "-"
					+ p.getSex() + "-" + p.getBirthday());
		}
	}

	public void testPerson() {
		System.out.println("-------------测试内置对象-------------");
		System.out.println(person.getName() + "-" + person.getAge() + "-"
				+ person.getSex() + "-" + person.getBirthday());
	}

	public void testPersonset() {
		System.out.println("-------------测试set集合内置对象-------------");
		for (Iterator it = personSet.iterator(); it.hasNext();) {
			Person p = (Person) it.next();
			System.out.println(p.getName() + "-" + p.getAge() + "-"
					+ p.getSex() + "-" + p.getBirthday());
		}
	}

	public void testMaps() {
		System.out.println("-------------测试map集合注入-------------");
		for (Iterator it = maps.keySet().iterator(); it.hasNext();) {
			String key = (String) it.next();
			System.out.println(key + ":" + maps.get(key));
		}
	}

	public void testProps() {
		System.out.println("-------------测试properties集合注入-------------");
		for (Iterator it = properties.keySet().iterator(); it.hasNext();) {
			String key = (String) it.next();
			System.out.println(key + ":" + properties.getProperty(key));
		}
	}

	public void testConstruct1() {
		System.out
				.println("\r\n以下是构造器测试1*************************************\r\n");
		testList();
		testPerson();
		testPersonset();
		testMaps();
	}

	public void testListc() {
		System.out
				.println("\r\n测试注入的构造集合*************************************\r\n");
		for (Iterator it = listc.iterator(); it.hasNext();) {
			Dao dao = (Dao) it.next();
			dao.print("");
		}
	}

	public static void main(String[] args) throws Exception {
		XmlBeanFactory factory = new XmlBeanFactory(new ClassPathResource(
				"propertyTest.xml"));
		TestProperty tp = (TestProperty) factory.getBean("testp");
		tp.testList();
		tp.testPersons();
		tp.testPerson();
		tp.testPersonset();
		tp.testMaps();
		tp.testProps();
		/***** 注入构造集合测试 ***/
		tp.testListc();
		/******** 构造器测试开始 *********/
		TestProperty tpc = (TestProperty) factory.getBean("testc");
		tpc.testConstruct1();
		System.out.println("------------------以下是构造器单元测试-----------------");
		List list = (List) factory.getBean("conslist");
		System.out.println(list.size());
		Map map = (Map) factory.getBean("consMap");
		System.out.println(map.get("中国"));
		Properties pro = (Properties) factory.getBean("consProp");
		System.out.println(pro.getProperty("北京市"));
		Set set = (Set) factory.getBean("consSet");
		System.out.println(set.size());
		String s = "";
		CustomEditorConfigurer config = factory.getBean(CustomEditorConfigurer.class);
		Map data = config.getCustomEditors();
		CustomDateEditor editor = (CustomDateEditor)data.get("java.util.Date");
		SimpleDateFormat sdf = (SimpleDateFormat)editor.getFormat();
		System.out.println(sdf.format(new Date()));
		System.out.println("------------------以下是日期注入测试-----------------");
		Person p = (Person)factory.getBean("person");
		System.out.println("birthday = " + p.getBirthday());
		System.out.println("salary = " + p.getSalary());
		System.out.println("debug = " + p.isDebug());
	}
}
