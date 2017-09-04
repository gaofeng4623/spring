package com.spring.beans.factory;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.annotation.Resource;
import javax.sql.DataSource;

import com.spring.beans.aware.FactoryBean;
import com.spring.beans.common.PrototypeItem;
import com.spring.beans.common.SourceItem;
import com.spring.beans.common.annotation.Autowired;
import com.spring.beans.common.annotation.Component;
import com.spring.beans.common.annotation.Controller;
import com.spring.beans.common.annotation.DependsOn;
import com.spring.beans.common.annotation.PostConstruct;
import com.spring.beans.common.annotation.PreDestroy;
import com.spring.beans.common.annotation.Qualifier;
import com.spring.beans.common.annotation.Repository;
import com.spring.beans.common.annotation.RestController;
import com.spring.beans.common.annotation.Service;
import com.spring.beans.common.exception.BeanNotFoundException;
import com.spring.beans.common.exception.NoUniqueBeanDefinitionException;
import com.spring.beans.common.utils.Consts;
import com.spring.beans.common.utils.Server;
import com.spring.beans.factory.config.CustomEditorConfigurer;
import com.spring.beans.factory.config.EditorConfigurer;

/**
 * @author 高峰
 *
 */ 
@SuppressWarnings("all")
public abstract class BasicCoreBeanFactory extends Server implements BeanFactory{
	private Map<String, SourceItem> dataSource = new LinkedHashMap<String, SourceItem>();
	private Map<String, Object> prototypePool = new ConcurrentHashMap<String, Object>();
	private Map<String, Object> controllers = new ConcurrentHashMap<String, Object>();
	private List exceptionHandles = new ArrayList();
	private Map<String, Map> dataConfig = new ConcurrentHashMap<String, Map>();
	private Map<String, DataSource> DataSources = new ConcurrentHashMap<String, DataSource>();
	private Set<Class<?>> packageAnnotations = null;
	private List<?> annotationAdvices = new ArrayList(); //通知注解
	private List<Object> advisors = new ArrayList<Object>(); //advisors适配器
	private EditorConfigurer editorConfigurer; //属性解析器
	
	public BasicCoreBeanFactory() {
		super();
	}
	
	public int getDataSourceSize() {
		return this.dataSource.size();
	}
	
	public Set getDataSourceNames() {
		return this.dataSource.keySet();
	}
	
	public Map<String, SourceItem> getDataSource() {
		return dataSource;
	}

	public void setDataSource(Map<String, SourceItem> dataSource) {
		this.dataSource = dataSource;
	}
	
	public int getPrototypePoolSize() {
		return this.dataSource.size();
	}
	
	public Set getPrototypePoolNames() {
		return this.prototypePool.keySet();
	}
	
	public Map<String, Object> getPrototypePool() {
		return prototypePool;
	}

	public void setPrototypePool(Map<String, Object> prototypePool) {
		this.prototypePool = prototypePool;
	}
	
	public Map<String, Object> getControllers() {
		return controllers;
	}

	public void setControllers(Map<String, Object> controllers) {
		this.controllers = controllers;
	}
	
	public List getExceptionHandles() {
		return exceptionHandles;
	}

	public void setExceptionHandles(List exceptionHandles) {
		this.exceptionHandles = exceptionHandles;
	}

	public Map<String, Map> getDataConfig() {
		return dataConfig;
	}

	public void setDataConfig(Map<String, Map> dataConfig) {
		this.dataConfig = dataConfig;
	}
	
	public Map<String, DataSource> getDataSources() {
		return DataSources;
	}

	public void setDataSources(Map<String, DataSource> dataSources) {
		DataSources = dataSources;
	}
	
	public List<Object> getAdvisors() {
		return advisors;
	}

	public void setAdvisors(List<Object> advisors) {
		this.advisors = advisors;
	}

	public List<?> getAnnotationAdvices() {
		return annotationAdvices;
	}

	public void setAnnotationAdvices(List<?> annotationAdvices) {
		this.annotationAdvices = annotationAdvices;
	}

	public EditorConfigurer getEditorConfigurer() {
		return editorConfigurer;
	}

	public void setEditorConfigurer(EditorConfigurer editorConfigurer) {
		this.editorConfigurer = editorConfigurer;
	}
	
