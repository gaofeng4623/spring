package com.spring.beans.resource.loader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import com.spring.beans.resource.Resource;

public abstract class DefaultResourceLoader implements ResourceLoader{
	private Resource source;
	private DocumentBuilder documentBuilder;
	
	public DocumentBuilder getDocumentBuilder() {
		return documentBuilder;
	}
	
	public void setDocumentBuilder(DocumentBuilder documentBuilder) {
		this.documentBuilder = documentBuilder;
	}

	public DefaultResourceLoader() {
		try {
			this.documentBuilder = DocumentBuilderFactory.newInstance()
			.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
	}
	
	public abstract Resource getSourceByPath(String path);
	
	public Resource getSource(String path) {
		return getSourceByPath(path);
	}
	
	public final Resource getSource() {
		return this.source;
	}
	
	public void parseResource(String source) {
		Resource resource = getSource(source);
		if (resource != null) this.source = resource;
	}
	
}
