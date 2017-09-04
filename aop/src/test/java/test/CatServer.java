package test;

public class CatServer implements CatService{

	public void run() {
		System.out.println("MVC猫咪在奔跑!");
	}

	public void addCat(Cat cat) {
		System.out.println("MVC增加了一只" + cat.getName());
	}

	public void dance() {
		System.out.println("会跳舞的猫!--");
	}
}
