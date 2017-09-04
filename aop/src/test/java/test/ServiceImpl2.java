package test;

public class ServiceImpl2 implements Service{
	private Dao dao;
	
	public Dao getDao() {
		return dao;
	}

	public void setDao(Dao dao) {
		this.dao = dao;
	}


	public String todo(String word) {
		//dao.print("内置对象执行了>>>>>>>>");
		System.out.println("todo方法在执行");
		return "拦截器测试";
	}
	

	public String testService() { 
		System.out.println("test方法在执行");
		return "返回喽";
	}
	

	public void other() {
		System.out.println(">>>>>>>other");
	}

	public String toTest() {
		System.out.println("toTest方法在执行");
		return "toTest";
	}

	public String goTest() {
		System.out.println("goTest方法在执行");
		return "goTest";
	}

}
