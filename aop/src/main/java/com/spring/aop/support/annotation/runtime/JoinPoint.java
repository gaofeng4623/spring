package com.spring.aop.support.annotation.runtime;


public interface JoinPoint {

	Object getThis();

    Object getTarget();
    
    Object[] getArgs();

    Signature getSignature();
    
    String toString();

    String toShortString();

    String toLongString();
}
