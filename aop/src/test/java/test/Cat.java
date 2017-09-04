package test;

import java.util.List;

public class Cat {
	private String name;
	private String type; //品种
	private int[] arr;
	private String[] arr2;
	private List list;
	
	public List getList() {
		return list;
	}
	public void setList(List list) {
		this.list = list;
	}
	public String[] getArr2() {
		return arr2;
	}
	public void setArr2(String[] arr2) {
		this.arr2 = arr2;
	}
	public int[] getArr() {
		return arr;
	}
	public void setArr(int[] arr) {
		this.arr = arr;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public void testAop1() {
		System.out.println("test1>>>>>>>>>>>>>22");
	}
	public void testAop2() {
		System.out.println("test2>>>>>>>>>>>>>");
	}
}
