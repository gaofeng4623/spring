package test.anno;

import com.spring.beans.common.annotation.Component;

@Component
public class DogImpl implements Dog{

	public void runDog() {
		System.out.println("小黑狗在急速奔跑!");
	}

}
