package com.spring.beans.common.utils;

public class Consts {
	public static final String BEAN = "bean";
	public static final String SIGN = "sign";
	public static final String INTEGER = "java.lang.Integer";
	public static final String LONG = "java.lang.Long";
	public static final String DOUBLE = "java.lang.Double";
	public static final String FLOAT = "java.lang.Float";
	public static final String SHORT = "java.lang.Short";
	public static final String BOOLEAN = "java.lang.Boolean";
	public static final String BYTE = "java.lang.Byte";
	public static final String CHAR = "java.lang.Character";
	public static final String EXCEPTION = "java.lang.Exception";
	public static final String STRING = "java.lang.String";
	public static final String Class = "java.lang.Class";
	public static final String[] BASETYPE = new String[] { "java.lang.Integer", "java.lang.Long",
		"java.lang.Double", "java.lang.Float", "java.lang.Boolean", "java.lang.Byte", "java.lang.Short", 
		"java.lang.Character","java.util.Date", "java.lang.String", "java.io.File" };
	public static final String DATE = "java.util.Date";
	public static final String FILE = "java.io.File";
	public static final String singleton = "singleton";
	public static final String prototype = "prototype";// 原型模式
	public static final String scope = "scope"; // 作用域
	public static final String lazyinit = "lazy-init"; // 延迟加载
	public static final String defaultLazyInit = "default-lazy-init"; // 默认延迟加载
	public static final String component_scan = "component-scan"; // ioc注解扫描
	public static final String base_package = "base-package";
	public static final String proxy_target_class = "proxy-target-class";
	public static final String transaction_manager = "transaction-manager";
	public static final String ON = "on";
	public static final String OFF = "off";
	public static final String toggle = "toggle"; // 拦截开关
	public static final String proxyMethod = "getTarget"; // 获得代理对象的方法
	public static final String requestScope = "request";
	public static final String sessionScope = "session";
	public static final String dependson = "depends-on";
	public static final String factoryMethod = "factory-method";
	public static final String factoryBean = "factory-bean";
	public static final String objectModel = "objectModel"; // 工厂对象模式
	public static final String staticModel = "staticModel"; // 工厂静态模式
	public static final String beanAwareModel = "beanAwareModel"; // 工厂接口模式
	public static final String initMethod = "init-method";
	public static final String destroyMethod = "destroy-method"; // 销毁方法
	public static final String connectionMethod = "getConnection";
	public static final String closeMethod = "close";
	public static final String ByIdModel = "ById"; // id匹配模式
	public static final String ProtoTypeModel = "ProtoType"; // 精确类型匹配
	public static final String FuzzyTypeModel = "FuzzyType"; // 模糊类型匹配
	public static final String SUPER = "super"; // 上级事务
	public static final String READONLY = "readOnly";
	public static final String paramName = "paramName";
	public static final String defaultMethodName = "defaultMethodName";
	public static final String AppliactionContext = "/WEB-INF/applicationContext.xml"; // 默认配置文件
	public static final String contextConfigLocation = "contextConfigLocation";
	public static final String XMLPATH_MVC = "/WEB-INF/spring-servlet.xml"; // MVC默认配置
}
