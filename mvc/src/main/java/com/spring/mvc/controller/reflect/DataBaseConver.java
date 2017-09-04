package com.spring.mvc.controller.reflect;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.spring.beans.common.utils.Server;
import com.spring.beans.factory.BeanFactory;
import com.spring.beans.factory.config.EditorConfigurer;
import com.spring.mvc.common.ModelMap;
import com.spring.mvc.common.Util;

public class DataBaseConver implements Conver{
	private BeanFactory factory;
	private EditorConfigurer configer;
	
	public DataBaseConver(BeanFactory factory, EditorConfigurer configer){
		this.factory = factory;
		this.configer = configer;
	}
	
	public BeanFactory getFactory() {
		return factory;
	}

	public void setFactory(BeanFactory factory) {
		this.factory = factory;
	}

	public void reflectFormData(int num, String key, Class<?> paramType,
			Object[] parameters, Annotation[][] annotations, HttpServletRequest request, 
			Map<String, String>pathVariableMap, ModelMap modelMap) 
		throws InstantiationException, IllegalAccessException {
		String defaultValue = Util.getParamDefaultValue(num, annotations);
		if (hasProperty(key, request, paramType)) {
			String[] values = request.getParameterValues(key);
			if (Server.isFileProperty(paramType)) {
				parameters[num] = null;
			} else {
				Object value = parseObjectArray(paramType, values);
				if (Server.isEmpty(value) && !Server.isEmpty(defaultValue)) {
					value = defaultValue; //默认值
				}
				parameters[num] = Server.converParams(paramType, value, getEditorConfigurer());
			}
			
		} else {
			if (Server.isBaseType(paramType)) {
				//进行PathVariable注入
				Object value = Util.getPathVariableValue(key, annotations[num], pathVariableMap);
				if (!Server.isEmpty(value)) {
					value = Server.converParams(paramType, value, getEditorConfigurer());
				} else { //默认值
					value = !Server.isEmpty(defaultValue) ?
							Server.converParams(paramType, defaultValue, getEditorConfigurer())
						  : Server.getDefaultParamValue(paramType);
				}
				parameters[num] = value;
			} else if (Server.isFileProperty(paramType)) {
				parameters[num] = null;
			} else if (paramType.isArray() || paramType.isInterface()) {
				//判断子类型是否包含映射关系
				if (paramType.isArray()) {
					
				} else if (Collection.class.isAssignableFrom(paramType)) {
					
				} else {
					parameters[num] = null;
				}
			} else {
				try {
					//选择是否从modelMap中取值
					Object pojo = Util.getInstanceOfParamPojo(modelMap, key, annotations[num], paramType); 
					parameters[num] = pojo;
					setCommentFormBean(pojo, request);
				} catch (Exception e) {
					parameters[num] = null;
				}
			}
		}
		//解析属性注解
		Util.handleAttributesByParamAnnotation(num, parameters, annotations, request); 
	}




	/**********
	 *********【方法参数和request字段相似度】****
	 ********/

	public boolean hasProperty(String property, HttpServletRequest request, Class<?> paramType) {
		if (Server.isBaseType(paramType)) {
			for (Enumeration en = request.getParameterNames(); en.hasMoreElements();) {
				String name = (String) en.nextElement();
				if (property.equals(name))
					return true;
			}
		}
		return false;
	}
	
	/**********
	 *********【匹配FormBean的pojo属性和request字段相似度】****
	 ********/
	
	private boolean hasChildProperty(String keyname, HttpServletRequest request) {
		for (Enumeration<String> en = request.getParameterNames(); en.hasMoreElements();) {
			String name = en.nextElement();
			if (name.startsWith(keyname + "."))
				return true;
		}
		return false;
	}
	
	
	
	/********
	 ********【映射普通表单的属性到FormBean】******
	 **********/
	public void setCommentFormBean(Object action, HttpServletRequest request) {
		Class<?> cl = action.getClass();
		Field[] fields = cl.getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			try {
				PropertyDescriptor property = new PropertyDescriptor(fields[i]
						.getName(), cl);
				if (hasProperty(property.getName(), request, property.getPropertyType())) {
					setFormBeanFiled(action, property, request);
				} else if (hasChildProperty(property.getName(), request)) {
					setFormBeanPoJo(action, property, property.getName(), request);
				}
				
			} catch (Exception e) {
				System.out.println("setCommentFormBean() " + e.getMessage());
			} 
		}

	}

	
	/***********
	 * 【映射表单字段值到FormBean属性】*************
	 * 
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws InvocationTargetException
	 * @throws IllegalArgumentException
	 */

	public void setFormBeanFiled(Object action, PropertyDescriptor property,
			HttpServletRequest request) throws InstantiationException,
			IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {
		if (Server.isFileProperty(property.getPropertyType())) return; //过滤上传文件
		Method md = property.getWriteMethod();
		String name = property.getName();
		Object value = request.getParameterValues(name);
		value = parseObjectArray(property.getPropertyType(), value);
		value = Server.converParams(property.getPropertyType(), value, getEditorConfigurer()); // 数据类型转化
		md.invoke(action, value);
	}
	
	/**********
	 * 【映射表单字段值到FormBean的内置POJO】**********
	 * 如果属性是数组、接口或基本数据类型,则跳过创建,当且仅当formBean中含有但在request中不存在的属性
	 * @throws ClassNotFoundException
	 * @throws InvocationTargetException
	 * @throws IllegalArgumentException
	 */

	public void setFormBeanPoJo(Object action, PropertyDescriptor pd, String baseName,
			HttpServletRequest request) throws InstantiationException,
			IllegalAccessException, ClassNotFoundException,
			IllegalArgumentException, InvocationTargetException {
		
		if (Server.isFilteringType(pd.getPropertyType())) return;
		Method write = pd.getWriteMethod();
		String typeName = pd.getPropertyType().getName();
		Object object = Class.forName(typeName).newInstance();
		Class<?> cl = object.getClass();
		Field[] fields = cl.getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			try {
				PropertyDescriptor property = new PropertyDescriptor(fields[i]
						.getName(), cl);
				String keyname = baseName + "." + property.getName();
				Method md = property.getWriteMethod();
				if (hasProperty(keyname, request, property.getPropertyType())) {
					if (Server.isFileProperty(property.getPropertyType())) continue;  //过滤上传文件类型
					Object value = request.getParameterValues(keyname);
					value = parseObjectArray(property.getPropertyType(), value);
					value = Server.converParams(property.getPropertyType(), value, getEditorConfigurer()); // 数据类型转化
					md.invoke(object, value);
				} else if (hasChildProperty(keyname, request)) {
					setFormBeanPoJo(object, property, keyname, request);
				}
			} catch (Exception e) {
				System.out.println("setFormBeanPoJo(): " + e.getMessage());
			} 
		}
		write.invoke(action, object); // 嵌入新创建的POJO
	}
	
	
	/***
	 * 解析字符串数组
	 * @param value
	 * @return
	 */
	public Object parseObjectArray(Class<?> type, Object value){
		if (value != null) {
			String[] arr = (String[]) value;
			if (Server.existArray(type)) {
				value = arr;
			} else if (!type.isArray()) {
				value = arr[0];
			}
		}
		return value;
	}

	public void doAfter() {
		
	}

	@Override
	public void setEditorConfigurer(EditorConfigurer configer) {
		this.configer = configer;
	}

	@Override
	public EditorConfigurer getEditorConfigurer() {
		return this.configer;
	}

}
