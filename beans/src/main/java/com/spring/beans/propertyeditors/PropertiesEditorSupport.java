package com.spring.beans.propertyeditors;

public abstract class PropertiesEditorSupport implements PropertiesEditor{
	private Object value;
	
	public abstract void setAsText(String text) throws Exception;
	
	public String getAsText() {
		return value.toString();
	}
	
	public void setValue(Object value) {
		this.value = value;
	}
	
	public Object getValue() {
		return this.value;
	}
}
