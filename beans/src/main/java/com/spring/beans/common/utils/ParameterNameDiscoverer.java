package com.spring.beans.common.utils;

import java.lang.reflect.Method;

import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.Modifier;
import javassist.NotFoundException;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;

/****
 * 该类依靠javassist字节码技术获取方法参数名
 * @author gaofeng
 *
 */
public class ParameterNameDiscoverer {
	
	public ParameterNameDiscoverer(){}
	
	public String[] getParameterNames(Class<?> cl, Method method) {
		String[] paramNames = null;
		 try {  
	            ClassPool pool = ClassPool.getDefault(); 
	            //官网找的针对javassist.NotFoundException异常补丁
	            pool.insertClassPath(new ClassClassPath(cl));  
	            CtClass cc = pool.get(cl.getName()); 
	            CtMethod cm = cc.getDeclaredMethod(method.getName());  
	            MethodInfo methodInfo = cm.getMethodInfo();  
	            CodeAttribute codeAttribute = methodInfo.getCodeAttribute();  
	            LocalVariableAttribute attr = (LocalVariableAttribute) codeAttribute.getAttribute(LocalVariableAttribute.tag);  
	            if (attr == null) {  
	                // exception  
	            }  
	            paramNames = new String[cm.getParameterTypes().length];  
	            int pos = Modifier.isStatic(cm.getModifiers()) ? 0 : 1;  
	            for (int i = 0; i < paramNames.length; i++)  
	                paramNames[i] = attr.variableName(i + pos);  
	            
	        } catch (NotFoundException e) {  
	            System.out.println("ParameterNameDiscoverer : " + e.getMessage());
	        }  
	        return paramNames;
	}

}
