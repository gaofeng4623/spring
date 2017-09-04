package com.spring.mvc.controller.reflect;

import java.lang.annotation.Annotation;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.spring.beans.factory.config.EditorConfigurer;
import com.spring.mvc.common.ModelMap;

public interface Conver {
	public void reflectFormData(int num, String key, Class<?> paramType,
                                Object[] parameters, Annotation[][] annotations,
                                HttpServletRequest request, Map<String, String> pathVariableMap,
                                ModelMap modelMap) throws Exception;

	public void doAfter();

	public void setEditorConfigurer(EditorConfigurer configer);

	public EditorConfigurer getEditorConfigurer();
}
