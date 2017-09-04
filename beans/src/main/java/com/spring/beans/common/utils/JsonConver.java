package com.spring.beans.common.utils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import sun.misc.BASE64Encoder;

public class JsonConver{
	private SimpleDateFormat dateFormat;
	
	public SimpleDateFormat getDateFormat() {
		return dateFormat;
	}

	public void setDateFormat(SimpleDateFormat dateFormat) {
		this.dateFormat = dateFormat;
	}
	
	
	public JsonConver() {
	}
	
	
	//控制分发
	public String createJson(Object obj) {
		if (obj == null) return "";
		Class<?> type = obj.getClass();
		if (Collection.class.isAssignableFrom(type)) {
			return createJsonByCollection((Collection<?>)obj);
		} else if (Map.class.isAssignableFrom(type)) {
			return createJsonByMap((Map)obj);
		} else if (type.isArray()) {
			if (isByteArray(type))
				return createJsonByByteArray((byte[])obj);
			else
				return createJsonByArray(obj);
		} else if (java.util.Date.class.isAssignableFrom(type)) {
			return createJsonByDate((java.util.Date)obj);
		} else {
			if (isCommenType(obj))
				return "\"" + String.valueOf(obj) + "\"";
			else
				return createJsonByBean(obj);
		}
	}
	
	
	
	public boolean isCommenType(Object obj) {
		if (obj instanceof Integer || obj instanceof Double
		   || obj instanceof Float || obj instanceof Long
		   || obj instanceof Short || obj instanceof String
		   || obj instanceof Exception) {
			return true;
		}
		return false;
	}
	
	
	//普�?java对象转换
	public String createJsonByBean(Object bean) {
		StringBuffer sb = new StringBuffer();
		Class<?> cl = bean.getClass();
		Field[] fields = cl.getDeclaredFields();
		sb.append("{");
		for (int y = 0; y < fields.length; y++) {
			try {
				PropertyDescriptor property = new PropertyDescriptor(
						fields[y].getName(), cl);
				String name = property.getName();
				Method md = property.getReadMethod();
				Object value = md.invoke(bean);
				value = createJson(value); //属�?转换
				sb.append("\"").append(name).append("\"").append(":");
				sb.append(value);
				sb.append(y == fields.length - 1 ? "" : ",");
			} catch (Exception e) {
				//e.printStackTrace();
			} 
		}
		sb.append("}");
		return sb.toString();
	}
	
	
	//集合类型转换
	public String createJsonByCollection(Collection<?> coll) {
		int count = 0;
		if (coll == null) return null;
		StringBuffer sb = new StringBuffer();
		sb.append("[");
		for (Iterator<?> it = coll.iterator(); it.hasNext(); count ++) {
			Object obj = it.next();
			sb.append(createJson(obj));	//调�?用接�?
			sb.append(count == coll.size() - 1 ? "" : ",");
		}
		sb.append("]");
		return sb.toString();
	}
	
	
	
	//Map类型转换
	public String createJsonByMap(Map<String, Object> map) {
		int count = 0;
		if (map == null) return null;
		StringBuffer sb = new StringBuffer();
		sb.append("{");
		for (Iterator<String> it = map.keySet().iterator(); it.hasNext(); count ++) {
			String key = it.next();
			Object obj = map.get(key);
			sb.append("\"").append(key).append("\"").append(":");
			sb.append(createJson(obj)); //通用接口
			sb.append(count == map.size() - 1 ? "" : ",");
		}
		sb.append("}");
		
		return sb.toString();
	}
	
	
	//数组类型
	public String createJsonByArray(Object array) {
		if (array == null) return null;
		StringBuffer sb = new StringBuffer();
		sb.append("[");
		Object[] arr = (Object[]) array;
		for (int i = 0; i < arr.length; i++) {
			sb.append(createJson(arr[i])); //通用接口
			sb.append(i == arr.length - 1 ? "" : ",");
		}
		sb.append("]");
		return null;
	}
	
	
	public boolean isByteArray(Class<?> type) {
		return "byte".equals(type.getComponentType().getName());
	}
	
	//二进制类�?
	public String createJsonByByteArray(byte[] bytes) {
		if (bytes == null || bytes.length == 0)
			return null;
		return new BASE64Encoder().encode(bytes);
	}
	
	
	//日期类型
	public String createJsonByDate(java.util.Date date) {
		if (this.dateFormat != null)
			return dateFormat.format(date);
		else
			return new SimpleDateFormat("yyyy-MM-dd").format(date);
	}
	
	
	
	
}
