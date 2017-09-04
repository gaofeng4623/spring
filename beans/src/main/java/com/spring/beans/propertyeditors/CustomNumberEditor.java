package com.spring.beans.propertyeditors;

import com.spring.beans.common.utils.Server;

public class CustomNumberEditor extends Server implements PropertiesEditor {
	private Object value;
	private boolean toggle;
	private Class<?> type;
	
	public CustomNumberEditor(Class<?> type, boolean toggle) {
		this.type = getComponentType(type);
		this.toggle = toggle;
	}
	
	
	@Override
	public void setAsText(String text) throws Exception {
		PropertiesEditor editor = null;
		if (Integer.class.isAssignableFrom(type)) {
			editor = new IntegerEditor();
		} else if (Long.class.isAssignableFrom(type)) {
			editor = new LongEditor();
		} else if (Double.class.isAssignableFrom(type)) {
			editor = new DoubleEditor();
		} else if (Float.class.isAssignableFrom(type)) {
			editor = new FloatEditor();
		} else if (Short.class.isAssignableFrom(type)) {
			editor = new ShortEditor();
		} else if (Byte.class.isAssignableFrom(type)) {
			editor = new ByteEditor();
		}
		if (editor != null) {
			editor.setAsText(text);
			setValue(editor.getValue());	
		}
	}

	@Override
	public String getAsText() {
		return getValue().toString();
	}

	@Override
	public void setValue(Object object) {
		this.value = object;
	}

	@Override
	public Object getValue() {
		return this.value;
	}
}
