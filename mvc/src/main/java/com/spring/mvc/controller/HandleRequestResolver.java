package com.spring.mvc.controller;

import java.io.File;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.spring.beans.common.utils.ParameterNameDiscoverer;
import com.spring.beans.common.utils.Server;
import com.spring.beans.factory.BeanFactory;
import com.spring.beans.factory.config.EditorConfigurer;
import com.spring.beans.factory.config.WebDataBinder;
import com.spring.mvc.common.GUID;
import com.spring.mvc.common.ModelAndView;
import com.spring.mvc.common.ModelMap;
import com.spring.mvc.common.Util;
import com.spring.mvc.common.annotation.ModelAttribute;
import com.spring.mvc.common.annotation.RequestMapping;
import com.spring.mvc.common.annotation.RequestParam;
import com.spring.mvc.controller.convers.AnnotationConverterAdapter;
import com.spring.mvc.controller.convers.HttpMessageConverter;
import com.spring.mvc.controller.reflect.ControllerContext;
import com.spring.mvc.controller.reflect.Conver;
import com.spring.mvc.controller.reflect.DataBaseConver;
import com.spring.mvc.controller.reflect.FileUploadConver;

/**
 * @author mvc的核心数据绑定模块
 *
 */
public class HandleRequestResolver {
	
