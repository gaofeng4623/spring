package test.anno;

import java.sql.SQLException;

import javax.annotation.Resource;

import com.spring.beans.common.annotation.Autowired;
import com.spring.beans.common.annotation.DependsOn;
import com.spring.beans.common.annotation.PostConstruct;
import com.spring.beans.common.annotation.PreDestroy;
import com.spring.beans.common.annotation.Qualifier;
import com.spring.beans.common.annotation.Scope;
import com.spring.beans.common.annotation.Service;

@Scope("prototype")
@Service("test1")
@DependsOn("cat007")
public class Test1 {
	@Resource
	private Cat cat;
	
	@Autowired
	@Qualifier("dogImpl")
	private Dog dog;

	private Pig pig;
	
	@Resource(name="aopService")
	private test.Service service;
	
	public Pig getPig() {
		return pig;
	}
	
	@Resource(name="pig")
	public void setPig(Pig pig) {
		this.pig = pig;
	}

	public Dog getDog() {
		return dog;
	}

	public void setDog(Dog dog) {
		this.dog = dog;
	}

	@PostConstruct
	public void init() {
		System.out.println("Test1初始化方法......");
	}
	@PreDestroy
	public void destroy() {
		System.out.println(this + "被销毁");
	}
	public void runCat() {
		try {
			cat.runCat("测试");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void runDog() {
		dog.runDog();
	}
	
	public void runPig() {
		pig.runPig();
	}

	public void service() {
		service.goTest();
	}
	
	public Cat getCat() {
		return cat;
	}

	public void setCat(Cat cat) {
		this.cat = cat;
	}

}
