package com.spring.beans.common.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;

import com.spring.beans.aware.BeanPostProcessor;
import com.spring.beans.common.annotation.Component;
import com.spring.beans.common.annotation.Controller;
import com.spring.beans.common.annotation.Lazy;
import com.spring.beans.common.annotation.Repository;
import com.spring.beans.common.annotation.RestController;
import com.spring.beans.common.annotation.Scope;
import com.spring.beans.common.annotation.Service;
import com.spring.beans.factory.config.EditorConfigurer;

/**
 * @author 高峰
 * 
 */
public class Server extends XmlParseUtil{
	public static final int POST_PROSSOR_BEFORE = 0;
	public static final int POST_PROSSOR_AFTER = 1;
	private static ServletContext context;

	public static void setServletContext(ServletContext application) {
		context = application;
	}

	public Server() {
		super();
	}

	/************** 【转换数据类型�? *****************/

	public static Object conver(Class<?> target, Object value, EditorConfigurer configer) {
		try {
			if (target == null || value == null)
				return value;
			Class<?> type = getComponentType(target);
			if (target.isArray()) {
				if (value instanceof Collection && equalsType(Consts.STRING, type)) {
					Collection coll = (Collection) value;
					String[] arr = new String[coll.size()];
					Object[] values = coll.toArray(arr);
					for (int i = 0; i < values.length; i++) {
						if (matchType(type, configer)) {
							values[i] = configer.parsePropertyValue(type, (String)values[i]);
						}
					}
					value = values;
				} else if (value instanceof Collection && equalsType(Consts.INTEGER, type)) {
					Collection coll = (Collection) value;
					String[] temp = new String[coll.size()];
					Object[] arr = coll.toArray(temp);
					int[] it = new int[arr.length];
					for (int i = 0; i < arr.length; i++) {
						if (matchType(type, configer)) {
							it[i] = (Integer) configer.parsePropertyValue(type, parseValue(arr[i]));
						} else {
							it[i] = Integer.parseInt(parseValue(arr[i]));
						}
					}
					value = it;
				} else if (value instanceof Collection && equalsType(Consts.DOUBLE, type)) {
					Collection coll = (Collection) value;
					String[] temp = new String[coll.size()];
					Object[] arr = coll.toArray(temp);
					double[] db = new double[arr.length];
					for (int i = 0; i < arr.length; i++) {
						if (matchType(type, configer)) {
							db[i] = (Double) configer.parsePropertyValue(type, parseValue(arr[i]));
						}
					}
					value = db;
				} else if (value instanceof Collection && equalsType(Consts.LONG, type)) {
					Collection coll = (Collection) value;
					String[] temp = new String[coll.size()];
					Object[] arr = coll.toArray(temp);
					long[] l = new long[arr.length];
					for (int i = 0; i < arr.length; i++) {
						if (matchType(type, configer)) {
							l[i] = (Long) configer.parsePropertyValue(type, parseValue(arr[i]));
						} else {
							l[i] = Long.parseLong(parseValue(arr[i]));
						}
					}
					value = l;
				} else if (value instanceof Collection && equalsType(Consts.FLOAT, type)) {
					Collection coll = (Collection) value;
					String[] temp = new String[coll.size()];
					Object[] arr = coll.toArray(temp);
					float[] ft = new float[arr.length];
					for (int i = 0; i < arr.length; i++) {
						if (matchType(type, configer)) {
							ft[i] = (Float) configer.parsePropertyValue(type, parseValue(arr[i]));
						}
					}
					value = ft;
				} else if (value instanceof Collection && equalsType(Consts.SHORT, type)) {
					Collection coll = (Collection) value;
					String[] temp = new String[coll.size()];
					Object[] arr = coll.toArray(temp);
					short[] st = new short[arr.length];
					for (int i = 0; i < arr.length; i++) {
						if (matchType(type, configer)) {
							st[i] = (Short) configer.parsePropertyValue(type, parseValue(arr[i]));
						}
					}
					value = st;
				} else if (value instanceof String && equalsType(Consts.STRING, type)) {
					value = new String[] { (String) value };
				} else if (value instanceof String && equalsType(Consts.BYTE, type)) {
					if (matchType(type, configer)) {
						value = configer.parsePropertyValue(type, (String)value);
					}
				}
			} else {
				if (matchType(type, configer)) {
					value = configer.parsePropertyValue(type, String.valueOf(value));
				} else if (equalsType(Consts.STRING, type)) {
					value = String.valueOf(value);
				} else if (equalsType(Consts.INTEGER, type)) {
					value = Integer.parseInt(parseValue(value));
				} else if (equalsType(Consts.LONG, type)) {
					value = Long.parseLong(parseValue(value));
				} else if (equalsType(Consts.BOOLEAN, type)) {
					value = Boolean.parseBoolean((String) value);
				} else if (equalsType(Consts.Class, type)) {
					value = Class.forName((String) value);
				} else if (value instanceof String
						&& target.isAssignableFrom(List.class)) { // 单�?配置情况
					List list = new ArrayList(); // 官方类型
					list.add(value);
					value = list;
				} else if (value instanceof String
						&& target.isAssignableFrom(Set.class)) {
					Set set = new HashSet(); // 官方类型
					set.add(value);
					value = set;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return value;
	}

	private static boolean matchType(Class<?> propertyType, EditorConfigurer configer) {
		return configer != null && configer.matcheType(propertyType);
	}

	//比较类型
	public static boolean equalsType(String string, Class<?> type) {
		try {
			return Class.forName(string).isAssignableFrom(type);
		} catch (ClassNotFoundException e) {
			return false;
		}
	}

	/**
	 * 处理mvc方法参数的数据类型转�?
	 * 
	 * @param target
	 * @param value
	 * @return
	 */
	public static Object converParams(Class<?> target, Object value, EditorConfigurer configer) {
		try {
			Class<?> type = getComponentType(target);
			if (target.isArray()) {
				if (equalsType(Consts.STRING, type)) {
					value = (String[]) value;
				} else if (equalsType(Consts.INTEGER, type)) {
					String[] arr = (String[]) value;
					int[] it = new int[arr.length];
					for (int i = 0; i < arr.length; i++) {
						if (matchType(type, configer)) {
							it[i] = (Integer) configer.parsePropertyValue(type, parseValue(arr[i]));
						} else {
							it[i] = Integer.parseInt(parseValue(arr[i]));
						}
					}
					value = it;
				} else if (equalsType(Consts.DOUBLE, type)) {
					String[] arr = (String[]) value;
					double[] db = new double[arr.length];
					for (int i = 0; i < arr.length; i++) {
						if (matchType(type, configer)) {
							db[i] = Double.parseDouble(parseValue(arr[i]));
						}
					}
					value = db;
				} else if (equalsType(Consts.LONG, type)) {
					String[] arr = (String[]) value;
					long[] l = new long[arr.length];
					for (int i = 0; i < arr.length; i++) {
						if (matchType(type, configer)) {
							l[i] = (Long) configer.parsePropertyValue(type, parseValue(arr[i]));
						} else {
							l[i] = Long.parseLong(parseValue(arr[i]));
						}
					}
					value = l;
				} else if (equalsType(Consts.FLOAT, type)) {
					String[] arr = (String[]) value;
					float[] ft = new float[arr.length];
					for (int i = 0; i < arr.length; i++) {
						if (matchType(type, configer)) {
							ft[i] = (Float) configer.parsePropertyValue(type, parseValue(arr[i]));
						} 
					}
					value = ft;
				} else if (equalsType(Consts.SHORT, type)) {
					String[] arr = (String[]) value;
					short[] st = new short[arr.length];
					for (int i = 0; i < arr.length; i++) {
						if (matchType(type, configer)) {
							st[i] = (Short) configer.parsePropertyValue(type, parseValue(arr[i]));
						}
					}
					value = st;
				} else if (equalsType(Consts.BYTE, type)) {
					String[] arr = (String[]) value;
					if (matchType(type, configer)) {
						value = (byte[]) configer.parsePropertyValue(type, arr[0]);
					}
				}

			} else {
				if (matchType(type, configer)) {
					value = configer.parsePropertyValue(type, String.valueOf(value));
				} else if (equalsType(Consts.STRING, type)) {
					value = String.valueOf(value);
				} else if (equalsType(Consts.INTEGER, type)) {
					value = Integer.parseInt(parseValue(value));
				} else if (equalsType(Consts.LONG, type)) {
					value = Long.parseLong(parseValue(value));
				} else if (equalsType(Consts.BOOLEAN, type)) {
					value = Boolean.parseBoolean((String) value);
				} else if (equalsType(Consts.Class, type)) {
					value = Class.forName((String) value);
				}
				return type.cast(value);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return value;
	}

	
	/***** 解析数组内的类型 *******/
	public static Class<?> getComponentType(Class<?> type) {
		if (type.isArray()) {
			return classConver(type.getComponentType().getName());
		} else {
			return classConver(type.getName());
		}
	}
	
	/***** 把基本数据类型转换为�?****/
	public static Class<?> classConver(String name) {
		try {
			if ("int".equals(name)) {
				return Class.forName(Consts.INTEGER);
			} else if ("long".equals(name)) {
				return Class.forName(Consts.LONG);
			} else if ("double".equals(name)) {
				return Class.forName(Consts.DOUBLE);
			} else if ("short".equals(name)) {
				return Class.forName(Consts.SHORT);
			} else if ("float".equals(name)) {
				return Class.forName(Consts.FLOAT);
			} else if ("boolean".equals(name)) {
				return Class.forName(Consts.BOOLEAN);
			} else if ("char".equals(name)) {
				return Class.forName(Consts.CHAR);
			} else if ("byte".equals(name)) {
				return Class.forName(Consts.BYTE);
			}
			return Class.forName(name);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Object getDefaultParamValue(Class<?> target) {
		Class<?> type = getComponentType(target);
		if (equalsType(Consts.INTEGER, type) || equalsType(Consts.DOUBLE, type)
				|| equalsType(Consts.LONG, type) || equalsType(Consts.FLOAT, type)
				|| equalsType(Consts.SHORT, type) || equalsType(Consts.BYTE, type)
				|| equalsType(Consts.CHAR, type)) {
			return 0;
		} else if (equalsType(Consts.STRING, type) || equalsType(Consts.DATE, type)) {
			return null;
		}
		return null;
	}

	/**** 判断是File还是File数组 ***/
	public static boolean isFileProperty(Class<?> type) {
		try {
			return Class.forName(Consts.FILE).isAssignableFrom(getComponentType(type));
		} catch (ClassNotFoundException e) {
			return false;
		}
	}

	public static boolean isFilteringType(Class<?> type) {
		// 判断是接口或数组则不进行创建
		return type.isArray() || type.isInterface()
				|| isBaseType(type);
	}

	public static boolean isBaseType(Class<?> type) {
		Class<?> resultT = getComponentType(type); //可能是基本数据类型数�?
		String typeName = classConver(resultT.getName()).getName();
		for (int i = 0; i < Consts.BASETYPE.length; i++) {
			if (typeName.equals(Consts.BASETYPE[i]))
				return true;
		}
		return false;
	}

	/***
	 * 过滤属�?的普通类型和数组类型
	 * 
	 * @param property
	 * @return
	 */
	public static boolean existArray(Class<?> cls) {
		Class<?> type = getComponentType(cls);
		if (cls.isArray()) {
			if (equalsType(Consts.STRING, type)
					|| equalsType(Consts.INTEGER, type)
					|| equalsType(Consts.DOUBLE, type)
					|| equalsType(Consts.LONG, type)
					|| equalsType(Consts.FLOAT, type)
					|| equalsType(Consts.SHORT, type)) {
				return true;
			} else {
				System.out.println(cls.getName() + " 为映射不支持的数组类");
			}
		}

		return false;
	}


	public static String parseValue(Object value) {
		if (value == null || "".equals(value.toString().trim()))
			value = "0";
		else if (!parseNumber(value)) {
			value = "0";
		}
		return value.toString();
	}

	/*** �?��用户输入 ***/
	public static boolean parseNumber(Object value) {
		String regex = "^\\d+\\.?\\d+$|^\\d+$";
		return Pattern.matches(regex, value.toString());
	}

	public static String getRealPath(String fileName) {
		String realPath = null;
		if (context != null)
			realPath = context.getRealPath(fileName);
		return realPath;
	}
	
	public static boolean isEmpty(Object value) {
		return value == null || "".equals(value.toString());
	}
	
	/*** 匹配类似的构造器 ***/
	protected List<Constructor<?>> getConstructors(Class<?> target, int paramsLength) {
		List<Constructor<?>> list = new ArrayList();
		Constructor<?>[] ctors = target.getConstructors();
		for (int i = 0 ; i < ctors.length; i++) {
			int length = ctors[i].getParameterTypes().length;
			if (length == paramsLength) {
				list.add(ctors[i]);
			}
		}
		return list;
	}
	
	/**
	 * 根据属�?名称获得属�?对象
	 * @param name
	 * @param implCl
	 * @return
	 */
	public Field parseField(String name, Class<?> implCl) {
		Field[] fields = implCl.getDeclaredFields();
		Field[] superFileds = implCl.getSuperclass().getDeclaredFields();
		for (Field f : fields) {
			if (f.getName().equals(name))
				return f;
		}
		for (Field f : superFileds) {
			if (f.getName().equals(name))
				return f;
		}
		return null;
	}
	
	/****
	 * 获得目标属�?的set方法
	 * @param methods
	 * @return
	 */
	public Method getWriteMethod(Method[] methods, String filedName) {
		if (methods == null)
			return null;
		for (Method md : methods) {
			String methodName = md.getName();
			if (methodName.startsWith("set") && methodName.length() > 3 
					&& methodName.substring(3).equalsIgnoreCase(filedName)) {
				return md;
			}
		}
		return null;
	}
	
	/**
	 * 对注入之后的属�?进行代理
	 * @param bean
	 * @param option
	 */
	public void setProxyProperty(Object bean, int what) {
		Method method = null;
		Object result = null;
		 Class<?> cl = bean.getClass();
		 Method[] methods = cl.getMethods();
		 do {
			Field[] fields = cl.getDeclaredFields();
			for (int i = 0; i < fields.length; i++) {
				try {
					Field f = fields[i];
					f.setAccessible(true);
					Object targetValue = f.get(bean);
					if (what == POST_PROSSOR_BEFORE) {
						result = ((BeanPostProcessor) bean)
								.postProcessBeforeInitialization(targetValue, f.getName());
					} else if (what == POST_PROSSOR_AFTER){
						result = ((BeanPostProcessor) bean)
								.postProcessAfterInitialization(targetValue, f.getName());
					}
					method = getWriteMethod(methods, f.getName());
					if (method != null) {
						method.invoke(bean, result);
					} else {
						f.set(bean, result);
					}
					f.setAccessible(false);
					} catch (Exception e) {
						e.printStackTrace();
					} 
				}
				cl = cl.getSuperclass();
			} while(cl != null); 
		}

	public boolean isPrototype(String scope) {
		return scope != null && scope.equals(Consts.prototype);
	}
	
	public boolean isSingleton(Class<?> cl) {
		boolean isLazy = false;
		String scope = getScopeOfAnnotation(cl);
		boolean hasLazyAnno = cl.isAnnotationPresent(Lazy.class);
		if (hasLazyAnno) {
			Lazy lazy = cl.getAnnotation(Lazy.class);
			isLazy = lazy.value();
		}
		return !isPrototype(scope) && !isLazy;
	}
	
	public String getScopeOfAnnotation(Class<?> cls) {
		if (cls.isAnnotationPresent(Scope.class)) {
			Scope sc = (Scope) cls.getAnnotation(Scope.class);
			return sc.value();
		} else 
			return Consts.singleton;
	}
	
	public boolean isController(Class<?> cl) {
		return cl.isAnnotationPresent(Controller.class) 
			|| cl.isAnnotationPresent(RestController.class);
	}
	
	//判断扫描到的类是否存在注�?
	public boolean hasAnnotation(Class<?> cl) {
		return cl.isAnnotationPresent(Component.class)
			|| cl.isAnnotationPresent(Service.class)
			|| cl.isAnnotationPresent(Controller.class)
			|| cl.isAnnotationPresent(RestController.class)
			|| cl.isAnnotationPresent(Repository.class);
	}
}
