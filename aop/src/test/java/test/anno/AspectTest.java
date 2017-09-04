package test.anno;

import com.spring.aop.common.annotation.After;
import com.spring.aop.common.annotation.AfterReturning;
import com.spring.aop.common.annotation.AfterThrowing;
import com.spring.aop.common.annotation.Around;
import com.spring.aop.common.annotation.Aspect;
import com.spring.aop.common.annotation.Before;
import com.spring.aop.common.annotation.Pointcut;
import com.spring.aop.support.annotation.runtime.JoinPoint;
import com.spring.aop.support.annotation.runtime.ProceedingJoinPoint;
import com.spring.beans.common.annotation.Component;
@Component
@Aspect
public class AspectTest {
	@Pointcut(value = "execution(public * test.anno..*.init())")
	public void pointcut() {
	}
	
	@Pointcut(value = "within(test.anno..*)")
	public void pointcut2() {
	}
	
	@Before("pointcut2()")
	public void beforeAdvice() {
		System.out.println("=========== within before advice");
	}

	@Before(value = "pointcut()", argNames = "name")
	public void beforeAdvice(JoinPoint joinPoint, String name) {
		System.out.println("=========== before advice param	: " + name);
	}
	
	@AfterReturning(value = "execution(* cn.javass..*.sayBefore(..))",
			pointcut = "execution(public * test.anno..*.init()) && args(name)",
			argNames = "retVal", returning = "retVal") //注意argNames必须包含returning
	public void afterReturningAdvice(Object retVal) {
		System.out.println("=========== after returning advice retVal:" + retVal); 
	}
	
	@AfterThrowing(value="pointcut()", 
			argNames="param,exception", throwing = "exception") 
	public void afterThrowingAdvice(String param, Exception exception) { 
		System.out.println("=========== after throwing advice exception:" + exception);
	} 
	
	@Around(value="pointcut()") 
	public Object aroundAdvice(ProceedingJoinPoint pjp) throws Throwable {	
		System.out.println("=========== around before advice"); 
		Object retVal = pjp.proceed(); 
		System.out.println("=========== around after advice"); 
		return retVal; 
	} 
	
	@After(pointcut="execution(public * test.anno..*.init())") 
	public void afterFinallyAdvice() { 
		System.out.println("=========== after finally advice"); 
	}
	
}
