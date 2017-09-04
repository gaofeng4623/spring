package com.spring.beans.factory.config;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.spring.beans.common.exception.PropertyEditorException;
import com.spring.beans.common.utils.Server;
import com.spring.beans.propertyeditors.BooleanEditor;
import com.spring.beans.propertyeditors.IntegerEditor;
import com.spring.beans.propertyeditors.LongEditor;
import com.spring.beans.propertyeditors.PropertiesEditor;
import com.spring.beans.propertyeditors.StringEditor;

public class WebDataBinder extends Server implements EditorConfigurer {
	private Map<String, Object> customEditors = new ConcurrentHashMap<String, Object>();

	public WebDataBinder() {
		// 初始化基本编辑器
		this.customEditors.put("java.lang.String", new StringEditor());
		this.customEditors.put("java.lang.Integer", new IntegerEditor());
		this.customEditors.put("java.lang.Long", new LongEditor());
		this.customEditors.put("java.lang.Boolean", new BooleanEditor());
	}

	public Map<String, Object> getCustomEditors() {
		return customEditors;
	}

	public void setCustomEditors(Map<String, Object> customEditors) {
		this.customEditors.putAll(customEditors);
	}

	@Override
	public boolean matcheType(Class<?> propertyType) {
		for (String key : customEditors.keySet()) {
			Class<?> type = classConver(key);
			if (type == null) continue;
			if (propertyType.isAssignableFrom(type)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Object parsePropertyValue(Class<?> propertyType, String text)
			throws PropertyEditorException {
		PropertiesEditor editor = (PropertiesEditor) customEditors
				.get(propertyType.getName());
		try {
			editor.setAsText(text);
			return propertyType.cast(editor.getValue());
		} catch (Exception e) {
			e.printStackTrace();
			throw new PropertyEditorException(e);
		}
	}
	
	@Override
	public void registerCustomEditor(Class<?> type, PropertiesEditor editor) {
		Class<?> keyt = classConver(type.getName());
		if (keyt != null) {
			customEditors.put(keyt.getName(), editor);
		}
	}

}
