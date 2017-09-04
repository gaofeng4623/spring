package test.aop;

import java.lang.reflect.Method;

import com.spring.aop.common.xml.inner.MethodMatcher;

public class MyMethodMatcher implements MethodMatcher{

	public boolean isRuntime() {
		return true;
	}

	public boolean matches(Method method, Class<?> targetClass) {
		if (targetClass.getSimpleName().equals("Service")
				&& method.getName().contains("test"))
			return true;
		return false;
	}

	public boolean matches(Method method, Class<?> targetClass, Object[] args) {
		
		return false;
	}

}
