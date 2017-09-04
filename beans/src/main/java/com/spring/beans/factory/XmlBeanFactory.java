package com.spring.beans.factory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.sql.DataSource;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.spring.beans.aware.BeanFactoryAware;
import com.spring.beans.aware.BeanNameAware;
import com.spring.beans.aware.BeanPostProcessor;
import com.spring.beans.aware.DisposableBean;
import com.spring.beans.aware.FactoryBean;
import com.spring.beans.aware.InitializingBean;
import com.spring.beans.common.PrototypeItem;
import com.spring.beans.common.SourceItem;
import com.spring.beans.common.exception.BeanDependOnException;
import com.spring.beans.common.exception.BeanNotFoundException;
import com.spring.beans.common.exception.PropertyCastException;
import com.spring.beans.common.utils.Consts;
import com.spring.beans.resource.Resource;
import com.spring.beans.resource.loader.ResourceLoader;

@SuppressWarnings("all")
public class XmlBeanFactory extends BasicCoreBeanFactory {
	private Document[] resource;
	private boolean scanable;
	private boolean autoAspect;
	private boolean aspectCglibable;
	private boolean contextModel;
	private String basePackage;
	private String transaction_manager;


	public XmlBeanFactory() {
	}

	public Document[] getResource() {
		return resource;
	}

	public void setResource(Document[] resource) {
		this.resource = resource;
	}

	public String getBasePackage() {
		return basePackage;
	}

	public void setBasePackage(String basePackage) {
		this.basePackage = basePackage;
	}

	public boolean isAspectCglibable() {
		return aspectCglibable;
	}

	public void setAspectCglibable(boolean aspectCglibable) {
		this.aspectCglibable = aspectCglibable;
	}

	public boolean isContextModel() {
		return contextModel;
	}

	public void setContextModel(boolean contextModel) {
		this.contextModel = contextModel;
	}

	public String getTransaction_manager() {
		return transaction_manager;
	}

	public void setTransaction_manager(String transaction_manager) {
		this.transaction_manager = transaction_manager;
	}

	/***
	 * 判断是否开启IOC扫描
	 * @return
	 */
	public boolean iocScanable() {
		return this.scanable && getBasePackage() != null;
	}

	public void setIocScanable(boolean scanable) {
		this.scanable = scanable;
	}

	/***
	 * 判断是否开启aspect扫描
	 * @return
	 */
	public boolean aspectScanable() {
		return this.autoAspect;
	}

	public void setAspectScanable(boolean able) {
		this.autoAspect = able;
	}


	public XmlBeanFactory(Resource resource){
		super();
		Document doc = null;
		if (resource instanceof ResourceLoader) {
			Resource res = ((ResourceLoader) resource).getSource();
			if (res != null)
				this.resource = res.getDocuments();
			else
				this.resource = resource.getDocuments();

		} else {
			this.resource = resource.getDocuments();
		}

		for (int i = 0 ; i < this.resource.length; i++) {
			doc = this.resource[i];
			Node node = getChildrenByTagName("beans/component-scan", doc);
			if (node != null) {
				setIocScanable(true);
				String baseDir = getAttributeValue(Consts.base_package, node);
				if (baseDir != null) setBasePackage(baseDir);
				System.out.println("basePackage::" + basePackage);
			}
			node = getChildrenByTagName("beans/aspectj-autoproxy", doc);
			if (node != null) {
				setAspectScanable(true);
				String cglibable = getAttributeValue(Consts.proxy_target_class, node);
				if (cglibable != null && "true".equalsIgnoreCase(cglibable.trim())) {
					setAspectCglibable(true);
				}
			}
			node = getChildrenByTagName("beans/annotation-driven", doc);
			if (node != null) {
				String manager = getAttributeValue(Consts.transaction_manager, node);
				setTransaction_manager(manager);
			}
		}

		loadingProperties(this.resource, getDataConfig()); //装载properties配置

		setEditorConfigurer(createPropertyEditorConfiger()); //初始化属性解析器
	}