	//创建属性转换配置器
	public EditorConfigurer createPropertyEditorConfiger() {
		try {
			return getBean(EditorConfigurer.class);
		} catch (Exception e) {
			return new CustomEditorConfigurer();
		}
	}

	public abstract Object getBean(Object param) throws Exception; //注解时调用
	
	public abstract Object getBeanByProperty(String beanId, Class<?> type) throws Exception; //匹配属性时调用
	
	public abstract void containerAware(Object object, String beanName); //容器感应接口
	
	public abstract void reflectBeforeAware(Object bean); //前置初始化接口
	
	public abstract void reflectAfterAware(Object bean); //后置初始化接口
	
	public abstract void afterPropertiesSet(Object bean); //属性注入后，初始化前调用
	
	public abstract void initPropertyEditorConfig(Object bean); //配置属性解析器
	
	/***
	 * 扫描指定包下的注解
	 * ****/
	public Set<Class<?>> getPackageAnnotations(String packageName) {
		if (this.packageAnnotations == null) {
			this.packageAnnotations = getclasses(packageName);
		}
		return packageAnnotations;
	}
	
	/***
	 * 根据配置中的包名查找指定的bean
	 * @param packageName
	 * @param param
	 * @return
	 * @throws Exception 
	 */
	public SourceItem getAnnotationByID(String packageName, String beanId) throws Exception {
		Class<?> cl = null;
		Set<Class<?>> set = getPackageAnnotations(packageName);
		for (Iterator<Class<?>> it = set.iterator(); it.hasNext();) {
			cl = it.next();
			if (matchId(cl, beanId))
				return getBeanByAnnotation(cl, true);
		}
		return null;
	}
	
	
	/***
	 * 获取注解bean的作用域
	 * @param packageName
	 * @param beanId
	 * @return
	 */
	public String getAnnotationScopeByModel(String packageName, 
			Object param, String model) {
		Class<?> cl = null;
		Set<Class<?>> set = getPackageAnnotations(packageName);
		for (Iterator<Class<?>> it = set.iterator(); it.hasNext();) {
			cl = it.next();
			if (model.equalsIgnoreCase(Consts.ByIdModel)) {
				if (matchId(cl, param.toString()))
					return getScopeOfAnnotation(cl);
			} else if (model.equalsIgnoreCase(Consts.ProtoTypeModel)) {
				if (matchClass(cl, (Class<?>)param))
					return getScopeOfAnnotation(cl);
			} else if (model.equalsIgnoreCase(Consts.FuzzyTypeModel)) {
				if (matchFuzzyClass(cl, (Class<?>)param))
					return getScopeOfAnnotation(cl);
			}
			
		}
		return null;
	}
	
	
	/***
	 * 扫描指定目录下所有的单例注解对象
	 * @param packageName
	 * @return
	 * @throws Exception 
	 */
	public Map<String, SourceItem> getAnnotationBeansByDirectory(String packageName) throws Exception {
		Class<?> cl = null;
		SourceItem si = null;
		Map<String, SourceItem> beans = new HashMap<String, SourceItem>();
		Set<Class<?>> set = getPackageAnnotations(packageName);
		for (Iterator<Class<?>> it = set.iterator(); it.hasNext();) {
			cl = it.next();
			if (hasAnnotation(cl)) {
				String beanId = getKeyOfAnnotationBean(cl);
				if (isSingleton(cl)) {	//仅加载单例模式的bean
					si = getBeanByAnnotation(cl, true);
					if (si != null) 
						beans.put(beanId, si);
				}
				if (isController(cl)) { //装载controller控制器模型
					fillControllerPool(beanId, cl, this.getControllers(),
							this.getExceptionHandles());
				}
			}
		}
		return beans;
	}
	
	
	
