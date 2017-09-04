package com.spring.aop.common.xml.advisor;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

public class RegexpMethodPointcutAdvisor extends AdvisorAdapter{
	private String[] patterns;
	private String pattern;
	
	public String getPattern() {
		return pattern;
	}
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}
	public String[] getPatterns() {
		return patterns;
	}
	public void setPatterns(String[] patterns) {
		this.patterns = patterns;
	}
	
	@Override
	public boolean matchesMethod(Method method, Class targetClass, Object[] args) {
		String parse = targetClass.getName() + "." + method.getName();
		if (pattern != null) {
			return Pattern.matches(pattern, parse);
		} else if (patterns != null) {
			for (String regx : patterns) {
				if (Pattern.matches(regx, parse))
					return true;
			}
		}
		return false;
	}

}
