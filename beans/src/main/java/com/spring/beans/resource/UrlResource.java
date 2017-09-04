package com.spring.beans.resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import com.spring.beans.resource.loader.DefaultResourceLoader;


public class UrlResource extends DefaultResourceLoader implements Resource{
	private Document[] documents;
	
	public UrlResource(String urlStr) {
		parseResource(urlStr);
		if (getSource()!=null) return;
		this.documents = new Document[1];
		documents[0] = getDoc(urlStr);
	}
	
	public Document[] getDocuments() {
		return documents;
	}

	 public Document getDoc(String urlStr) {
         URL url;
         String sCurrentLine = "";
         BufferedReader bufferedReader = null;
         StringBuffer sb = new StringBuffer("");
         try {
             url = new URL(urlStr);
             URLConnection URLconnection = url.openConnection();  
             HttpURLConnection httpConnection = (HttpURLConnection)URLconnection;
             int responseCode = httpConnection.getResponseCode();  
             if (responseCode == HttpURLConnection.HTTP_OK) {
                 InputStream urlStream = httpConnection.getInputStream();
                 InputStreamReader isr = new InputStreamReader(urlStream, "UTF-8");
                 Document doc = getDocumentBuilder().parse(new InputSource(isr));
     			 isr.close();
     			 return doc;
             }else{
                    System.err.println("无法连接被请求对象");
              }
             
         } catch (Exception e) {
         	System.err.println("连接 "+urlStr+" 失败!");
         } finally {
        	 if (bufferedReader != null)
				try {
					bufferedReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
         }
         	return null;
 }

	@Override
	public Resource getSourceByPath(String path) {
		if (path.startsWith("file:")) {
			return new FileSystemResource(path);
		} else if (path.startsWith("classpath:")) {
			return new ClassPathResource(path);
		}
		return null;
	}
}
