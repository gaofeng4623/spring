package com.spring.aop.resolver.annotation;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

import com.spring.aop.resolver.ClassNameMatcher;
import com.spring.aop.resolver.Matcher;

public class AnnotationExecution extends AbstractAnnotationExcuteInter implements Matcher{
	private boolean not = false; //当前表达式的外层运算 ！
	private String model = "normal"; //匹配的运算模式
	private String[] exceptions = null; //异常类型,匹配时简写和全拼都要匹配
	private String modifier = null; //修饰符
	private String returnType = null; //返回值类型
	private String paramTypeRegx = "?"; //方法参数类型
	private String methodNameRegx = ""; //方法名的匹配规则
	private String classNameRegx = null; //类名的匹配规则
	private Matcher[] classNameMatchers = null; //类匹配器
	
	public AnnotationExecution() {
		
	}
	
	public AnnotationExecution(String expression) {
		initClassConfig(expression);
	}
	
	public boolean matched(Class<?> cl, Method method) {
		return isNot() ? !(matchClass(cl.getName()) && matchMethod(method))
				: matchClass(cl.getName()) && matchMethod(method);
	}
	
	/**
	 * 对外暴露接口，容器加载时匹配代理
	 */
	public boolean matchedClass(Class<?> cl){ 
		return isNot() ? !matchClass(cl.getName())
				: matchClass(cl.getName());
	}
	
	public boolean matchClass(String className) {
		Matcher cnm = null;
		if ("normal".equals(model)) {
			cnm = classNameMatchers[0];
			return cnm.matchClass(className);
		} else if ("&&".equals(model)) {
			for (int i = 0 ; i < classNameMatchers.length; i++) {
				cnm = classNameMatchers[i];
				if (!cnm.matchClass(className))
					return false;
			}
			return true;
		} else if ("||".equals(model)){
			for (int i = 0 ; i < classNameMatchers.length; i++) {
				cnm = classNameMatchers[i];
				if (cnm.matchClass(className))
					return true;
			}
			return false;
		}
		return false;
	}
	
	public boolean matchMethod(Method method) {
		//多种方法汇总,临时为true
		if (!matchMethodName(method.getName()))
			return false;
		if (!matchMethodParam(null, method))
			return false;
		if (!matchExceptions(method))
			return false;
		if (!matchReturnType(method))
			return false;
		return true;
	}
	
	public boolean matchMethodName(String methodName) {
		if (methodName == null) return false;
		return Pattern.matches(methodNameRegx, methodName.trim());
	}
	
	
	//匹配方法参数个数及类型
	public boolean matchMethodParam(Class<?> cl, Method method) {
		Class<?>[] types = method.getParameterTypes();
		if ("".equals(paramTypeRegx.trim())) {
			if (types.length > 0) return false;
		} else if ("..".equals(paramTypeRegx.trim())){
			return true;
		} else if ("*".equals(paramTypeRegx.trim())) { //任何类型的单参数
			if (types.length != 1) return false;
		} else {
			return paramTypeRegx.trim()
				.equals(getParamTypes(method));
		}
		return true; //续写
	}
	
	
	//匹配异常
	public boolean matchExceptions(Method method) {
		if (this.exceptions == null) return true;
		Class<?>[] exptypes = method.getExceptionTypes();
		for (String e : exceptions) {
			for (Class<?> s : exptypes) {
				if (!e.trim().equals(s.getSimpleName()))
					return false;
			}
		}
		return true;
	}
	