	/***
	 * 获得上下文中的资源
	 * param:String/Class
	 * @throws BeanNotFoundException
	 */
	@Override
	public Object getBean(Object param) throws Exception {
		SourceItem sourceItem = getSource(param);
		if (sourceItem == null) { //原型模式
			PrototypeItem protem = getPrototypeSource(param);
			if (protem != null) {
				sourceItem = prototypeController(protem.getSource());
			}
		} else {
			return sourceItem.getSource(); //单例模式
		}
		if (sourceItem == null) {
			sourceItem = getBeanSourceByParam(param); //创建模式
		}
		if (sourceItem != null && !sourceItem.isUpdated()) {
			if (isContextModel())
				IocBaseContext.doAopFilter(sourceItem, this); //进行AOP代理
		}
		return sourceItem == null ? null : sourceItem.getSource();
	}

	/****
	 * 查找指定类型的bean
	 */
	@Override
	public <T> T getBean(Class<T> type) throws Exception {
		Object bean = getBean((Object)type);
		return type.cast(bean);
	}

	@Override
	public void initPropertyEditorConfig(Object bean) {

	}


	/*****
	 * 根据提供的id和类型匹配相应的bean
	 * 在XML的ref及@Resource/@Autowired注入时调用
	 * @param beanId
	 * @param propertyType
	 * @return
	 * @throws Exception
	 */
	public Object getBeanByProperty(String beanId, Class<?> propertyType) throws Exception {
		SourceItem sourceItem = getBeanInfo(beanId);
		if (sourceItem != null) {
			if (propertyType == null) return sourceItem.getSource();
			if (isAssignableFrom(sourceItem.getProtypeClass(), propertyType)) {
				if (!sourceItem.isUpdated() && isContextModel()) {
					IocBaseContext.doAopFilter(sourceItem, this); //进行AOP代理
				}
			} else {
				return null; //类型不匹配
			}
		}
		return sourceItem == null ? null : sourceItem.getSource();
	}




	/***
	 * 获取组件的详细信息
	 * @param param
	 * @return
	 * 某些情境下，我们只需要封装信息，不需要代理
	 * @throws Exception
	 */
	public SourceItem getBeanInfo(Object param) throws Exception {
		SourceItem item = getSource(param);
		if (item == null) { //原型模式
			PrototypeItem protem = getPrototypeSource(param);
			if (protem != null)
				return prototypeController(protem.getSource());
		} else
			return item; //单例模式
		return getBeanSourceByParam(param);
	}



	/****
	 * 在这里获得XML和注解资源中的匹配元素
	 * id获取规则如下：
	 * 先匹配XML中的ID，检索不到则匹配注解中ID
	 * 1.先精确匹配XML，检索不到则精确匹配注解
	 * 2.先模糊匹配XML，检索不到则模糊匹配注解
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public SourceItem getBeanSourceByParam(Object param) throws Exception {
		SourceItem result = null;
		if (param instanceof String) {
			Node element = getElementByID(param.toString());
			if (element != null) {
				result = createBeanByConfigration(element, true);
			} else {
				if (iocScanable())
					result = getAnnotationByID(getBasePackage(), param.toString());
			}
			if (result == null) {
				element = getElementByAttributes(param.toString());
				if (element != null)
					result = createBeanByConfigration(element, true);
			}

		} else if (param instanceof Class) {
			Node element = getElementByType((Class<?>) param);
			if (element != null) {
				result = createBeanByConfigration(element, true);
			} else {
				if (iocScanable())
					result = getAnnotationByProtoType(getBasePackage(), (Class<?>) param);
			}
			if (result == null) {
				element = getElementByFuzzyType((Class<?>) param);
				if (element != null) {
					result = createBeanByConfigration(element, true);
				} else {
					if (iocScanable())
						result = getAnnotationByFuzzyType(getBasePackage(), (Class<?>) param);
				}
			}
		}
		return result;
	}



	/****
	 * 从XML配置中根据元素组装bean
	 * @param element
	 * @return
	 * @throws Exception
	 */
	public SourceItem createBeanByConfigration(Node element, boolean hasCached) throws Exception {
		Object impl = null;
		String prototype = null;
		SourceItem sourceItem = null;
		String sign = getSignOfElementBean(element);
		String scope = getAttributeOf(Consts.scope, element);
		String initMethod = getInitMethod(element);
		String destroyMethod = getDestroyMethod(element);
		if (getAttributeValue(Consts.dependson, element) != null) {
			try {
				getBean(getAttributeValue(Consts.dependson, element)); //处理对象的依赖关系
			} catch (Exception e) {
				throw new BeanDependOnException(e);
			}
		}
		if (isFactoryModel(element)) { //检测工厂模式
			impl = createFactoryBean(element);
		} else {
			impl = reflectPropertysToBean(element);
		}
		prototype = getPrototypeOfBean(impl);
		Object[] result = createReflectProxyBean(sign, impl); //获得代理对象资源
		boolean updated = Integer.parseInt(result[1].toString()) == 1;
		sourceItem = new SourceItem(sign, prototype, scope, result[0],
				impl, initMethod, destroyMethod, updated);
		if (hasCached) {
			if (isPrototype(element)) {
				addPrototypePool(sign, prototype, element, getPrototypePool());
			} else {
				addSource(sourceItem, getDataSource());
			}
		}
		return sourceItem;
	}



