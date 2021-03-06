package com.spring.mvc.common.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target({TYPE, FIELD, METHOD})
@Retention(RUNTIME)
public @interface RequestMapping {
public String value() default "";
public String method() default "";
public String produces() default "";
}
