package test;

import com.spring.beans.aware.InitializingBean;

public class ServiceImpl implements Service ,InitializingBean{
	private Dao dao;

	public String todo(String word) {
		dao.print(word);
		return "拦截器测试";
	}

	public Dao getDao() {
		return dao;
	}

	public void setDao(Dao dao) {
		this.dao = dao;
	}

	public String testService() {
		System.out.println("ServiceImpl.test()方法在执行");
		return "返回喽";
	}

	public void other() {
		
	}
	
	public void close() {
		System.out.println("调用了关闭方法!");
	}

	public void init() {
		System.out.println("ServiceImpl初始化方法，一般建表用到");
	}

	public String toTest() {
		// TODO Auto-generated method stub
		return null;
	}

	public String goTest() {
		System.out.println("ServiceImpl.goTest()方法在执行");
		return "goTest执行完毕";
	}

	public void afterPropertiesSet() throws Exception {
		System.out.println("初始化InitializingBean......");
	}
}