	/****
	 * 原型分流控制器
	 * @param object
	 * @return
	 * @throws Exception
	 */
	private SourceItem prototypeController(Object object) throws Exception {
		if (object instanceof Class) {
			return getBeanByAnnotation((Class<?>) object, false);
		} else if (object instanceof Node) {
			return createBeanByConfigration((Node) object, false);
		}
		return null;
	}



	/***
	 * 获得代理对象或当前对象的原型
	 * *****/
	private String getPrototypeOfBean(Object bean) {
		if (bean == null) return "";
		if (IocBaseContext.isProxyFactoryBean(bean.getClass())) {
			Object object = IocBaseContext.getObject(bean);
			if (object != null)
				return object.getClass().getName();
		} else {
			return bean.getClass().getName();
		}
		return null;
	}


	/***
	 * 创建代理映射关系
	 * @param bean
	 * @return
	 */
	public Object[] createReflectProxyBean(String sign, Object bean) {
		int counter = 0;
		if (IocBaseContext.isProxyFactoryBean(bean.getClass())) {
			bean = IocBaseContext.getReflectProxyBean(bean);
			counter = 1;
		} else if (bean instanceof DataSource
				&& !(IocBaseContext.isAbstractDataSource(bean.getClass()))) {
			getDataSources().put(sign, (DataSource) bean);
			bean = IocBaseContext.getTransactionDataSource((DataSource) bean);
			counter = 1;
		}
		return new Object[]{bean, counter};
	}


	/***
	 * 获得bean的初始化方法
	 * @param element
	 * @return
	 */
	public String getInitMethod(Node element) {
		return getAttributeValue(Consts.initMethod, element);
	}

	/***
	 * 获得bean的销毁方法
	 * @param element
	 * @return
	 */
	public String getDestroyMethod(Node element) {
		return getAttributeValue(Consts.destroyMethod, element);
	}

	/**
	 * 获得class属性
	 * @param element
	 * @return
	 */
	public String getClass(Node element) {
		return String.valueOf(getAttributeValue("class", element));
	}

	/****
	 * 判断是否是原型模式
	 * @param element
	 * @return
	 */
	public boolean isPrototype(Node element) {
		String scope = getAttributeOf(Consts.scope, element);
		String singleton = getAttributeOf(Consts.singleton, element);
		if (scope.equalsIgnoreCase(Consts.prototype)
				|| singleton.equalsIgnoreCase("false"))
			return true;
		return false;
	}


