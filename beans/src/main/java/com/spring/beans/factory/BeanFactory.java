package com.spring.beans.factory;

import java.util.List;
import java.util.Map;

import com.spring.beans.common.SourceItem;
import com.spring.beans.factory.config.EditorConfigurer;

public interface BeanFactory {
	
	public Object getBean(Object param) throws Exception;

	public <T> T getBean(Class<T> type) throws Exception;

	public boolean isAspectCglibable();

	public EditorConfigurer getEditorConfigurer();

	public void destroySingletons();
	
	public Map<String, SourceItem> getDataSource();
	
	public List<?> getAdvisors();
}
