package com.spring.aop.common.xml.advisor;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

import com.spring.aop.common.xml.pointcut.NameMatchMethodPointcut;

public class NameMatchMethodPiontcutAdvisor extends AdvisorAdapter {
	private String mappedName;
	private String[] mappedNames;

	public String getMappedName() {
		return mappedName;
	}

	public void setMappedName(String mappedName) {
		this.mappedName = mappedName;
	}

	public String[] getMappedNames() {
		return mappedNames;
	}

	public void setMappedNames(String[] mappedNames) {
		this.mappedNames = mappedNames;
	}

	@Override
	public boolean matchesMethod(Method method, Class targetClass, Object[] args) {

		if (getPointcut() != null) {
			if (!(getPointcut() instanceof NameMatchMethodPointcut)) {
				System.err
						.println("the Pointcut must be NameMatchMethodPointcut");
				return false;
			}
			return getPointcut().matchesMethod(method, targetClass, args);
		} else {
			if (this.mappedName != null) {
				return matches(mappedName, method.getName());
			} else if (this.mappedNames != null) {
				for (String regx : mappedNames) {
					if (matches(regx, method.getName()))
						return true;
				}
			}
		}
		return false;
	}

	// 通配符转化成正则表达式
	public boolean matches(String regx, String name) {
		if (regx.contains("*")) {
			regx = regx.replace("*", ".*");
		}
		return Pattern.matches(regx, name);
	}
}
