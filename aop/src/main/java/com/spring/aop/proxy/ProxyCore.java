package com.spring.aop.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * 为动态代理的提供辅助
 */
public abstract class ProxyCore implements InvocationHandler {
    private Object object;
    private Object target;

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public Object getTarget() {
        return target;
    }

    public void setTargetProperty(Object bean) {
        this.target = bean;
    }

    public abstract void setTarget(Object bean);

    public abstract Object invoke(Object proxy, Method method, Object[] args) throws Throwable;
}
