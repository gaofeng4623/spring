package test.ceshi;
import test.Person;

public class Factory {
	
	public static Person createInstance() {
		Person p = new Person();
		p.setName("张三");
		p.setAge(29);
		return p;
	}
	
	public Person createInstance2() {
		Person p = new Person();
		p.setName("李四");
		p.setAge(40);
		return p;
	}
}
