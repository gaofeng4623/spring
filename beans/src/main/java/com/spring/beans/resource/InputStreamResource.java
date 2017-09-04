package com.spring.beans.resource;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;


public class InputStreamResource implements Resource{
	private Document[] documents;
	
	public InputStreamResource(InputStream is) {
		this.documents = new Document[1];
		this.documents[0] = getDoc(is);
	}
	
	public Document getDoc(InputStream is) {
		Document root = null;
		InputStreamReader isr = null;
		try {
			isr = new InputStreamReader(is, "UTF-8");
			DocumentBuilder builder = DocumentBuilderFactory.newInstance()
				.newDocumentBuilder();
			return builder.parse(new InputSource(isr));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (isr != null)
				try {isr.close();} catch (IOException e) {}
		}
		return root;
	}
	
	public Document[] getDocuments() {
		return documents;
	}

}
