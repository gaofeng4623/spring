package test.aop;

import java.lang.reflect.Method;

import com.spring.aop.common.xml.inner.ClassFilter;
import com.spring.aop.common.xml.inner.MethodMatcher;
import com.spring.aop.common.xml.inner.Pointcut;

public class MyPointcut implements Pointcut{

	public ClassFilter getClassFilter() {
		return new MyClassFilter();
	}

	public MethodMatcher getMethodMatcher() {
		return new MyMethodMatcher();
	}

	public boolean matchesClass(Class clazz) {
		return getClassFilter().matches(clazz);
	}

	public boolean matchesMethod(Method method, Class targetClass, Object[] args) {
		return getMethodMatcher().matches(method, targetClass)
		|| getMethodMatcher().matches(method, targetClass, args);
	}

}
