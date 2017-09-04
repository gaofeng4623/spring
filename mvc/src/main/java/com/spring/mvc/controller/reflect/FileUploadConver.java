package com.spring.mvc.controller.reflect;

import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;

import com.spring.beans.common.utils.Consts;
import com.spring.beans.common.utils.Server;
import com.spring.beans.factory.BeanFactory;
import com.spring.beans.factory.config.EditorConfigurer;
import com.spring.mvc.common.ModelMap;
import com.spring.mvc.common.Util;


public class FileUploadConver implements Conver{
	
	private String tempPath;
	private File tempDir;
	private BeanFactory factory;
	private EditorConfigurer configer;
	
	public FileUploadConver(BeanFactory factory, EditorConfigurer configer){
		this.factory = factory;
		this.configer = configer;
	}
	
	public BeanFactory getFactory() {
		return factory;
	}

	public void setFactory(BeanFactory factory) {
		this.factory = factory;
	}
	
	public String getTempPath() {
		return tempPath;
	}
	
	public void setTempPath(String tempPath) {
		this.tempPath = tempPath;
	}
	
	public File getTempDir() {
		return tempDir;
	}

	public void setTempDir(File tempDir) {
		this.tempDir = tempDir;
	}
	
	
	public void reflectFormData(int i, String key, Class<?> paramType,
			Object[] parameters, Annotation[][] annotations,
			HttpServletRequest request, Map<String, String> pathVariableMap,
			ModelMap modelMap) throws Exception {
		String defaultValue = Util.getParamDefaultValue(i, annotations);
		List<FileItem> fileList = ControllerContext.getUploadRequestList();
		if (hasProperty(key, fileList, paramType)) {
			FileItem[] fileItems = getFileItem(key, fileList);
			if (Server.isFileProperty(paramType)) {
				parameters[i] = createParamFile(paramType, fileItems);
			} else {
				Object value = parseObjectArray(paramType, fileItems);
				if (Server.isEmpty(value) && !Server.isEmpty(defaultValue)) {
					value = defaultValue; //默认值
				}
				parameters[i] = Server.converParams(paramType, value, getEditorConfigurer());
			}
			
		} else {
			if (Server.isBaseType(paramType)) {
				//进行PathVariable注入
				Object value = Util.getPathVariableValue(key, annotations[i], pathVariableMap);
				if (!Server.isEmpty(value)) {
					value = Server.converParams(paramType, value, getEditorConfigurer());
				} else { //默认值
					value = !Server.isEmpty(defaultValue) ?
							Server.converParams(paramType, defaultValue, getEditorConfigurer())
						  : Server.getDefaultParamValue(paramType);
				}
				parameters[i] = value;
			} else if (Server.isFileProperty(paramType)) {
				parameters[i] = null;
			} else if (paramType.isArray() || paramType.isInterface()) {
				//判断子类型是否包含映射关系
				if (paramType.isArray()) {
					
				} else if (Collection.class.isAssignableFrom(paramType)) {
					
				} else {
					parameters[i] = null;
				}
			} else {
				try {
					Object pojo = Util.getInstanceOfParamPojo(modelMap, key, annotations[i], paramType);
					parameters[i] = pojo;
					setUploadFormBean(pojo, request, fileList);
				} catch(Exception e) {
					parameters[i] = null;
				}
			}
		}
		//解析属性注解
		Util.handleAttributesByParamAnnotation(i, parameters, annotations, request); 
	}

	
	

