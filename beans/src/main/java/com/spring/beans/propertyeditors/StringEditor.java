package com.spring.beans.propertyeditors;


public class StringEditor implements PropertiesEditor {
	private Object value;
	
	@Override
	public void setAsText(String text) throws Exception {
		String value = text == null ? "" : text;
		setValue(String.valueOf(value));
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
