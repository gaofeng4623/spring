package com.spring.mvc.controller;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.spring.beans.common.annotation.RestController;
import com.spring.beans.common.utils.Server;
import com.spring.beans.factory.BeanFactory;
import com.spring.beans.factory.config.EditorConfigurer;
import com.spring.context.WebApplicationContext;
import com.spring.mvc.common.ModelAndView;
import com.spring.mvc.common.RequestContextUtils;
import com.spring.mvc.common.annotation.ExceptionHandler;
import com.spring.mvc.common.annotation.InitBinder;
import com.spring.mvc.common.annotation.RequestMapping;
import com.spring.mvc.common.annotation.ResponseBody;
import com.spring.mvc.common.exception.ParseRequestMethodException;
import com.spring.mvc.servlet.exresolver.AnnotationExceptionResolver;

public class AnnotationController extends AbstractController {
	private Object controller;
	private String controllerId;
	private String methodPath;
	private Method method;
	private Method dataBinder;
	private boolean responseBody;
	private boolean restController;
	private String requestMethod;
	private Class<?> prototype;
	private final boolean isRequestMapping = true;
	private BeanFactory factory;

	public AnnotationController() {
	}

	public AnnotationController(String controllerId, String methodPath,
			Method method, Method dataBinder, boolean responseBody, String requestMethod,
			Class<?> prototype, BeanFactory factory) {
		this.controllerId = controllerId;
		this.methodPath = methodPath;
		this.method = method;
		this.dataBinder = dataBinder;
		this.responseBody = responseBody;
		this.requestMethod = requestMethod;
		this.prototype = prototype;
		this.factory = factory;
	}

	@Override
	public ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		if (!parseRequestMethod(request))
			throw new ParseRequestMethodException();
		return new HandleRequestResolver().handleRequestInternal(request,
				response, getPrototype(), getMethodPath(), getMethod(), getDataBinder(),
				getController(), getFactory(), null, isResponseBody(), isRestController(),
				isRequestMapping());
	}

	public void loadingController(HttpServletRequest request) throws Exception {
		/**
		 * AnnotationController的controller应该先为注解的ID
		 * 然后通过mvccontext获取,因为有可能出现非单例情况
		 **/
		WebApplicationContext mvcContext = RequestContextUtils
				.getWebApplicationContext(request);
		setController(mvcContext.getBean(getControllerId()));
	}

	public boolean parseRequestMethod(HttpServletRequest request) {
		String method = request.getMethod();
		return "".equals(requestMethod) || method.equals(requestMethod);
	}

	/**
	 * 装载controller配置信息 该方法为BasicBeanFactory提供服务
	 * 
	 * @param beanId
	 * @param cl
	 * @param controllers
	 */
	public static void fillControllerPool(String beanId, Class<?> cl,
			Map<String, Object> controllers, List exceptionHandles, BeanFactory factory) {
		RequestMapping baseMapping = (RequestMapping) cl
				.getAnnotation(RequestMapping.class);
		String basePath = baseMapping == null ? "" : baseMapping.value();
		basePath = Server.isEmpty(basePath) ? basePath : (basePath
				.startsWith("/") ? basePath : "/" + basePath);
		Method[] methods = cl.getMethods();
		Method initDateBinder = publishDateBinderMethod(methods); //数据绑定方法
		for (Method m : methods) {
			if (m.isAnnotationPresent(RequestMapping.class)) {
				RequestMapping methodMapping = (RequestMapping) m
						.getAnnotation(RequestMapping.class);
				String mappingValue = methodMapping.value();
				mappingValue = mappingValue.contains(".") ? mappingValue
						.substring(0, mappingValue.lastIndexOf("."))
						: mappingValue;
				mappingValue = mappingValue.startsWith("/") ? mappingValue
						: "/" + mappingValue;
				String httpMethod = "_" + methodMapping.method();
				String methodPath = basePath + mappingValue;
				AnnotationController annocontrol = new AnnotationController(
						beanId, methodPath, m, initDateBinder,
						m.isAnnotationPresent(ResponseBody.class),
						methodMapping.method(), cl, factory);
				if (cl.isAnnotationPresent(RestController.class)) {
					annocontrol.setRestController(true);
				}
				controllers.put(methodPath + httpMethod, annocontrol);
			} else if (m.isAnnotationPresent(ExceptionHandler.class)) {
				ExceptionHandler expHandler = (ExceptionHandler) m
						.getAnnotation(ExceptionHandler.class);
				AnnotationExceptionResolver annoresolver = new AnnotationExceptionResolver(
						beanId, m, initDateBinder, cl, expHandler.value(), factory);
				exceptionHandles.add(annoresolver);
			}
		}
	}

	// 获得@InitBinder初始化方法
	private static Method publishDateBinderMethod(Method[] methods) {
		for (Method md : methods) {
			if (md.isAnnotationPresent(InitBinder.class)) {
				Class<?>[] types = md.getParameterTypes();
				if (types.length == 1 && EditorConfigurer.class.isAssignableFrom(types[0])) {
					return md;
				}
			}
		}
		return null;
	}

	// @ModelAttribute待定
	public Object getController() {
		return controller;
	}

	public void setController(Object controller) {
		this.controller = controller;
	}

	public Method getMethod() {
		return method;
	}

	public void setMethod(Method method) {
		this.method = method;
	}

	public String getRequestMethod() {
		return requestMethod;
	}

	public void setRequestMethod(String requestMethod) {
		this.requestMethod = requestMethod;
	}

	public boolean isResponseBody() {
		return responseBody;
	}

	public void setResponseBody(boolean responseBody) {
		this.responseBody = responseBody;
	}

	public String getControllerId() {
		return controllerId;
	}

	public void setControllerId(String controllerId) {
		this.controllerId = controllerId;
	}

	public Class<?> getPrototype() {
		return prototype;
	}

	public void setPrototype(Class<?> prototype) {
		this.prototype = prototype;
	}

	public boolean isRestController() {
		return restController;
	}

	public void setRestController(boolean restController) {
		this.restController = restController;
	}

	public String getMethodPath() {
		return methodPath;
	}

	public void setMethodPath(String methodPath) {
		this.methodPath = methodPath;
	}

	public boolean isRequestMapping() {
		return isRequestMapping;
	}

	public BeanFactory getFactory() {
		return factory;
	}

	public void setFactory(BeanFactory factory) {
		this.factory = factory;
	}

	public Method getDataBinder() {
		return dataBinder;
	}

	public void setDataBinder(Method dataBinder) {
		this.dataBinder = dataBinder;
	}
	
}
