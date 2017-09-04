package test.aop;

import com.spring.aop.common.xml.inner.ClassFilter;

public class MyClassFilter implements ClassFilter{

	public boolean matches(Class clazz) {
		if(clazz.getSimpleName().equals("Service"))
			return true;
		return false;
	}

}
