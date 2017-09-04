package com.spring.aop.resolver;

import java.util.regex.Pattern;

public class ClassNameMatcher extends AbstractClassNameMatcher {
	private boolean not = false;
	private boolean interfaceModel = false;
	private String classNameRegx;

	public boolean isNot() {
		return not;
	}

	public void setNot(boolean not) {
		this.not = not;
	}

	public boolean isInterfaceModel() {
		return interfaceModel;
	}

	public void setInterfaceModel(boolean interfaceModel) {
		this.interfaceModel = interfaceModel;
	}

	public String getClassNameRegx() {
		return classNameRegx;
	}

	public void setClassNameRegx(String classNameRegx) {
		this.classNameRegx = classNameRegx;
	}

	public ClassNameMatcher() {
	}

	public ClassNameMatcher(String classNameRegx) {
		this.classNameRegx = classNameRegx;
	}

	/*****
	 * 匹配外部执行的类
	 */
	public boolean matchClass(String className) {
		if (isInterfaceModel()) {
			try {
				Class cl = Class.forName(className);
				Class[] interfaces = cl.getInterfaces();
				return isNot() ? !matchInterface(interfaces)
						: matchInterface(interfaces);
			} catch (ClassNotFoundException e) {
				System.out.println("ClassNameMatcher.matchClass(" + className
						+ ")" + e.getMessage());
			}
		} else {
			return isNot() ? !Pattern.matches(classNameRegx, className)
					: Pattern.matches(classNameRegx, className);
		}

		return false;
	}

	/*****
	 * 匹配这个类实现的接口
	 * 
	 * @param interfaces
	 * @return
	 */
	private boolean matchInterface(Class[] interfaces) {
		if (interfaces == null)
			return false;
		for (int i = 0; i < interfaces.length; i++) {
			if (Pattern.matches(classNameRegx, interfaces[i].getName()))
				return true;
		}
		return false;
	}

}
