package com.spring.transaction.common.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.spring.transaction.common.Isolation;
import com.spring.transaction.common.Propagation;

@Target({ TYPE, FIELD, METHOD, PARAMETER })
@Retention(RUNTIME)
public @interface Transactional {
	
	public Class<?>[] rollbackFor() default {};
	
	public Class<?>[] noRollbackFor() default {};

	public String[] rollbackForClassname() default {};

	public String[] noRollbackForClassname() default {};

	public String propagation() default Propagation.REQUIRED;

	public String isolation() default Isolation.NONE; // 隔离级别

	public boolean readOnly() default false;

	public long timeout() default -1;
}
