package test.ceshi;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.PreparedStatement;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;


public class LookBeanByClass {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			XPath xpath = XPathFactory.newInstance().newXPath();
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			InputStreamReader isr = new InputStreamReader(new FileInputStream(
					new File("E:\\workspace\\Spring\\WebRoot\\WEB-INF\\classes\\springaop.xml")), "utf-8");
			Document doc = builder.parse(new InputSource(isr));
			NodeList nodes = (NodeList) xpath.evaluate("beans/bean", doc,
					XPathConstants.NODESET);
		
			for (int i = 0; i < nodes.getLength(); i++) {
				Node e = nodes.item(i);
				System.out.println(e);
			}
			isr.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