	public boolean matchReturnType(Method method) {
		if ("*".equals(returnType.trim())) return true;
		Class<?> returnType = method.getReturnType();
		return this.returnType.trim().equals(returnType);
	}
	
	
	private String getParamTypes(Method method) {
		String paramTypes = "";
		StringBuffer sb = new StringBuffer();
		Class<?>[] types = method.getParameterTypes();
		if (types.length > 0) {
			for (Class<?> c : types) {
				sb.append(c.getName()).append(",");
			}
			paramTypes = sb.toString();
			paramTypes = paramTypes.substring(0, paramTypes.length() - 1);
		}
		return paramTypes;
		
	}
	
	
	//解析类中的配置,只解析单个excution
	/**
	 * @param expression
	 */
	public void initClassConfig(String expression) {
		System.out.println("expression ---- " + expression);
		String[] classNameRegxs = null; //可能带有&&或||的类匹配条件
		if (!expression.contains("@")) {
			String regx = "[\\(](\\.\\.|\\*|(\\*\\,)*\\w+\\d*(\\.\\w+\\d*)*(\\,\\*)*(\\,.*)*|s*)[\\)]";
			String[] result = expression.split(regx);
			for (int i =0 ; i < result.length; i++) {
				String item = result[i];
				paramTypeRegx = i == 0 ? expression.replace(item, "")
						: paramTypeRegx.replace(item, "");
				if (item.contains("throws")) {
					item = item.replace("throws", "");
					if (item.contains(",")) {
						exceptions = item.trim().split(",");
					} else {
						exceptions = new String[]{item.trim()};
					}
				} else {
					item = item.replaceFirst("^\\s+", ""); //去掉开头的空格
					if (Pattern.matches("^(public).*|^(protected).*|^(private).*", item)) {
						modifier = item.substring(0, item.indexOf(" ")).trim();
						item = item.replaceFirst("^(public)|^(protected)|^(private)", "");
						item = item.replaceFirst("^\\s+", "");
					} else {
						modifier = "*";
					}
					//处理返回值类型
					if (item.startsWith("*")) { 
						returnType = "*"; 
						item = item.replaceFirst("^\\*", "");
						item = item.replaceFirst("^\\s+", "");
					} else {
						returnType = item.substring(0,item.indexOf(" "));
						item = item.substring(item.indexOf(" "),item.length());
						item = item.replaceFirst("^\\s+", "");
					}
					//处理方法名的匹配规则
					if ("*".equals(item)) { //任何类的任何方法名
						classNameRegx = ".*";
						methodNameRegx = ".*";
						classNameMatchers = new ClassNameMatcher[]{
								new ClassNameMatcher(classNameRegx)};
					} else if (!item.contains(".")) { //直接写方法名的，例(* chilout(..))
						classNameRegx = ".*";
						methodNameRegx = item.trim().replace("*", ".*");
						classNameMatchers = new ClassNameMatcher[]{
								new ClassNameMatcher(classNameRegx)};
					} else {
						methodNameRegx = item.substring(item.lastIndexOf(".") + 1,item.length());
						methodNameRegx = methodNameRegx.replace("*", ".*");
						classNameRegx = item.substring(0, item.lastIndexOf("."));
						System.out.println("classNameRegx = " + classNameRegx);
						if (classNameRegx.startsWith("(") && classNameRegx.endsWith(")")) {
							classNameRegx = classNameRegx.replaceAll("\\(|\\)", "");
						}
						//处理类匹配的运算符
						if (classNameRegx.contains("&&")) {
							model = "&&";
							classNameRegxs = classNameRegx.split("&&");
						} else if (classNameRegx.contains("||")) {
							model = "||";
							classNameRegxs = classNameRegx.split("||");
						} else {
							classNameRegxs = new String[]{classNameRegx};
						}
						classNameMatchers = getClassNameMatchers(classNameRegxs);
						
					}
				}
			}
			/**
			 * 支持无参()、单参(*)、任意参(..)及类型参(java.util.Date)
			 * 或多类型参(java.util.Date,java.util.List)
			 */
			paramTypeRegx = paramTypeRegx.replaceAll("\\(|\\)", ""); //参数类型表达式
			
			System.out.println("modifier = " + modifier);
			System.out.println("returnType = " + returnType);
			System.out.println("methodNameRegx = " + methodNameRegx);
			System.out.println("paramType = " + paramTypeRegx.trim());
			if (exceptions != null) {
				for (String s : exceptions) {
					System.out.println("异常:" + s);
				}
			}
			for (Matcher c : classNameMatchers) {
				ClassNameMatcher cm = (ClassNameMatcher) c;
				System.out.println("....................");
				System.out.println("isnot = " + cm.isNot());
				System.out.println("isInterfaceModel = " + cm.isInterfaceModel());
				System.out.println("classNameRegx = " + cm.getClassNameRegx());
			}
			
		} else {
			//包含注解运算符的
		}
	}
	
	
	
	
	//测试运行结果
	public static void main(String[] args) {
		AnnotationExecution ect = new AnnotationExecution();
		String point1 = "public * cn.javass..IpointService.*(..) " +
		"throws ArrayIndexOutException,NullPointException";
		ect.initClassConfig(point1);
		System.out.println("------------------------------------------");
		AnnotationExecution ect2 = new AnnotationExecution();
		String point2 = "* cn.javass..IpointCut*.test*(java.util.HashMap,..) " +
		"throws ArrayIndexOutException,NullPointException";
		ect2.initClassConfig(point2);
		System.out.println("------------------------------------------");
		AnnotationExecution ect3 = new AnnotationExecution();
		String point3 = "java.util.ArrayList (!cn.javass..IpointCut*+).test*(java.util.HashMap) " +
		"throws ArrayIndexOutException,NullPointException";
		ect3.initClassConfig(point3);
		System.out.println("------------------------------------------");
		AnnotationExecution ect4 = new AnnotationExecution();
		String point4 = "public * *(..)";
		ect4.initClassConfig(point4);
		System.out.println("------------------------------------------");
		AnnotationExecution ect5 = new AnnotationExecution();
		String point5 = "* (cn.javass..IpointService+ && java.io.Serializable+).*(*,java.lang.String,..) " +
				"throws ArrayIndexOutException,NullPointException";
		ect5.initClassConfig(point5);
		System.out.println("------------------------------------------");
		AnnotationExecution ect6 = new AnnotationExecution();
		String point6 = "* enjoy(..)";
		ect6.initClassConfig(point6);
		System.out.println("------------------------------------------");
		AnnotationExecution ect7 = new AnnotationExecution();
		String point7 = "public * testAnno..*.runCat(..)";
		ect7.initClassConfig(point7);
	}
	
	
	
	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String[] getExceptions() {
		return exceptions;
	}

	public void setExceptions(String[] exceptions) {
		this.exceptions = exceptions;
	}

	public String getModifier() {
		return modifier;
	}

	public void setModifier(String modifier) {
		this.modifier = modifier;
	}

	public String getReturnType() {
		return returnType;
	}

	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}

	public String getParamTypeRegx() {
		return paramTypeRegx;
	}

	public void setParamTypeRegx(String paramTypeRegx) {
		this.paramTypeRegx = paramTypeRegx;
	}

	public String getMethodNameRegx() {
		return methodNameRegx;
	}

	public void setMethodNameRegx(String methodNameRegx) {
		this.methodNameRegx = methodNameRegx;
	}

	public String getClassNameRegx() {
		return classNameRegx;
	}

	public void setClassNameRegx(String classNameRegx) {
		this.classNameRegx = classNameRegx;
	}

	public boolean isNot() {
		return not;
	}

	public void setNot(boolean not) {
		this.not = not;
	}

}
