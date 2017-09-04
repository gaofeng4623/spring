package test.ceshi;

import com.spring.beans.common.annotation.Repository;

@Repository("ceshi")
public class Ceshi {
	public String ceshi() {
		System.out.println(">>>>>>>>>ceshi");
		return "ceshi";
	}
}
