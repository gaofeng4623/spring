package test.aop;

import java.lang.reflect.Method;

import com.spring.aop.common.xml.inner.MethodBeforeAdvice;

public class MyAdvice implements MethodBeforeAdvice{

	public Object before(Method method, Object[] args, Object instance) {
		System.out.println(method.getName()+"执行前被拦截");
		return null;
	}

}
