package test.bean;

public class BigDog{

	public void testDog() throws Exception {
		System.out.println("BigDog.testDog()");
		throw new NullPointerException();
	}

	public String toRun() {
		System.out.println("BigDog.toRun()");
		return "欢乐的奔跑";
	}

	public void toTest() {
		System.out.println("BigDog.toTest()");
	}

}
