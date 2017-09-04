package com.spring.beans.propertyeditors;


public class ByteEditor implements PropertiesEditor {
	private Object value;

	@Override
	public void setAsText(String text) throws Exception {
		setValue(text.getBytes());
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