	/*****
	 * 通过构造器注入
	 * @throws Exception ****/
	public Object constructBean(Node beanElement) throws Exception {
		String value = "";
		Object result = null;
		Object constructValue = null;
		Node constructEl = null;
		String beanName = getSignOfElementBean(beanElement);
		String packageName = getAttributeValue("class", beanElement);
		Class<?> implCl = Class.forName(packageName);
		List<Node> constructList = getNodeList("constructor-arg", beanElement);
		if (constructList.size() > 0) {
			Object[] params = new Object[constructList.size()];
			for (int i = 0 ; i < constructList.size() ; i++) {
				constructEl = constructList.get(i);
				value = getAttributeValue("value", constructEl);
				if (value != null && value.length() > 0) {
					params[i] = value;
					continue;
				}
				// 引用嵌套方法@getPropertyByNodeType,支持多种配置类型
				constructValue = getPropertyByNodeType(constructEl, null);
				params[i] = constructValue;
			}
			List<Constructor<?>> ctors = getConstructors(implCl, constructList.size()); //匹配构造器
			for (Constructor<?> c : ctors) {
				try {
					Class<?>[] types = c.getParameterTypes();
					Object[] tempParams = new Object[types.length];
					for (int i = 0; i < types.length; i++) {
						Object target = isBaseType(params[i].getClass()) ?
								conver(types[i], params[i], getEditorConfigurer()) : params[i]; //只转换构造器的基本数据类型参数
						tempParams[i] = target;
					}
					result = c.newInstance(tempParams);
					if (result != null) {
						containerAware(result, beanName); //容器感应接口
						return result;
					}
				} catch (Exception e) {
					continue;
				}
			}
		}

		return null;
	}


	/*****通过对象属性注入
	 * @throws Exception ****/
	public Object setPropertysOfBean(Node beanElement) throws Exception {

		String beanName = getSignOfElementBean(beanElement);
		String packageName = getAttributeValue("class", beanElement);
		Class<?> implCl = Class.forName(packageName);
		Object impl = implCl.newInstance();
		containerAware(impl, beanName); //容器感应接口
		Method[] methods = implCl.getMethods();
		List<Node> propertys = getNodeList("property", beanElement);
		if (propertys == null) return null;
		outer:
		for (int i = 0; i < propertys.size(); i++) {
			Node e = propertys.get(i);
			String propertyName = getAttributeOf("name", e);
			Class<?> propertyType = getTypeByAttributeName(implCl, propertyName);
			inner:
			for (Method md : methods) {
				String methodName = md.getName();
				if (methodName.startsWith("set") && methodName.length() > 3
						&& methodName.substring(3).equalsIgnoreCase(propertyName)) {
					Object relBean = controller(e, propertyName, propertyType, implCl);
					try {
						md.invoke(impl, relBean);
					} catch (Exception ex) {
						ex.printStackTrace();
						throw new PropertyCastException(propertyType, relBean);
					}
					continue outer;
				}
			}
			setAccessibleField(propertyName, implCl, impl, e, propertyType);
		}
		return impl;
	}

