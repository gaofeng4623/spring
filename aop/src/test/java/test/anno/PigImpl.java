package test.anno;

import com.spring.beans.common.annotation.Repository;

@Repository
public class PigImpl implements Pig{
	private String name;
	
	public void runPig() {
		System.out.println("小花猪在吱吱叫...");
	}
	@Override
	public String getName() {
		return this.name;
	}
	@Override
	public void setName(String name) {
		this.name = name;
	}

}
