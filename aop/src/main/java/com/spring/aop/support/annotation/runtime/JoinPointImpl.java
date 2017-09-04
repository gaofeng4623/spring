package com.spring.aop.support.annotation.runtime;

public class JoinPointImpl implements JoinPoint {
	private Object proxy;
	private Object target;
	private Object[] args;
	private Signature signature;

	public JoinPointImpl() {
	}

	public JoinPointImpl(Signature signature, Object target, Object proxy,
			Object[] args) {
		this.signature = signature;
		this.proxy = proxy;
		this.target = target;
		this.args = args;
	}

	@Override
	public Object getThis() {
		return this.proxy;
	}

	@Override
	public Object getTarget() {
		return target;
	}

	@Override
	public Object[] getArgs() {
		return args;
	}

	@Override
	public Signature getSignature() {
		return this.signature;
	}

	@Override
	public String toShortString() {
		return target.getClass().getSimpleName();
	}

	@Override
	public String toLongString() {
		return target.getClass().getName();
	}

	public void setTarget(Object target) {
		this.target = target;
	}

	public void setSignature(Signature signature) {
		this.signature = signature;
	}

	public void setArgs(Object[] args) {
		this.args = args;
	}
}
