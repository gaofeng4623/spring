package test.aop;

import java.lang.reflect.*;

import com.spring.aop.common.xml.inner.ThrowsAdvice;

public class ThrowAdviceTest implements ThrowsAdvice{
	public String test(Method md, Object[] args, Object obj, Exception throwable) {
		System.out.println(md.getName() + "抛出异常被调用");
		return null;
	}
	
	public String test2(Object obj, NullPointerException e) {
		System.out.println("抛出异常被调用2"); 
		return null;
	}
}
