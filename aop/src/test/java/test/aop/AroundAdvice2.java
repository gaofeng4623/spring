package test.aop;

import com.spring.aop.common.xml.inner.MethodInterceptor;
import com.spring.aop.common.xml.inner.MethodInvocation;

public class AroundAdvice2 implements MethodInterceptor{

	public Object invoke(MethodInvocation methodInvocation) throws Throwable {
		System.out.println(">>AroundAdvice.before2()");
		Object result = methodInvocation.proceed();
		System.out.println(">>AroundAdvice.after2()");
		return result;
	}

}