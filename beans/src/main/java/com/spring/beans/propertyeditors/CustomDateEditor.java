package com.spring.beans.propertyeditors;

import java.text.SimpleDateFormat;

public class CustomDateEditor implements PropertiesEditor {
	private Object value;
	private SimpleDateFormat format;
	private boolean toggle;
	
	public CustomDateEditor(SimpleDateFormat format, boolean toggle) {
		this.format = format;
		this.toggle = toggle;
	}
	
	public CustomDateEditor() {
		
	}
	
	@Override
	public void setAsText(String text) throws Exception {
		setValue(format.parse(text));
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

	public SimpleDateFormat getFormat() {
		return format;
	}

	public void setFormat(SimpleDateFormat format) {
		this.format = format;
	}

	
}
