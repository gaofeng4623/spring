package com.spring.beans.propertyeditors;

import java.util.regex.Pattern;

public class FloatEditor implements PropertiesEditor {
	private Object value;

	@Override
	public void setAsText(String text) throws Exception {
		setValue(Float.parseFloat(parseValue(text)));
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

	public String parseValue(Object value) {
		if (value == null || "".equals(value.toString().trim()))
			value = "0";
		else if (!parseNumber(value)) {
			value = "0";
		}
		return value.toString();
	}

	/*** 检测用户输入 ***/
	public boolean parseNumber(Object value) {
		String regex = "^\\d+\\.?\\d+$|^\\d+$";
		return Pattern.matches(regex, value.toString());
	}
}
