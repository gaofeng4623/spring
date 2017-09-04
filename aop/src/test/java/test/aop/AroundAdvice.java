package test.aop;

import com.spring.aop.common.xml.inner.MethodInterceptor;
import com.spring.aop.common.xml.inner.MethodInvocation;

public class AroundAdvice implements MethodInterceptor{

	public Object invoke(MethodInvocation methodInvocation) throws Throwable {
		System.out.println(">>AroundAdvice.before1()");
		Object result = methodInvocation.proceed();
		System.out.println(">>AroundAdvice.after1()");
		return result;
	}

}