	/**
	 * 检出未代理的数据源，注入事务控制管理器
	 * @throws Exception
	 */
	public Object controller(Node e, String propertyName, Class<?> propertyType, Class<?> cl) throws Exception {
		if (IocBaseContext.isTransactionManager(cl)
				&& DataSource.class.isAssignableFrom(propertyType)) {
			try {
				this.getBean(propertyName);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			return this.getDataSources().get(propertyName);
		} else {
			return controller(e, propertyType);
		}
	}


	/***
	 ****属性注入的控制器，避免对象的不必要创建
	 * @throws Exception
	 **********/
	public Object controller(Node e, Class<?> type) throws Exception {
		Object relBean = null;
		String value = getAttributeValue("value", e);
		if (value == null) {
			Node valueEle = getChildrenByTagName("value", e);
			if (valueEle != null)
				value = valueEle.getTextContent();
		}
		String ref = getAttributeValue("ref", e);
		if (ref == null) {
			Node refEle = getChildrenByTagName("ref", e);
			if (refEle != null)
				ref = getReflectBeanValue(refEle);
		}
		/********getBean(rel)递归***********/
		if (value == null && ref != null) {
			relBean = getBeanByProperty(ref, type);
		} else if (value != null) {
			relBean = parseTextOrProperties(type, value, getDataConfig(), getEditorConfigurer());
		} else if (value == null && ref == null) {
			relBean = conver(type, getPropertyByNodeType(e, type), getEditorConfigurer());
		}
		return relBean;
	}



	/***
	 * **根据子节点的类型返回相应实例****
	 * 经测试ArrayList/HashSet/HashMap
	 * 与官方返回类型一致
	 * 不用考虑属性为具体类型，如属性为LinkedList
	 * @throws Exception
	 * ***/
	public Object getPropertyByNodeType(Node nodes, Class<?> type) throws Exception {
		Node element = null;
		List<Node> childs = null;
		SourceItem target = null;
		if (getChildrenByTagName("list", nodes) != null) {
			List<Object> result = new ArrayList<Object>();
			element = getChildrenByTagName("list", nodes);
			if (getChildrenByTagName("value", element) != null) {
				childs = getNodeList("value", element);
				for (Iterator<Node> it = childs.iterator(); it.hasNext();) {
					String value = String.valueOf(it.next().getTextContent());
					result.add(value);
				}
			} if (getChildrenByTagName("bean", element) != null) {
				childs = getNodeList("bean", element);
				for (Iterator<Node> it = childs.iterator(); it.hasNext();) {
					target = createBeanByConfigration(it.next(), false);
					result.add(target.getSource());
				}
			} if (getChildrenByTagName("ref", element) != null) {
				Node e = null;
				childs = getNodeList("ref", element); //有时间考虑下local
				for (Iterator<Node> it = childs.iterator(); it.hasNext();) {
					e = it.next();
					String refBean = getReflectBeanValue(e);
					result.add(getBean(refBean));
				}
			}
			return result;
		} else if (getChildrenByTagName("set", nodes) != null) {
			Set<Object> result = new HashSet<Object>();
			element = getChildrenByTagName("set", nodes);
			if (getChildrenByTagName("value", element) != null) {
				childs = getNodeList("value", element);
				for (Iterator<Node> it = childs.iterator(); it.hasNext();) {
					String value = String.valueOf(it.next().getTextContent());
					result.add(value);
				}
			} if (getChildrenByTagName("bean", element) != null) {
				childs = getNodeList("bean", element);
				for (Iterator<Node> it = childs.iterator(); it.hasNext();) {
					target = createBeanByConfigration(it.next(), false);
					result.add(target.getSource());
				}
			} if (getChildrenByTagName("ref", element) != null) {
				Node e = null;
				childs = getNodeList("ref", element);
				for (Iterator<Node> it = childs.iterator(); it.hasNext();) {
					e = it.next();
					String refBean = getReflectBeanValue(e);
					result.add(getBean(refBean));
				}
			}
			return result;
		} else if (getChildrenByTagName("map", nodes) != null) {
			Map<String, Object> result = new HashMap<String, Object>();
			element = getChildrenByTagName("map", nodes);
			childs = getNodeList("entry", element);
			for (Iterator<Node> it = childs.iterator(); it.hasNext();) {
				Object value = null;
				Node el = it.next();
				String key = getAttributeOf("key", el);
				if (hasAttribute("value-ref", el)) {
					value = getBean(getAttributeOf("value-ref", el));
				} else {
					value = getAttributeValue("value", el);
					if (value == null || ((String) value).length() == 0) {
						// 引用嵌套方法@getPropertyByNodeType,支持多种配置类型
						value = getPropertyByNodeType(el, null);
					}
				}
				result.put(key, value);
			}
			return result;
		} else if (getChildrenByTagName("props", nodes) != null) {
			Properties result = new Properties();
			element = getChildrenByTagName("props", nodes);
			childs = getNodeList("prop", element);
			for (Iterator<Node> it = childs.iterator(); it.hasNext();) {
				Node el = it.next();
				String key = getAttributeOf("key", el);
				String value = getAttributeValue("value", el);
				if (value == null || value.length() == 0) {
					value = el.getTextContent();
				}
				result.put(key, value);
			}
			return result;
		} else if (getChildrenByTagName("bean", nodes) != null) {

			target = createBeanByConfigration(
					getChildrenByTagName("bean", nodes), false); //不缓存子元素了
			Object result = target.getSource();
			if (type == null) return result;
			else {
				if (isAssignableFrom(target.getProtypeClass(), type))
					return result;
				else
					return null;
			}

		} else if (getChildrenByTagName("ref", nodes) != null) {
			String beanRef = getReflectBeanValue(getChildrenByTagName("ref", nodes));;
			if (beanRef != null)
				return type == null ? getBean(beanRef) : getBeanByProperty(beanRef, type);
		}
		return null;
	}


	/*****
	 * *判断通过属性或者构造器注入**
	 * *
	 * @throws Exception *****/
	public Object reflectPropertysToBean(Node beanElement)
			throws Exception {
		Object bean = constructBean(beanElement);
		if (bean == null) {
			bean = setPropertysOfBean(beanElement);
		}
		afterPropertiesSet(bean);
		this.reflectBeforeAware(bean); //前置感应接口
		toRunInitMethod(beanElement, bean);
		this.reflectAfterAware(bean); //后置感应接口
		return bean;
	}


	/***
	 * 调用初始化方法
	 * @param beanElement
	 * @param object
	 * @throws Exception
	 */
	private void toRunInitMethod(Node beanElement, Object object)
			throws Exception {

		if (getAttributeValue(Consts.initMethod, beanElement) != null) {
			String initMethod = getAttributeValue(Consts.initMethod, beanElement);
			Method[] methods = object.getClass().getMethods();
			for (int i = 0; i < methods.length; i++) {
				if (initMethod.equals(methods[i].getName()))
					methods[i].invoke(object); //调用初始化方法
			}
		}
	}

	//容器感应接口
	@Override
	public void containerAware(Object object, String beanName) {
		if (object instanceof BeanFactoryAware) {
			((BeanFactoryAware) object).setBeanFactory(this);
		} if (object instanceof BeanNameAware) {
			((BeanNameAware) object).setBeanName(beanName);
		}
	}


	//处理前置感应接口
	@Override
	public void reflectBeforeAware(Object bean) {
		if (bean instanceof BeanPostProcessor) {
			setProxyProperty(bean, POST_PROSSOR_BEFORE);
		}
	}

	//处理后置感应接口
	@Override
	public void reflectAfterAware(Object bean) {
		if (bean instanceof BeanPostProcessor) {
			setProxyProperty(bean ,POST_PROSSOR_AFTER);
		}
	}

	//属性注入完成之后，初始化之前调用
	@Override
	public void afterPropertiesSet(Object bean) {
		if (bean instanceof InitializingBean) {
			try {
				((InitializingBean) bean).afterPropertiesSet();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/****
	 * 判断一个bean的原型是否隶属于指定的类型
	 * @param protoType
	 * @param type
	 * @return
	 */
	private boolean isAssignableFrom(String protoType, Class<?> type) {
		try {
			Class<? extends Object> cls = Class.forName(protoType);
			if (type.isAssignableFrom(cls)) {
				return true;
			}
		} catch (ClassNotFoundException e) {
			return false;
		}
		return false;
	}


	/*****
	 * 从提供的数据源中根据ID查找匹配XML元素
	 * @param sign
	 * @return
	 */
	public Node getElementByID(String sign) {
		Document doc = null;
		List<Node> nodes = null;
		Node node = null;
		for (int i = 0 ; i < resource.length; i++) {
			doc = resource[i];
			nodes = getNodeList("beans/bean", doc);
			if (nodes == null) continue;
			for (int y = 0; y < nodes.size(); y++) {
				node = (Node) nodes.get(y);
				if (sign.equals(getAttributeOf("id", node)))
					return node;
			}
		}
		return null;
	}

	/***
	 * 从提供的数据源中按照name/class优先级查找元素
	 * @param sign
	 * @return
	 */
	public Node getElementByAttributes(String sign) {
		Document doc = null;
		List<Node> nodes = null;
		Node node = null;
		for (int i = 0 ; i < resource.length; i++) {
			doc = resource[i];
			nodes = getNodeList("beans/bean", doc);
			if (nodes == null) continue;
			for (int y = 0; y < nodes.size(); y++) {
				node = (Node) nodes.get(y);
				if (sign.equals(getAttributeOf("name", node)))
					return node;
			}
		}
		for (int i = 0 ; i < resource.length; i++) {
			doc = resource[i];
			nodes = getNodeList("beans/bean", doc);
			if (nodes == null) continue;
			for (int y = 0; y < nodes.size(); y++) {
				node = (Node) nodes.get(y);
				if (sign.equals(getAttributeOf("class", node)))
					return node;
			}
		}

		return null;
	}

	/****
	 * 根据提供的类型精确匹配XML元素
	 * @param type
	 * @return
	 */
	public Node getElementByType(Class<?> type) {
		Document doc = null;
		String className = null;
		//完全等价模式
		for (int i = 0 ; i < resource.length; i++) {
			doc = resource[i];
			List<Node> list = getNodeList("beans/bean", doc);
			for (Iterator<Node> it = list.iterator(); it.hasNext();) {
				Node e = it.next();
				className = getAttributeOf("class", e);
				if (className.equals(type.getName()))
					return e;
			}
		}

		return null;
	}

	/****
	 * 根据提供的类型模糊匹配XML元素
	 * @param type
	 * @return
	 * @throws Exception
	 */
	public Node getElementByFuzzyType(Class<?> type) throws Exception {
		Class<?> cl = null;
		Document doc = null;
		String className = null;
		//继承实现模式
		for (int i = 0 ; i < resource.length; i++) {
			doc = resource[i];
			List<Node> list = getNodeList("beans/bean", doc);
			for (Iterator<Node> it = list.iterator(); it.hasNext();) {
				Node e = (Node) it.next();
				className = getAttributeValue("class", e);
				if (className == null) continue;
				try {
					cl = Class.forName(className);
					if (type.isAssignableFrom(cl))
						return e;
				} catch (ClassNotFoundException e1) {
					e1.printStackTrace();
				}
			}
		}
		//代理模式
		Class<?> protype = null;
		Object target = null;
		for (int i = 0 ; i < resource.length; i++) {
			doc = resource[i];
			List<Node> list = getNodeList("beans/bean", doc);
			for (Iterator<Node> it = list.iterator(); it.hasNext();) {
				Node e = it.next();
				className = getAttributeValue("class", e);
				if (className == null) continue;
				try {
					protype = Class.forName(className);
					if (IocBaseContext.isProxyFactoryBean(protype)) {
						target = createBeanByConfigration(e, false).getSource();
						if (target != null) {
							Class<?>[] arr = target.getClass().getInterfaces();
							for (Class<?> c : arr) {
								if (type.isAssignableFrom(c)) {
									System.out.println(">>>>>>>>>代理类匹配成功!!!");
									return e;
								}
							}
						}
					}
				} catch (ClassNotFoundException e1) {
					e1.printStackTrace();
				}

			}
		}
		return null;
	}


	/**
	 * 判断是否是工厂模式
	 * @param element
	 * @return
	 */
	public boolean isFactoryModel(Node element) {
		try {
			return getFactoryModel(element) != null;
		} catch (ClassNotFoundException e) {
			return false;
		}
	}

	public String getFactoryModel(Node element) throws ClassNotFoundException {
		String packageName = getAttributeValue("class", element);
		String factoryMethod = getAttributeValue(Consts.factoryMethod, element);
		if (factoryMethod != null) {
			String factoryBean = getAttributeValue(Consts.factoryBean, element);
			if (factoryBean != null) return Consts.objectModel;
			return Consts.staticModel;
		} else {
			if ((FactoryBean.class).isAssignableFrom(Class.forName(packageName))) {
				return Consts.beanAwareModel;
			}
		}
		return null;
	}

	// 通过工厂获得对象
	private Object createFactoryBean(Node element) throws Exception {
		Method md = null;
		Object result = null;
		String factoryMethod = getAttributeValue(Consts.factoryMethod, element);
		String model = getFactoryModel(element);
		if (model.equals(Consts.staticModel)) {
			String packageName = getAttributeValue("class", element);
			if (packageName == null)
				return null;
			Class<?> cl = Class.forName(packageName);
			md = cl.getMethod(factoryMethod, null);
			if (md == null)
				return null;
			result = md.invoke(null);
		} else if (model.equals(Consts.objectModel)) {
			String factoryBean = getAttributeValue(Consts.factoryBean,
					element);
			Object obj = getBean(factoryBean);
			md = obj.getClass().getMethod(factoryMethod, null);
			if (md == null) return null;
			result = md.invoke(obj);
		} else if (model.equals(Consts.beanAwareModel)) {
			String packageName = getAttributeValue("class", element);
			Object impl = Class.forName(packageName).newInstance();
			result = ((FactoryBean) impl).getObject();
		}
		toRunInitMethod(element, result);
		return result;
	}

	/****
	 * *获取scope为request或session的实例***
	 * ****/
	public Map<String, Object> getScopeBeans(String signScope) {
		String scope = null;
		Document doc = null;
		Map<String, Object> data = new HashMap<String, Object>();
		for (int i = 0; i < resource.length; i++) {
			doc = resource[i];
			List<Node> list = getNodeList("bean", doc);
			for (Iterator<Node> it = list.iterator(); it.hasNext();) {
				Node e = it.next();
				String sign = getSignOfElementBean(e);
				scope = getAttributeOf(Consts.scope, e);
				if (signScope.equalsIgnoreCase(scope)) {
					try {
						data.put(sign, getBean(sign));
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			}
		}
		return data;
	}

	/***
	 * ***获得映射对象属性的数据类型***
	 * */
	public Class<?> getTypeByAttributeName(Class<?> cl, String attributeName) {
		Field[] fields = cl.getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			if (attributeName.equals(fields[i].getName())) {
				return fields[i].getType();
			}
		}
		fields = cl.getSuperclass().getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			if (attributeName.equals(fields[i].getName())) {
				return fields[i].getType();
			}
		}
		return null;
	}

	/***
	 * mvc子容器继承父上下文资源 规则为子上下文可以访问父上下文资源 父上下文无法访问子上下文
	 *
	 * @param factory
	 */
	public void setParent(XmlBeanFactory factory) {
		List<Document> context = new ArrayList<Document>();
		for (Document d : resource) {
			context.add(d);
		}
		for (Document d : factory.getResource()) {
			context.add(d);
		}
		// 合并父子上下文资源
		this.resource = context.toArray(new Document[context.size()]);
		this.getDataSource().putAll(factory.getDataSource()); // 继承单例池
		this.getPrototypePool().putAll(factory.getPrototypePool());
		this.getDataConfig().putAll(factory.getDataConfig()); // 合并配置文件
		this.getAdvisors().addAll(factory.getAdvisors()); // 合并aop适配器
		this.setAspectScanable(factory.aspectScanable()); // 连接父容器的aspect扫描开关
		this.setBasePackage(factory.getBasePackage());
		this.getControllers().putAll(factory.getControllers()); // 合并controller控制器
		this.getExceptionHandles().addAll(factory.getExceptionHandles()); // 合并异常处理器
	}

	/*******
	 * Spring在此销毁本次任务创建 的所有对象释放资源
	 */
	public void destroySingletons() {
		Object bean = null;
		Method md = null;
		String destroyMethod = null;
		for (Iterator<?> it = getDataSource().keySet().iterator(); it.hasNext();) {
			String id = it.next().toString();
			SourceItem si = getDataSource().get(id);
			destroyMethod = si.getDestroyMethod();
			bean = si.getSource();
			if (bean instanceof DisposableBean) {
				((DisposableBean) bean).destroy(); //接口销毁
				continue;
			}
			if (bean != null && destroyMethod != null) {
				try {
					md = bean.getClass().getMethod(destroyMethod);
					if (md != null)
						md.invoke(bean); // 方法配置销毁
				} catch (SecurityException e) {
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					System.out.println("no destroy-method : \r\n"
							+ e.getMessage());
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		}
		getDataSource().clear(); // 清空所有数据资源
	}

	/***
	 * 属性强制赋值
	 * @param name
	 * @param implCl
	 * @param impl
	 * @param e
	 * @param type
	 * @throws Exception
	 */
	private void setAccessibleField(String name, Class<?> implCl, Object impl,
									Node e, Class<?> type) throws Exception {
		Field field = parseField(name, implCl);
		if (field != null) {
			Object relBean = controller(e, type);
			field.setAccessible(true);
			try {
				field.set(impl, relBean);
				field.setAccessible(false);
			} catch (Exception e1) {
				throw new PropertyCastException(type, relBean);
			}
		}
	}
}