	/***
	 * 此接口双重用途，页面跳转和异常处理双重调用
	 * @param request
	 * @param response
	 * @param prototype
	 * @param methodPath
	 * @param method
	 * @param controller
	 * @param exception
	 * @param isResponseBody
	 * @param isRestController
	 * @param isRequestMapping
	 * @return
	 * @throws Exception
	 */
	public ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response, Class<?> prototype, String methodPath, Method method,
			Method initDataBinder, Object controller, BeanFactory factory, Throwable exception,
			boolean isResponseBody, boolean isRestController, boolean isRequestMapping) throws Exception {
		
		ModelMap modelMap = new ModelMap();
		EditorConfigurer configer = createDataBinder(initDataBinder, controller);
		Conver conver = createConverByRequestType(request, factory, configer); //获取数据转换器
		if (isRequestMapping) {
			handleModelAttributeMethods(modelMap, request, response,
				prototype, methodPath, controller, conver, exception); //初始化modelAttribute
		}
		Object[] parameters = reflectParametersOfMethod(request, response,
				prototype, methodPath, method, modelMap, exception, conver); //映射数据到方法参数
		Object result = handleMethodResult(method, parameters, controller);
		conver.doAfter(); //执行后续操作，如移除上传临时文件
		//处理返回值 1.void 2.responsebody 3.string 4.内部重定位(内部转向)
		if (Util.isSameType(String.class, method.getReturnType())) {
			if (result != null)
				return modelMap == null ? new ModelAndView((String) result)
					: new ModelAndView((String) result, modelMap);
		} else if (Util.isSameType(ModelAndView.class, method.getReturnType())) {
				if (result != null) {
					ModelAndView mv = (ModelAndView) result;
					if (modelMap != null) mv.setData(modelMap);
					return mv;
				}
		} else {
			AnnotationConverterAdapter adapter = new AnnotationConverterAdapter();
			if (isResponseBody) {
				HttpMessageConverter hc = adapter.lookupMessageConverter(request, response, null);
				if (hc != null) hc.write(result, request, response);
			} else if (isRestController) {
				RequestMapping mapping = (RequestMapping) method.getAnnotation(RequestMapping.class);
				String produces = mapping.produces();
				HttpMessageConverter hc = adapter.lookupMessageConverter(request, response, produces);
				if (hc != null) hc.write(result, request, response);
			}
		}

		return null;
	}
	
	
	private EditorConfigurer createDataBinder(Method initDataBinder, Object controller) {
		EditorConfigurer configer = new WebDataBinder();
		if (initDataBinder != null) {
			try {
				initDataBinder.invoke(controller, configer);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return configer;
	}


	/**
	 * 执行controller被访问的方法
	 */
	private Object handleMethodResult(Method method, Object[] parameters,
			Object controller) throws Exception {
		Object result = null;
		if (parameters != null && parameters.length > 0) {
			/*for (int i = 0; i < parameters.length; i++) {
				System.out.println("param" + i + " = " + parameters[i]);
			}*/
			result = method.invoke(controller, parameters);
		} else {
			result = method.invoke(controller);
		}
		return result;
	}
	

	/**
	 * 获取数据转换器
	 * @param request
	 * @param configer 
	 * @return
	 * @throws FileUploadException
	 */
	private Conver createConverByRequestType(HttpServletRequest request, BeanFactory factory, EditorConfigurer configer) throws FileUploadException {
		ServletFileUpload su = new ServletFileUpload(new DiskFileItemFactory());
		if (!su.isMultipartContent(request)) {
			return new DataBaseConver(factory, configer);
		} else {
			Conver conver = new FileUploadConver(factory, configer);
			List<FileItem> fileList = su.parseRequest(request);
			ControllerContext.put(fileList);
			String uploadpath = request.getSession().getServletContext()
				.getRealPath("/") + new GUID().toString();
			((FileUploadConver)conver).setTempPath(uploadpath);
			((FileUploadConver)conver).setTempDir(new File(uploadpath));
			return conver;
		}
	}

	/**
	 * 实现方法参数的数据映射
	 */
	protected Object[] reflectParametersOfMethod(HttpServletRequest request,
			HttpServletResponse response, Class<?> prototype, String methodPath, Method method,
			ModelMap modelMap, Throwable exception, Conver conver) throws Exception {
		Class<?>[] types = method.getParameterTypes();
		Annotation[][] annotations = method.getParameterAnnotations();
		String[] paramNames = handlePramaterNamesForAnnotation(prototype, method, annotations); //处理参数注解
		Map<String, String> pathVariableMap = createPathVariableMap(methodPath, method, request); //处理路径注解
		Object[] parameters = new Object[types.length];
		
		for (int i = 0 ; i < types.length; i++) {
			if (Util.isSameType(HttpServletRequest.class, types[i])) {
				parameters[i] = request;
			} else if (Util.isSameType(HttpServletResponse.class, types[i])) {
				parameters[i] = response;
			} else if (Util.isSameType(PrintWriter.class, types[i])) {
				parameters[i] = response.getWriter();
			} else if (Util.isSameType(HttpSession.class, types[i])) {
				parameters[i] = request.getSession();
			} else if (Util.isSameType(ServletContext.class, types[i])) {
				parameters[i] = request.getSession().getServletContext();
			} else if (Util.isSameType(Throwable.class, types[i])) {
				parameters[i] = exception; 
			} else if (Util.isSameType(ModelMap.class, types[i])) {
				parameters[i] = modelMap;
			} else {
				//先判断上传模式
				String key = paramNames[i];
				conver.reflectFormData(i, key, types[i], parameters, annotations, request, pathVariableMap, modelMap);//数据处理中心
			}
		}
		return parameters;
	}
	
	//创建路径命名集合,当且仅当AnnotationExceptionResolver执行时，methodPath参数为空
	private Map<String, String> createPathVariableMap(String methodPath, Method method,
			HttpServletRequest request) {
		Map<String, String> pathVariableMap = new ConcurrentHashMap<String, String>();
		if (Server.isEmpty(methodPath)) { 
			return pathVariableMap;
		}
		String path = request.getServletPath();
		path = trimSufixPath(path);
		String[] tempModelPathArr = methodPath.split("['{'][0-9a-zA-Z_-]*['}']");
		String modelPathStr = replacePath(methodPath, tempModelPathArr);
		String pathStr = replacePath(path, tempModelPathArr);
		String[] modelPathArr = modelPathStr.split(",");
		String[] pathArr = pathStr.split(",");
		for (int i = 0; i < pathArr.length; i++) {
			String key = modelPathArr[i].replaceAll("\\{|\\}", "");
			String value = pathArr[i];
			pathVariableMap.put(key, value);
		}
		return pathVariableMap;
	}
	
	
	private String trimSufixPath(String path) {
		return path.substring(0, path.contains(".") ? path
				.lastIndexOf(".") : path.length());
	}

	private String replacePath(String path, String[] tempPathArr) {
		for (String s : tempPathArr) {
			path = path.replaceFirst(s, ",");
		}
		return path;
	}


	/**
	 * 解析参数注解@RequestParam
	 */
	private String[] handlePramaterNamesForAnnotation(Class<?> prototype, Method method, Annotation[][] annotations) {
		String[] parameterNames = new ParameterNameDiscoverer().getParameterNames(prototype, method);
		for (int i = 0; i < annotations.length; i++) {
			Annotation[] ano = annotations[i];
			for (Annotation a : ano) {
				if (RequestParam.class.isAssignableFrom(a.annotationType())) {
					String value = ((RequestParam)a).value();
					if (!Server.isEmpty(value)) {
						parameterNames[i] = value;
					}
				}
			}
		}
		return parameterNames;
	}
	
	
	/**
	 * 执行所有标注@ModelAttribute的方法
	 * @param modelMap
	 * @param prototype
	 * @throws Exception 
	 */
	private void handleModelAttributeMethods(ModelMap modelMap, HttpServletRequest request,
			HttpServletResponse response, Class<?> prototype, String methodPath, Object controller, Conver conver,
			Throwable exception) throws Exception {
		Method[] methods = prototype.getMethods();
		for (Method m : methods) {
			if (m.isAnnotationPresent(ModelAttribute.class)) {
				if (!m.isAnnotationPresent(RequestMapping.class)) {
					String annoVal = m.getAnnotation(ModelAttribute.class).value();
					Object[] parameters = reflectParametersOfMethod(request, response,
							prototype, methodPath, m, modelMap, exception, conver); //映射数据到方法参数
					Object result = handleMethodResult(m, parameters, controller);
					String returnType = m.getReturnType().getName();
					if (result != null) {
						if (!Server.isEmpty(annoVal)) {
							modelMap.setAttribute(annoVal, result);
						} else {
							modelMap.setAttribute(getSign(returnType), result);
						}
					}
				}
			}
		}
	}
	
	private String getSign(String sign) {
		String signa = sign.substring(0, 1);
		String signb = sign.substring(1, sign.length());
		return signa.toLowerCase() + signb;
	}
}
