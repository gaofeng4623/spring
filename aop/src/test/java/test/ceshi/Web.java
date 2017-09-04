package test.ceshi;

import java.util.List;

import javax.annotation.Resource;

import com.spring.beans.common.annotation.Controller;
import com.spring.beans.common.annotation.Scope;



@Controller("test")
@Scope("request")
public class Web {

	@Resource
	private Person person;
	private String name;
	private int id;

	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public List findAllPerson() {
		return null;
	}

}
