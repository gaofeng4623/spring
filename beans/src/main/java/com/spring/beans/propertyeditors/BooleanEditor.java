package com.spring.beans.propertyeditors;


public class BooleanEditor implements PropertiesEditor {
	private Object value;
	
	public BooleanEditor() {
		
	}
	
	@Override
	public void setAsText(String text) throws Exception {
		setValue(Boolean.parseBoolean(text.trim()));
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
