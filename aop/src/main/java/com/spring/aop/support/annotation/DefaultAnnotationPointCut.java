package com.spring.aop.support.annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.spring.aop.resolver.ArgsMatcher;
import com.spring.aop.resolver.Matcher;
import com.spring.aop.resolver.annotation.AnnotationExecution;
import com.spring.aop.resolver.annotation.AnnotationWithin;

public class DefaultAnnotationPointCut extends AbstractAnnotationPointCut {
	private String model = "normal";
	private ArgsMatcher args;
	private List executions;
	
	public DefaultAnnotationPointCut() {
		this.executions = new ArrayList();
	}
	
	@Override
	public void initClassConfig(String expression) {
		String item = null;
		String[] arr = null;
		if (expression.contains("||")) {
			this.model = "||";
			arr = expression.split("||");
		} else if (expression.contains("&&")) {
			this.model = "&&";
			arr = expression.split("&&");
		} else {
			arr = new String[]{expression};
		}
		for (int i = 0; i < arr.length; i++) {
			item = arr[i].trim();
			if (item.contains("execution") || item.contains("within")) {
				Matcher exc = getExecution(item); 
				getExecutions().add(exc);
			} else if (item.contains("args")) {
				args = new ArgsMatcher(item);
				args.setParamName(item); //指定匹配方法参数名
			}
		}
	}
	
	/****
	 * 获得单个表达式对象
	 * @param item
	 * @return
	 */
	private Matcher getExecution(String item) {
		boolean not = false;
		Matcher matcher = null;
		item = item.replaceFirst("^\\s+", ""); //去掉开头的空格
		item = item.replaceFirst("\\s+$", ""); //去掉结尾空格
		if (item.startsWith("!")) {
			item = item.replaceFirst("^\\!", "");
			not = true;
		}
		if (item.contains("execution")) {
			item = item.replace("execution", "");
			item = item.replaceFirst("^\\(", ""); //去掉开始的括号
			item = item.replaceFirst("\\)$", ""); //去掉末尾的括号
			AnnotationExecution exc = new AnnotationExecution(item);
			exc.setNot(not);
			matcher = exc;
		} else if (item.contains("within")) {
			item = item.replace("within", "");
			item = item.replaceFirst("^\\(", ""); //去掉开始的括号
			item = item.replaceFirst("\\)$", ""); //去掉末尾的括号
			AnnotationWithin aw = new AnnotationWithin(item);
			aw.setNot(not);
			matcher = aw;
		}
		
		
		return matcher;
	}
	
	
	public static void main(String[] args) {
		DefaultAnnotationPointCut dp = new DefaultAnnotationPointCut();	
		String item = "(public * testAnno..*.runCat(..))";
		item = item.replaceFirst("^\\(", ""); //去掉开始的括号
		item = item.replaceFirst("\\)$", ""); //去掉末尾的括号
		System.out.println("处理后 " + item);
	}
	
	
	@Override
	public boolean matched(Class<?> cl, Method method) {
		Matcher mt = null;
		if ("normal".equals(model)) {
			mt = (Matcher) getExecutions().get(0);
			return mt.matched(cl, method);
		} else if ("&&".equals(model)) {
			for (int i = 0; i < getExecutions().size(); i++) {
				mt = (Matcher) getExecutions().get(i);
				if (!mt.matched(cl, method))
					return false;
			}
			if (getArgs() != null) { //参数匹配
				if (!getArgs().matchMethodParam(cl, method))
					return false;
			}
			return true;
		} else if ("||".equals(model)){
			for (int i = 0; i < getExecutions().size(); i++) {
				mt = (Matcher) getExecutions().get(i);
				if (mt.matched(cl, method))
					return true;
			}
			return false;
		}
		return false;
	}
	
	/**
	 * 对外提供接口，容器加载时匹配代理
	 */
	public boolean matchedClass(Class cl){ 
		Matcher mt = null;
		if ("normal".equals(model)) {
			mt = (Matcher) getExecutions().get(0);
			return mt.matchedClass(cl);
		} else if ("&&".equals(model)) {
			for (int i = 0; i < getExecutions().size(); i++) {
				mt = (Matcher) getExecutions().get(i);
				if (!mt.matchedClass(cl))
					return false;
			}
			return true;
		} else if ("||".equals(model)){
			for (int i = 0; i < getExecutions().size(); i++) {
				mt = (Matcher) getExecutions().get(i);
				if (mt.matchedClass(cl))
					return true;
			}
			return false;
		}
		return false;
	}
	
	
	public boolean matchClass(String className) {
		return false;
	}

	public ArgsMatcher getArgs() {
		return args;
	}

	public void setArgs(ArgsMatcher args) {
		this.args = args;
	}
	
	public List getExecutions() {
		return executions;
	}

	public void setExecutions(List executions) {
		this.executions = executions;
	}
	
	
}
