package com.spring.beans.factory.config;

import com.spring.beans.common.exception.PropertyEditorException;
import com.spring.beans.propertyeditors.PropertiesEditor;

public interface EditorConfigurer {
	
	public boolean matcheType(Class<?> propertyType);

	public Object parsePropertyValue(Class<?> propertyType, String text)
			throws PropertyEditorException;
	
	public void registerCustomEditor(Class<?> type, PropertiesEditor editor);
}
