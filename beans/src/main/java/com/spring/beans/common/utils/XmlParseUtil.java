package com.spring.beans.common.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.spring.beans.factory.config.EditorConfigurer;

public class XmlParseUtil{
	private DocumentBuilder documentBuilder;
	private XPath xpath;

	public XmlParseUtil() {
		this.documentBuilder = createDocumentBuilder();
		this.xpath = XPathFactory.newInstance().newXPath();
	}
	
	public DocumentBuilder getDocumentBuilder() {
		return documentBuilder;
	}

	public void setDocumentBuilder(DocumentBuilder documentBuilder) {
		this.documentBuilder = documentBuilder;
	}

	public XPath getXpath() {
		return xpath; 
	}

	public void setXpath(XPath xpath) {
		this.xpath = xpath;
	}

	public DocumentBuilder createDocumentBuilder() {
		DocumentBuilder builder = null;
		try {
			builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		return builder;
	}
	
	

	/****获得当前元素节点下指定类型的�?��元素****/
	public List<Node> getNodeList(String expression, Object node) {
		List<Node> list = new ArrayList<Node>();
		try {
			NodeList nodes = (NodeList) this.xpath.evaluate(expression, node,
					XPathConstants.NODESET);
			if (nodes != null) {
				for (int i = 0; i < nodes.getLength(); i++) {
					list.add(nodes.item(i));
				}
			}
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
		return list;
	}

	/****获得�?��当前节点下指定类型的元素*****/
	public Node getChildrenByTagName(String tagName, Node node) {
		try {
			NodeList nodes = (NodeList) this.xpath.evaluate(tagName, node,
					XPathConstants.NODESET);
			if (nodes != null && nodes.getLength() > 0)
				return nodes.item(0);
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
		return null;
	}

	/*****根据提供的beanid遍历查找bean元素******/
	public Node getNodeById(Document doc, String id)
			throws XPathExpressionException {
		Node node = null;
		NodeList nodes = (NodeList) this.xpath.evaluate("beans/bean", doc,
				XPathConstants.NODESET);
		for (int i = 0; i < nodes.getLength(); i++) {
			node = nodes.item(i);
			NamedNodeMap attributes = node.getAttributes();
			Node idnode = attributes.getNamedItem("id");
			if (idnode.getTextContent().equals(id))
				return node;
		}
		return null;
	}

	/***** 获得关联元素的属性�?******/
	public String getReflectBeanValue(Node e) {
		String refBean = getAttributeValue("bean", e);
		if (refBean == null) refBean = getAttributeValue("local", e);
		return refBean;
	}
	
	/***** 获得当前元素的属�?没有则返回空******/
	public String getAttributeValue(String attributeName, Node node) {
		NamedNodeMap attributes = node.getAttributes();
		if (attributes == null)
			return null;
		Node idnode = attributes.getNamedItem(attributeName);
		if (idnode != null)
			return idnode.getTextContent();
		return null;
	}
	
	/****** 获得当前属�?是否存在 ********/
	public boolean hasAttribute(String attributeName, Node node) {
		NamedNodeMap attributes = node.getAttributes();
		if (attributes == null)
			return false;
		Node idnode = attributes.getNamedItem(attributeName);
		if (idnode == null)
			return false;
		return true;
	}

	/****** 获得当前元素的非空属�?********/
	public String getAttributeOf(String attributeName, Node node) {
		NamedNodeMap attributes = node.getAttributes();
		if (attributes == null) return "";
		Node idnode = attributes.getNamedItem(attributeName);
		if (idnode != null)
			return String.valueOf(idnode.getTextContent()).trim();
		return "";
	}
	
	
	
	/***
	 * 获得bean的唯�?���?
	 * @param element
	 * @return
	 */
	public String getSignOfElementBean(Node node) {
		String sign = getAttributeValue("id", node);
		if (sign == null)
			sign = getAttributeValue("name", node);
		if (sign == null) { 
			//xml采用全类名存储，注解采用类名�?��
			sign = getAttributeValue("class", node); 
		}
		return sign;
	}
	
	
	/****
	 * 根据Class属�?获取beanId
	 * @param className
	 * @return
	 */
	public String getBeanIdByClassName(String className) {
		if (className == null || 
				className.indexOf(".") < 0) return "";
		String sign = className.substring(className.lastIndexOf(".")
				+1, className.length());
		String signa = sign.substring(0, 1);
		String signb = sign.substring(1, sign.length());
		return signa.toLowerCase() + signb;
	}
	
	/************** 【转换数据类型接口�? *****************/

	public static Object parseTextOrProperties(Class<?> target, Object value,
			Map configdata, EditorConfigurer editor) {
		if (isProperties(value)) {
			return Server.conver(target,
					getCurrentProperty("#", String.valueOf(value), configdata), editor);
		}
		return Server.conver(target, value, editor);
	}
	
	/**************properties---start******************/
	
	/****
	 * 判断是否�?��加载配置
	 * 
	 * @param target
	 * @return
	 */
	public static boolean isProperties(Object target) {
		if (target != null && target instanceof String) {
			String value = String.valueOf(target);
			value = value.replaceAll("^\\s+|\\s+$", "");
			if (value.startsWith("#")) {
				value = value.replace("#", "");
				return value.startsWith("{") && value.endsWith("}");
			}
		}
		return false;
	}
	
	/*****
	 * 装载properties配置文件
	 * 
	 * @param resource
	 * @param configdata
	 */
	public void loadingProperties(Document[] resource, Map<String, Map> configdata) {
		Document doc = null;
		List<?> nodes = null;
		for (int i = 0; i < resource.length; i++) {
			doc = resource[i];
			nodes = getNodeList("beans/properties", doc);
			fillProperties(nodes, configdata);
		}
	}

	/*****
	 * 装载properties配置文件
	 * 
	 * @param elements
	 * @param configdata
	 */
	public void fillProperties(List<?> nodes, Map<String, Map> configdata) {
		Node node = null;
		Map<String, Object> item = null;
		String propid = null;
		String location = null;
		Properties ps = null;
		InputStream is = null;
		for (int i = 0; i < nodes.size(); i++) {
			node = (Node) nodes.get(i);
			propid = String.valueOf(getAttributeValue("id", node));
			location = String.valueOf(getAttributeValue("location",
					node));
			ps = new Properties();
			if (location.contains("classpath:"))
				location = location.replace("classpath:", "");
			is = this.getClass().getClassLoader().getResourceAsStream(location);
			try {
				ps.load(is);
				item = new HashMap();
				for (Enumeration en = ps.propertyNames(); en.hasMoreElements();) {
					String key = String.valueOf(en.nextElement());
					String value = String.valueOf(ps.getProperty(key));
					item.put(key, value);
				}
				configdata.put(propid, item); // 装载配置
			} catch (Exception e1) {
				System.out.println("配置文件加载异常:" + propid);
			} finally {
				if (is != null)
					try {
						is.close();
					} catch (IOException e) {
				}
			}

		}
	}

	/*****
	 * 获取配置文件中的对应�?
	 * 
	 * @param value
	 * @param configdata
	 * @return
	 */
	public static String getCurrentProperty(String prefix, String value, Map<String, Object> configdata) {
		String propId = null;
		String key = null;
		if (value != null) {
			value = value.replaceAll("^\\s+|\\s+$", "");
			if (value.startsWith(prefix)) {
				value = value.replace(prefix, "");
				if (value.startsWith("{") && value.endsWith("}")) {
					value = value.replaceAll("\\{|\\}", "");
					if (value.contains(".")) {
						propId = value.substring(0, value.indexOf("."));
						key = value.substring(value.indexOf(".") + 1);
						Map item = (Map) configdata.get(propId);
						if (item != null)
							return String.valueOf(item.get(key));
					}
				}
			}
		}
		return null;
	}
	
	/**************properties---end******************/
}
