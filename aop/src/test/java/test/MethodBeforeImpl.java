package test;

import java.lang.reflect.Method;

import com.spring.aop.common.xml.inner.*;

public class MethodBeforeImpl implements MethodBeforeAdvice {

	public Object before(Method method, Object[] args, Object instance) {
		System.out.println(method.getName() + "方法执行前被拦截");
		return null;
	}

}
