package test.anno;

import java.sql.SQLException;

import com.spring.beans.common.annotation.PostConstruct;
import com.spring.beans.common.annotation.Repository;
//@Lazy
@Repository("cat007")
public class CatImpl implements Cat{
	
	public String runCat(String name) throws SQLException {
		System.out.println(name + "小花猫在欢乐的奔跑...");
		//if (2 > 1) throw new SQLException("查询异常");
		return "返回一只小猫";
	}
	@PostConstruct
	public void init() {
		System.err.println("CatImpl初始化......");
	}
}
