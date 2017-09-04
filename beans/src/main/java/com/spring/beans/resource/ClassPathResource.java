package com.spring.beans.resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import com.spring.beans.resource.loader.DefaultResourceLoader;

public class ClassPathResource extends DefaultResourceLoader implements
		Resource {
	private Document[] documents;

	public ClassPathResource() {
		super();
	}

	public ClassPathResource(String theNameOfXml) {
		parseResource(theNameOfXml);
		if (getSource() != null)
			return;
		this.documents = new Document[1];
		documents[0] = getDoc(theNameOfXml);
	}

	public Document[] getDocuments() {
		return documents;
	}

	public void setDocuments(Document[] documents) {
		this.documents = documents;
	}

	public ClassPathResource(String[] xmlPaths) {
		if (xmlPaths == null)
			return;
		this.documents = new Document[xmlPaths.length];
		for (int i = 0; i < xmlPaths.length; i++) {
			documents[i] = getDoc(xmlPaths[i]);
		}
	}

	public Document getDoc(String theNameOfXml) {
		int sindex = 0;
		String realPath = null;
		InputStreamReader isr = null;
		try {
			if (theNameOfXml.startsWith("classpath:"))
				theNameOfXml = theNameOfXml
						.substring(theNameOfXml.indexOf(":") + 1);
			if (theNameOfXml.contains("\\") || theNameOfXml.contains("/")) {
				realPath = theNameOfXml;
			} else {
				String xmlpath = ClassPathResource.class.getResource("/")
						.getPath();
				if (xmlpath.startsWith("/"))
					sindex = 1;
				xmlpath = xmlpath.substring(sindex, xmlpath.length());
				if (!xmlpath.endsWith("/"))
					xmlpath += "/";
				xmlpath = xmlpath.replaceAll("%20", " ");
				realPath = xmlpath + theNameOfXml;
			}
			System.out.println("realPath  " + realPath);
			isr = new InputStreamReader(new FileInputStream(
					new File(realPath)), "utf-8");
			return getDocumentBuilder().parse(new InputSource(isr));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (isr != null)
				try {isr.close();} catch (IOException e) {}
		}
		return null;

	}

	@Override
	public Resource getSourceByPath(String path) {
		if (path.startsWith("file:")) {
			return new FileSystemResource(path);
		} else if (path.startsWith("http://")) {
			return new UrlResource(path);
		}
		return null;
	}

}
