package com.spring.beans.common;

import org.w3c.dom.Node;

public class PrototypeItem {
	private String sourceId;
	private String protypeClass;
	private Object source;

	
	public String getSourceId() {
		return sourceId;
	}


	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}


	public String getProtypeClass() {
		return protypeClass;
	}


	public void setProtypeClass(String protypeClass) {
		this.protypeClass = protypeClass;
	}


	public Object getSource() {
		return source;
	}


	public void setSource(Object source) {
		this.source = source;
	}


	public PrototypeItem(String sourceId, String protypeClass, Object source) {
		this.sourceId = sourceId;
		this.protypeClass = protypeClass;
		this.source = source;
	}
	
	public boolean isElement() {
		return this.source instanceof Node;
	}
	
	public boolean isClass() {
		return this.source instanceof Class;
	}
}
