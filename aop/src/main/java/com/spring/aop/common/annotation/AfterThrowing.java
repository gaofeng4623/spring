package com.spring.aop.common.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target( { TYPE, FIELD, METHOD })
@Retention(RUNTIME)
public @interface AfterThrowing {
	public String value() default "";
	public String pointcut() default ""; //优先级高
	public String argNames() default "";
	public String throwing() default "";
	
	
}