	/**
	 * 装载controller配置信息
	 * @param beanId
	 * @param cl
	 * @param controllers
	 */
	private void fillControllerPool(String beanId, Class<?> cl,
			Map<String, Object> controllers, List<?> exceptionHandles) {
		try {
			IocBaseContext.fillControllerPool(beanId, cl, controllers, exceptionHandles, this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	/***
	 * 根据类型精确查找注解元素
	 * @param packageName
	 * @param type
	 * @return
	 * @throws Exception 
	 */
	
	public SourceItem getAnnotationByProtoType(String packageName, Class<?> type) throws Exception {
		Class<?> cl = null;
		Set<Class<?>> set = getPackageAnnotations(packageName);
		for (Iterator<Class<?>> it = set.iterator(); it.hasNext();) {
			cl = it.next();
			if (matchClass(cl, type))
				return getBeanByAnnotation(cl, true);
		}
		return null;
	}
	
	/***
	 * 根据类型模糊查找注解元素
	 * @param packageName
	 * @param type
	 * @return
	 * @throws Exception 
	 */
	public SourceItem getAnnotationByFuzzyType(String packageName, Class<?> type) throws Exception {
		Class<?> cl = null;
		Set<Class<?>> set = getPackageAnnotations(packageName);
		for (Iterator<Class<?>> it = set.iterator(); it.hasNext();) {
			cl = it.next();
			if (matchFuzzyClass(cl, type))
				return getBeanByAnnotation(cl, true);
		}
		return null;
	}
	
	
	/**
	 * 根据提供的原型组装Bean
	 * @param cls
	 * @return
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	public SourceItem getBeanByAnnotation(Class<?> cls, boolean hasCached) throws Exception {
		Object impl = null;
		String initMethod = null;
		SourceItem sourceItem = null;
		createBeanOfDependsOn(cls); //bean之间依赖关系
		impl = cls.newInstance();
		String beanId = getKeyOfAnnotationBean(cls);
		String scope = getScopeOfAnnotation(cls);
		this.containerAware(impl, beanId);
		createBeanByAnnotation(cls, impl);
		this.afterPropertiesSet(impl);
		this.reflectBeforeAware(impl); //介于属性注入完毕执行初始化之前
		Method method = getAnnotationInitMethod(cls);
		if (method != null) initMethod = method.getName();
		Initialization(method, impl); //bean初始化
		this.reflectAfterAware(impl); //介于执行初始化之后
		impl = reflectFactoryBeanAware(impl);
		String destroyMethod = getAnnotationDestroyMethod(cls);
		SourceItem cache = getDataSource().get(beanId);
		sourceItem = cache == null ? new SourceItem(beanId, impl.getClass().getName(),
		scope, impl, impl, initMethod, destroyMethod, false) : getDataSource().get(beanId); //注解缓存
		if (hasCached) {
			if (isPrototype(scope)) {
				addPrototypePool(beanId, cls.getName(), cls, getPrototypePool());
			} else {
				if (cache == null)
					addSource(sourceItem, getDataSource());
			}
		}
		
		return sourceItem;
	}
	
	/**********工厂接口********/
	private Object reflectFactoryBeanAware(Object bean) {
		if (bean instanceof FactoryBean) {
			bean = ((FactoryBean) bean).getObject();
		}
		return bean;
	}
	
	/***处理bean的依赖关系***/
	private void createBeanOfDependsOn(Class<?> cls) {
		if (cls.isAnnotationPresent(DependsOn.class)) {
			DependsOn dps = (DependsOn) cls.getAnnotation(DependsOn.class);
			String name = dps.value();
			if (name != null && !"".equals(name)) {
				try {
					getBean(name);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	
	/****
	 * (Resource)组装注解资源
	 * @param cls
	 * @param fields
	 * @throws Exception 
	 */
	private void createBeanByAnnotation(Class<?> cls, Object impl) throws Exception {
		Resource rc = null;
		Autowired auto = null;
		Qualifier qfier = null;
		Method method = null;
		Method[] methods = cls.getMethods();
		Field[] fields = cls.getDeclaredFields();
		for (Field f : fields) {
			Object result = null;
			Object methodSource = null;
			if (f.isAnnotationPresent(Resource.class)) {
				rc = f.getAnnotation(Resource.class);
				result = getBeanByAnnotationOfResource(rc, f.getName(), f.getType());
			} else if (f.isAnnotationPresent(Autowired.class)) {
				auto = f.getAnnotation(Autowired.class);
				qfier = (Qualifier) f.getAnnotation(Qualifier.class);
				result = getBeanByAnnotationOfAutowired(auto, qfier, f.getType()
						, cls.getName(), f.isAnnotationPresent(Qualifier.class));
			}
			if (result != null) {
				method = getWriteMethod(methods, f.getName());
				if (method != null)
					method.invoke(impl, result);
				else {
					f.setAccessible(true);
					f.set(impl, result);
				}
			}
			method = getWriteMethod(methods, f.getName());
			if (method != null) {
				if (method.isAnnotationPresent(Resource.class)) {
					rc = method.getAnnotation(Resource.class);
					methodSource = getBeanByAnnotationOfResource(
							rc, f.getName(), f.getType());
				} else if (method.isAnnotationPresent(Autowired.class)) {
					auto = method.getAnnotation(Autowired.class);
					qfier = (Qualifier) method.getAnnotation(Qualifier.class);
					methodSource = getBeanByAnnotationOfAutowired(auto, qfier, f.getType()
							, cls.getName(), method.isAnnotationPresent(Qualifier.class));
				}
				if (methodSource != null) {
					method.invoke(impl, methodSource);
				}
			}
		}
	}
	
	
	
	
	/*****
	 * 获得Resource注解资源
	 * @param <T>
	 * @param rc
	 * @param filedName
	 * @param type
	 * @return
	 * @throws Exception 
	 */
	private Object getBeanByAnnotationOfResource(Resource rc, 
			String filedName, Class<?> type) throws Exception {
		Object result = null;
		String name = rc.name();
		if (!isEmpty(name)) {
			result = getBeanByProperty(name, type);
		} else {
			result = getBeanByProperty(filedName, type);
		}
		if (result == null) {
			try {
				result = getBean(type);
			} catch (Exception e) {
				e.printStackTrace();
			}	
		}
		return result;
	}
	
	
	/*****
	 * 获得Autowired注解资源
	 * @param auto
	 * @param qfier
	 * @param type
	 * @param ClassName
	 * @param hasQualifier
	 * @return
	 * @throws BeanNotFoundException
	 */
	private Object getBeanByAnnotationOfAutowired(Autowired auto, Qualifier qfier
			, Class<?> type, String ClassName, boolean hasQualifier) throws Exception {
		Object result = null;
		String name = null;
		if (hasQualifier) {
			name = qfier.value();
			result = getBeanByProperty(name, type);
		} else 
			result = getBean(type);
		if (auto.required() && result == null)
			throw new BeanNotFoundException("the property " 
					+ type.getSimpleName() + " of "
					+ ClassName + " can't be null");
		return result;
	}
	
	/*****
	 * 检索创建过的bean
	 * 1先匹配完全等价的类型，再匹配继承或实现，
	 * 2然后匹配代理，最后匹配继承或实现的代理类.
	 * @param id
	 * @return
	 */
	public SourceItem getSource(Object sign) throws NoUniqueBeanDefinitionException {
		if (sign instanceof String)
			return getDataSource().get(sign);
		else if (sign instanceof Class) {
			List<SourceItem> result = getSourceByClass((Class)sign);
			if (result.size() > 1) {
				String beanIds = getBeanIds(result);
				String message = "No qualifying bean of type [%s] is defined: \n" +
						"expected single matching bean but found %s : %s";
				message = String.format(message, ((Class)sign).getName(), result.size(), beanIds);
				throw new NoUniqueBeanDefinitionException(message);
			} else if (result.size() == 1) {
				return result.get(0);
			} else {
				SourceItem si = getProxySourceByClass((Class)sign);
				if (si != null) return si;
			}
		}
		return null;
	}

	public String getBeanIds(List<SourceItem> result) {
		if (result != null && result.size() > 0) {
			String beanIds = "";
			for (SourceItem item : result) {
				beanIds += item.getSourceId() + ",";
			}
			return beanIds;
		}
		return "";
	}

	/****
	 * 这是一个内嵌方法，用于匹配单例池中
	 * 指定类型的元素或继承实现该类型的元素。
	 * @param type
	 * @return
	 */
	public List<SourceItem> getSourceByClass(Class<?> type) {
		List<SourceItem> result = new ArrayList<SourceItem>();
		for (Iterator<?> it = getDataSource().keySet().iterator(); it.hasNext();) {
			String key = String.valueOf(it.next());
			SourceItem si = (SourceItem) getDataSource().get(key);
			if (type.isAssignableFrom(si.getSource().getClass()))
				result.add(si);
		}
		return result;
	}
	
	/****
	 * 这是一个内嵌方法，用于匹配单例池中指定
	 * 类型的元素代理类或继承实现该类型的元素代理类。
	 * @param type
	 * @return
	 */
	
	public SourceItem getProxySourceByClass(Class<?> type) {
		for (Iterator it = getDataSource().keySet().iterator(); it.hasNext();) {
			String key = String.valueOf(it.next());
			SourceItem si = (SourceItem) getDataSource().get(key);
			if (si.getProtypeClass().equals(type.getName()))
				return si;
		}
		for (Iterator it = getDataSource().keySet().iterator(); it.hasNext();) {
			String key = String.valueOf(it.next());
			SourceItem si = (SourceItem) getDataSource().get(key);
			if (si.getProtypeClass() != null) {
				try {
					if (type.isAssignableFrom(Class.forName(si.getProtypeClass())))
							return si;
				} catch (ClassNotFoundException e) {
					System.out.println(e.getMessage());
				}
			}
		}
		return null;
	}
	
	
	/****
	 * 获得原型模型
	 * @param sign
	 * @return
	 */
	public PrototypeItem getPrototypeSource(Object sign) {
		Class cl = null;
		String packName = null;
		if (sign instanceof String)
			return (PrototypeItem) getPrototypePool().get(sign);
		else if (sign instanceof Class) {
			packName = ((Class<?>)sign).getName();
			for (Iterator<?> it = getPrototypePool().keySet().iterator(); it.hasNext();) {
				String key = String.valueOf(it.next());
				PrototypeItem pi = (PrototypeItem) getPrototypePool().get(key);
				if (pi.getProtypeClass().equals(packName))
					return pi;
			}
			for (Iterator<?> it = getPrototypePool().keySet().iterator(); it.hasNext();) {
				String key = String.valueOf(it.next());
				PrototypeItem pi = (PrototypeItem) getPrototypePool().get(key);
				if (pi.getProtypeClass() != null) {
					try {
						if (((Class<?>)sign).isAssignableFrom(Class.forName(pi.getProtypeClass())))
								return pi;
					} catch (ClassNotFoundException e) {
						System.out.println(e.getMessage());
					}
				}
			}
			
		}
		return null;
	}
	
	
	
	/****
	 * ***添加到单例池****
	 * ******/
	public void addSource(SourceItem si, Map dataSource) {		
		if (si.getSourceId() != null && si.getSource() != null) {
			dataSource.put(si.getSourceId(), si);
		}
	}
	
	/****
	 * ***添加到原型池****
	 * ******/
	public void addPrototypePool(String sign, String prototype, Object object, Map<String,Object> pool) {
		PrototypeItem pi = new PrototypeItem(sign, prototype, object);
		pool.put(sign, pi); //将模型存入原型池
	}
	
	
	
	private Set<Class<?>> getclasses(String packName) {
		Set<Class<?>> classes = new LinkedHashSet<Class<?>>();
		boolean flag = true;// 是否循环迭代
		String packDir = packName.replace(".", "/");
		Enumeration<URL> dir;
		try {
			dir = Thread.currentThread().getContextClassLoader().getResources(
					packDir);
			while (dir.hasMoreElements()) {
				URL url = dir.nextElement();
				//System.out.println("url:***" + url);
				String protocol = url.getProtocol();// 获得协议号
				if ("file".equals(protocol)) {
					String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
					findAndAddClassesInPackageByFile(packName, filePath, flag,
							classes);
				} else if ("jar".equals(protocol)) {
					JarFile jar;
					jar = ((JarURLConnection) url.openConnection())
							.getJarFile();
					Enumeration<JarEntry> entries = jar.entries();
					while (entries.hasMoreElements()) {
						JarEntry entry = entries.nextElement();
						String name = entry.getName();
						if (name.endsWith(".class")) {
							name = name.substring(0,name.length()-6);
							if (name.indexOf("/") > 0) 
								name = name.replace("/", ".");
							classes.add(Class.forName(name));
						}
					}
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return classes;

	}

	private static void findAndAddClassesInPackageByFile(String packName,
			String filePath, final boolean flag, Set<Class<?>> classes) {
		File dir = new File(filePath);
		if (!dir.exists() || !dir.isDirectory()) {
			System.out.println("此路径下没有文件");
			return;
		}
		File[] dirfiles = dir.listFiles(new FileFilter() {
			public boolean accept(File pathname) {
				return flag && pathname.isDirectory()
						|| pathname.getName().endsWith(".class");	
			}
		});
		for (File file : dirfiles) {
			if (file.isDirectory()) {// 如果是目录，继续扫描
				findAndAddClassesInPackageByFile(packName + "."
						+ file.getName(), file.getAbsolutePath(), flag, classes);
			} else {// 如果是文件
				String className = file.getName().substring(0,
						file.getName().length() - 6); 
				//System.out.println("类名：" + className);
				try {
					classes.add(Thread.currentThread().getContextClassLoader()
							.loadClass(packName + "." + className));
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
	/***********************获取aspect切面通知开始********************************/
	/***
	 * 根据注解获得切面集合 
	 * @param packageName
	 * @return
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws BeanNotFoundException 
	 */
	
	public List<?> getAspectsByAnnotation(String packageName) throws Exception {
		Class cl = null;
		Object aspect = null;
		Map globalPointcuts = new HashMap(); //可重用全局切入点
		List totalAdvices = new ArrayList();
		Set set = getPackageAnnotations(packageName);
		List asptClasses = IocBaseContext.getAspectClasses(set, globalPointcuts);
		for (Iterator it = asptClasses.iterator(); it.hasNext();) {
			cl = (Class) it.next();
			aspect = hasAnnotation(cl) ? getBean(cl) : cl.newInstance();
			totalAdvices.addAll(IocBaseContext
					.getAdviceByMethods(cl, aspect, globalPointcuts)); //根据方法名生成切面通知
		}
		return totalAdvices;
	}
	
	/***********************获取aspect切面通知结束********************************/

	
	
	

	/****
	 * 匹配注解的bean
	 * @param cl
	 * @param param
	 * @return
	 */
	public boolean matchId(Class<?> cl, String beanId) {
		if (!hasAnnotation(cl)) return false;
		return beanId.equals(getKeyOfAnnotationBean(cl));
	}
	
	/****
	 * 精确匹配注解的bean
	 * @param cl
	 * @param param
	 * @return
	 */
	public boolean matchClass(Class<?> cl, Class<?> type) {
		if (!hasAnnotation(cl)) return false;
		return cl.getName().equals(type.getName());
	}
	
	/****
	 * 模糊匹配注解的bean
	 * @param cl
	 * @param param
	 * @return
	 */
	public boolean matchFuzzyClass(Class<?> cl, Class<?> type) {
		if (!hasAnnotation(cl)) return false;
		return type.isAssignableFrom(cl);
	}
	
	

	//获得初始化方法
	public Method getAnnotationInitMethod(Class<?> cls) {
		Method[] methods = cls.getMethods();
		for (Method md : methods) {
			if (md.isAnnotationPresent(PostConstruct.class)) {
				return md;
			}
		}
		return null;
	}
	
	//获得销毁方法
	public String getAnnotationDestroyMethod(Class<?> cls) {
		Method[] methods = cls.getMethods();
		for (Method md : methods) {
			if (md.isAnnotationPresent(PreDestroy.class)) {
				return md.getName();
			}
		}
		return null;
	}

	//创建时调用初始化方法
	private void Initialization(Method method, Object impl) {
		if (method != null) {
			try {
				method.invoke(impl);
			} catch (Exception e) {
				System.out.println("initMethod Erro --" + method.getName());
			} 
		}
		
	}
	
	
	/****
	 * 获得注解bean的唯一ID
	 * @param cls
	 * @return
	 */
	public String getKeyOfAnnotationBean(Class<?> cls) {
		String beanId = null;
		if (cls.isAnnotationPresent(Component.class)) {
			Component component = (Component) cls.getAnnotation(Component.class);
			beanId = component.value();
		} else if (cls.isAnnotationPresent(Service.class)) {
			Service service = (Service) cls.getAnnotation(Service.class);
			beanId = service.value();
		} else if (cls.isAnnotationPresent(Controller.class)) {
			Controller controler = (Controller) cls.getAnnotation(Controller.class);
			beanId = controler.value();
		} else if (cls.isAnnotationPresent(RestController.class)) {
			RestController controler = (RestController) cls.getAnnotation(RestController.class);
			beanId = controler.value();
		} else if (cls.isAnnotationPresent(Repository.class)) {
			Repository repository = (Repository) cls.getAnnotation(Repository.class);
			beanId = repository.value();
		}
		if ("".equals(beanId) || beanId == null)
			beanId = getBeanIdByClassName(cls.getName());
		return beanId;
	}
}
