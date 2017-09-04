package test.ceshi;

import java.util.List;

public interface PersonDao {
	public List listPersons();
	public void addPerson(String name) throws Exception;
	public Object getValue();
}
