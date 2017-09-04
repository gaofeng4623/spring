package com.spring.beans.resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.spring.beans.resource.loader.DefaultResourceLoader;


public class FileSystemResource extends DefaultResourceLoader implements Resource{
	private Document[] documents;
	public FileSystemResource(String filePath) {
		parseResource(filePath);
		if (getSource()!=null) return;
		this.documents = new Document[1];
		documents[0] = getDoc(filePath);
	}
	
	public Document getDoc(String filePath) {
		Document doc = null;
		InputStreamReader isr = null;
		try {
			if (filePath.startsWith("file:"))
				filePath = filePath.substring(filePath.indexOf(":") + 1);
			isr = new InputStreamReader(new FileInputStream(new File(filePath)), "UTF-8");
			doc = getDocumentBuilder().parse(new InputSource(isr));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} finally {
			try {isr.close();} catch (IOException e) {}
		}
		
		return doc;
	}
	
	public Document[] getDocuments() {
		
		return this.documents;
	}

	@Override
	public Resource getSourceByPath(String path) {
		if (path.startsWith("classpath:")) {
			return new ClassPathResource(path);
		} else if (path.startsWith("http://")) {
			return new UrlResource(path);
		}
		return null;
	}

}
