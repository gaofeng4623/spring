package com.spring.beans.common;

public class SourceItem {
	private String sourceId;
	private String scope;
	private Object source;
	private String protypeClass;
	private Object object; //原型对象
	private boolean updated;
	private String initMethod; //可重新初始化
	private String destroyMethod;
	
	public SourceItem() {
	}
	
	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}
	
	public Object getObject() {
		return object;
	}

	public void setObject(Object object) {
		this.object = object;
	}

	public String getProtypeClass() {
		return protypeClass;
	}

	public void setProtypeClass(String protypeClass) {
		this.protypeClass = protypeClass;
	}

	public SourceItem(String sourceId, String protypeClass, String scope,
			Object source, Object object, String initMethod,
			String destroyMethod, boolean updated) {
		this.sourceId = sourceId;
		this.protypeClass = protypeClass;
		this.scope = scope;
		this.source = source;
		this.object = object;
		this.initMethod = initMethod;
		this.destroyMethod = destroyMethod;
		this.updated = updated;
	}
	
	public String getSourceId() {
		return sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

	public Object getSource() {
		return source;
	}

	public void setSource(Object source) {
		this.source = source;
	}
	
	public String getInitMethod() {
		return initMethod;
	}

	public void setInitMethod(String initMethod) {
		this.initMethod = initMethod;
	}

	public String getDestroyMethod() {
		return destroyMethod;
	}

	public void setDestroyMethod(String destroyMethod) {
		this.destroyMethod = destroyMethod;
	}

	public boolean isUpdated() {
		return updated;
	}

	public void setUpdated(boolean updated) {
		this.updated = updated;
	}
	
	
	
}