	/********
	 ********【映射上传表单的属性到FormBean】******
	 * 
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws IllegalArgumentException
	 * @throws FileUploadException
	 * @throws IOException
	 **********/
	public void setUploadFormBean(Object action, HttpServletRequest request,
			List fileList) throws IllegalArgumentException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException, FileUploadException, IOException {
		
		Class cl = action.getClass();
		Field[] fields = cl.getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			try {
				PropertyDescriptor property = new PropertyDescriptor(fields[i]
						.getName(), cl);
				if (hasProperty(property.getName(), fileList, property.getPropertyType())) {
					FileItem[] fileItems = getFileItem(property.getName(), fileList);
					setFormBeanFiled(action, property, fileItems);
				} else if(hasChildProperty(property.getName(), fileList)){
					setFormBeanPoJo(action, property, property.getName(), fileList);
				}
			} catch (Exception e) {
				e.printStackTrace();
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
	 * @throws IOException
	 */

	public void setFormBeanFiled(Object action, PropertyDescriptor property,
			FileItem[] fileItems) {

		try {
			if (Server.isFileProperty(property.getPropertyType())) {
				setFormFile(action, property, fileItems);
			} else {
				Method md = property.getWriteMethod();
				String name = property.getName().toLowerCase();
				Object value = parseObjectArray(property.getPropertyType(), fileItems);
				value = Server.converParams(property.getPropertyType(), value, getEditorConfigurer()); // 数据类型转化
				md.invoke(action, value);
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	 }

	/**********
	 *  【映射表单字段值到FormBean的内置POJO】 
	 *  如果属性是数组、接口或基本数据类型,则跳过创建,当且仅当formbean中含有但在request中不存在的属性
	 *  ***********/

	public void setFormBeanPoJo(Object action, PropertyDescriptor pd, String baseName,
			List fileList) throws InstantiationException,
			IllegalAccessException, ClassNotFoundException,
			IllegalArgumentException, InvocationTargetException {
		
		if (Server.isFilteringType(pd.getPropertyType())) return;
		String typeName = pd.getPropertyType().getName();
		Method write = pd.getWriteMethod();
		Object object = Class.forName(typeName).newInstance(); 
		Class<?> cl = object.getClass();
		Field[] fields = cl.getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			try {
				PropertyDescriptor property = new PropertyDescriptor(fields[i]
						.getName(), cl);
				String keyname = baseName + "." + property.getName();
				if (hasProperty(keyname, fileList, property.getPropertyType())) {
					FileItem[] fileItems = getFileItem(keyname, fileList);
					if (Server.isFileProperty(property.getPropertyType())) {
						setFormFile(object, property, fileItems);
					} else {
						Method md = property.getWriteMethod();
						Object value = parseObjectArray(property.getPropertyType(), fileItems);
						value = Server.converParams(property.getPropertyType(), value, getEditorConfigurer()); // 数据类型转化
						md.invoke(object, value);
					}
				} else if (hasChildProperty(keyname, fileList)){
					setFormBeanPoJo(object, property, keyname, fileList);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} 
		}
		write.invoke(action, object); // 嵌入新创建的POJO
	}

	

	/******** 获取与属性对应的FileItem ***********/

	public FileItem[] getFileItem(String property, List<FileItem> fileList) {
		List<FileItem> list = new ArrayList();
		for (Iterator<FileItem> it = fileList.iterator(); it.hasNext();) {
			FileItem fileItem = it.next();
			String fieldName = fileItem.getFieldName();
			if (fieldName.equals(property)) {
				list.add(fileItem);
			}
		}
		FileItem[] fit = new FileItem[list.size()];
		for (int i = 0; i < list.size(); i++) {
			fit[i] = list.get(i);
		}
		return fit;
	}

	/*********
	 * **********【映射表单中的附件到formbean内置的pojo属性】******* *
	 * 
	 * @throws Exception
	 *             *
	 **************/

	public void setFormFile(Object action, PropertyDescriptor property,
			FileItem[] fileItems) throws Exception {
		String uploadpath = this.getTempPath();
		Method write = property.getWriteMethod();
		Class<?> type = Server.getComponentType(property.getPropertyType());
		if (property.getPropertyType().isArray()) {
			if (Server.equalsType(Consts.FILE, type) && fileItems != null
					&& fileItems.length > 0)
			{
				File[] files = new File[fileItems.length];
				for (int i = 0; i < fileItems.length; i++) {
					files[i] = createFormFile(uploadpath, fileItems[i]);
				}
				write.invoke(action, (Object) files);
				
				// 转换成Object数组是为了避免反射异常,
				// 因formFile是数组类型，会被误认为可变参数
			}
		} else {
			if (Server.equalsType(Consts.FILE, type) && fileItems != null
					&& fileItems.length > 0) {
				FileItem fileItem = fileItems[0];
				if (fileItem != null) {
					File file = createFormFile(uploadpath, fileItem);
					write.invoke(action, file);
				}
			} 
		}
		
	}
	
	/**
	 * 映射方法File参数
	 * @param paramType
	 * @param fileItems
	 * @return
	 * @throws Exception
	 */
	public Object createParamFile(Class<?> paramType, FileItem[] fileItems) throws Exception {
		String uploadpath = this.getTempPath();
		Class<?> type = Server.getComponentType(paramType);
		if (paramType.isArray()) {
			if (Server.equalsType(Consts.FILE, type) && fileItems != null
					&& fileItems.length > 0)

			{
				File[] files = new File[fileItems.length];
				for (int i = 0; i < fileItems.length; i++) {
					files[i] = createFormFile(uploadpath, fileItems[i]);
				}
				return files;
			}
		} else {
			if (Server.equalsType(Consts.FILE, type) && fileItems != null
					&& fileItems.length > 0) {
				FileItem fileItem = fileItems[0];
				if (fileItem != null) {
					File file = createFormFile(uploadpath, fileItem);
					return file;
				}
			} 
		}
		return null;
	}
	
	
	

	/***
	 * 解析字符串数组
	 * 
	 * @param value
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public Object parseObjectArray(Class<?> type, FileItem[] fileItems) throws UnsupportedEncodingException {
		Object value = null;
		if (fileItems != null) {
			String[] arr = new String[fileItems.length];
			for (int i = 0; i < fileItems.length; i++) {
				arr[i] = fileItems[i].getString("utf-8");
			}
			if (Server.existArray(type)) {
				value = arr;
			} else if (!type.isArray()) {
				value = arr[0];
			}
		}
		return value;
	}
	

	

	
	/**********
	 *********【匹配FormBean的属性和上传表单字段的相似度】****
	 ********/

	public boolean hasProperty(String property, List<FileItem> fileList, Class<?> paramType) {
		if (Server.isBaseType(paramType)) {
			for (Iterator<FileItem> it = fileList.iterator(); it.hasNext();) {
				FileItem fi = it.next();
				String fieldName = fi.getFieldName();
				if (fieldName.equals(property)) {
					return true;
				}
			}
		}
		return false;
	}
	
	/*******
	 * 判断requestlist中是否存在子关联pojo
	 * @param keyname
	 * @param fileList
	 * @return
	 */
	private boolean hasChildProperty(String keyname, List fileList) {
		for (Iterator it = fileList.iterator(); it.hasNext();) {
			FileItem fi = (FileItem) it.next();
			String fieldName = fi.getFieldName();
			if (fieldName.startsWith(keyname + ".")) {
				return true;
			}
		}
		return false;
	}
	

	
	/********* 映射出一个FormFile对象 **********/
	public File createFormFile(String uploadPath, FileItem fileItem)
			throws Exception {
		File root = new File(uploadPath);
		if (!root.exists()) 
			root.mkdir();
		if (fileItem.getSize() == 0)
			return null;
		String fileName = fileItem.getName();
		fileName = fileName.substring(fileName.lastIndexOf("\\") + 1, fileName
				.length());
		File file = new File(uploadPath, fileName);
		fileItem.write(file); //fileItem对象写完以后会将临时文件删除,所以同一对象仅能写入一次.
		return file;
	}
	

	public void doAfter() {
		File file = null;
		File tempDir = this.getTempDir();
		if (tempDir == null) return;
		String path = tempDir.getPath();
		String[] files = tempDir.list();
		if (files == null) return;
		for (int i = 0; i < files.length; i++) {
			file = new File(path, files[i]); 
			if (file.delete())
				System.out.println("Removing File " + file.getPath());
		}
		tempDir.delete();
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
