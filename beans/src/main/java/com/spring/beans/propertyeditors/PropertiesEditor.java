package com.spring.beans.propertyeditors;

public interface PropertiesEditor {

	public void setAsText(String text) throws Exception;
	
	public String getAsText();
	
	public void setValue(Object object);
	
	public Object getValue();
}
