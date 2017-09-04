package test;

import java.lang.reflect.Method;

import com.spring.aop.common.xml.inner.*;

public class MethodAfterImpl implements AfterReturningAdvice {

	public void afterReturning(Object value, Method method, Object[] args,
			Object instance) {
		String name = method.getName();
		System.out.println(name + "方法执行后被拦截，返回值:" + value);
	}

}
