package com.spring.beans.resource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;


public class ByteArrayResource implements Resource {
	private Document[] documents;

	public ByteArrayResource(byte[] data) {
		this.documents = new Document[1];
		documents[0] = getDoc(data);
	}
	
	public Document getDoc(byte[] data) {
		InputStream is = new ByteArrayInputStream(data);
		InputStreamReader isr = null;
		try {
			isr = new InputStreamReader(is, "UTF-8");
			DocumentBuilder builder = DocumentBuilderFactory.newInstance()
				.newDocumentBuilder();
			Document root = builder.parse(new InputSource(isr));
			return root;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (isr != null)
				try {isr.close();} catch (IOException e) {}
		}
		return null;
	}

	public Document[] getDocuments() {
		return documents;
	}

}
