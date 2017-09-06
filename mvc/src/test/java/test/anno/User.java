package test.anno;

import java.io.File;

public class User {
	private int id;
	private String name;
	private int age;
	private String sex;
	private String passWord;
	private double salary;
	private String[] loves;
	private User person;
	private File attach;
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public String getPassWord() {
		return passWord;
	}
	public void setPassWord(String passWord) {
		this.passWord = passWord;
	}
	public User getPerson() {
		return person;
	}
	public void setPerson(User person) {
		this.person = person;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public double getSalary() {
		return salary;
	}
	public void setSalary(double salary) {
		this.salary = salary;
	}
	public String[] getLoves() {
		return loves;
	}
	public void setLoves(String[] loves) {
		this.loves = loves;
	}
	public File getAttach() {
		return attach;
	}
	public void setAttach(File attach) {
		this.attach = attach;
	}
	
	public User(int id) {
		this.id = id;
	}
	
	public User() {
		
	}
	
	public static void main(String[] args) {
		User u = new User(2);
	}
}
