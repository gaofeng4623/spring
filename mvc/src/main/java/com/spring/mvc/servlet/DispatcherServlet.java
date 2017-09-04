package com.spring.mvc.servlet;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.spring.beans.common.exception.BeanNotFoundException;
import com.spring.beans.common.utils.Consts;
import com.spring.beans.common.utils.Server;
import com.spring.beans.factory.XmlBeanFactory;
import com.spring.beans.resource.ClassPathResource;
import com.spring.context.WebApplicationContext;
import com.spring.context.WebApplicationContextUtils;
import com.spring.context.loader.ContextLoader;
import com.spring.mvc.common.ModelAndView;
import com.spring.mvc.common.ParameterAndDefualtMethod;
import com.spring.mvc.common.ParameterMethodResolver;
import com.spring.mvc.controller.Controller;
import com.spring.mvc.controller.MultiActionController;
import com.spring.mvc.servlet.exresolver.AnnotationExceptionResolver;
import com.spring.mvc.servlet.exresolver.DefaultMappingExceptionResolver;
import com.spring.mvc.servlet.exresolver.HandlerExceptionResolver;
import com.spring.mvc.servlet.mappings.AnnotationHandleMapping;
import com.spring.mvc.servlet.mappings.HandleMapping;
import com.spring.mvc.servlet.view.ViewResolver;
import com.spring.mvc.servlet.view.XmlJsonView;

public class DispatcherServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	
	private WebApplicationContext applicationContext;
	
	/******
	 * 把本服务对应的mvc上下文保存在request中,如此在业务模型中通过
	 * request也可以访问到本mvc上下文bean资源,但其只是子上下文
	 * 当然也可以在业务模型中通过session访问全局上下文对象
	 *****/
	public void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		ViewResolver resolver = null;
		Controller controller = null;
		String controllerName = null;
		ModelAndView view = null;
		request.setAttribute(this.getClass().getName() + ".CONTEXT",applicationContext);
		String path = request.getServletPath();
		try {
			resolver = applicationContext.getBean(ViewResolver.class);
		} catch (Exception e) {
			throw new ServletException("the viewResolver bean is not define");
		}
		try {
			controllerName = getControllerName(applicationContext, path, request);
			if (controllerName != null) {
				controller = (Controller) applicationContext.getBean(controllerName);
			} else { // 注解工厂
				controller = (Controller) new AnnotationHandleMapping()
					.lookupHandler(path, request);
			}
			
			if (controller == null) return;
			if (controller instanceof MultiActionController) { // 判断是否是分发类实例
				try {
					ParameterMethodResolver paramMethod = applicationContext
						.getBean(ParameterMethodResolver.class);
					ParameterAndDefualtMethod.put(Consts.paramName, paramMethod.getParamName());
					ParameterAndDefualtMethod.put(Consts.defaultMethodName,
					paramMethod.getDefaultMethodName());
				} catch (BeanNotFoundException e) {
					throw new ServletException("the ParameterMethodResolver is not define");
				}
			}
			
			view = controller.handleRequest(request, response);
		} catch (Exception ex) {
			view = findViewForException(request, response, ex);
			handleException(ex);
		}
		
		pageViewHandle(view, resolver, request, response); //视图处理
		
	}

	
	
	private void handleException(Exception ex) {
		Throwable th = ex instanceof InvocationTargetException ? 
				((InvocationTargetException) ex).getTargetException() : ex;
		th.printStackTrace();
	}



	/**
	 * 处理controller返回视图
	 * @param view
	 * @param resolver
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void pageViewHandle(ModelAndView view, ViewResolver resolver, 
			HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		try {
			if (view == null) return; //没有返回视图的void方法
			view.fillRequestAttributes(request);
			String viewName = view.getPageview();
			Object viewConver = applicationContext.getBean(viewName);
			outPrintJsonXml(viewConver, view, request, response);
		} catch (Exception e) {
			String result = getResult(resolver, view);
			request.getRequestDispatcher(result).forward(request, response);
		}
	}
	
	
	/**
	 * 返回json或xml视图
	 * @param object
	 * @param view
	 * @param request
	 * @param response
	 */
	private void outPrintJsonXml(Object object, ModelAndView view, 
			HttpServletRequest request, HttpServletResponse response) {
		if (object instanceof XmlJsonView) {
			((XmlJsonView) object).converToJsonXml(view.getPojo(), request, response);
		}
	}

	/**
	 * mvc异常处理机制
	 * @param request
	 * @param response
	 * @param ex
	 * @return
	 */
	private ModelAndView findViewForException(HttpServletRequest request
			, HttpServletResponse response, Exception ex) {
		ModelAndView view = null;
		HandlerExceptionResolver handler = null;
		Throwable th = ex instanceof InvocationTargetException ? 
				((InvocationTargetException) ex).getTargetException() : ex;
		try {
			//可自定义异常处理
			handler = applicationContext.getBean(HandlerExceptionResolver.class);
			view = handler.resolveException(request, response, handler, th);
		} catch (Exception e) {
			List<AnnotationExceptionResolver> exceptionHandles =
				applicationContext.getExceptionHandles();
			for (AnnotationExceptionResolver an : exceptionHandles) {
				if (an.matchException(th)) {
					view = an.resolveException(request, response, handler, th);
					if (view != null) return view;
				}
			}
		} 
		
		if (view == null) {
			handler = new DefaultMappingExceptionResolver();
			view = handler.resolveException(request, response, handler, th);
		}
		return view;
	}
	

	private String getControllerName(WebApplicationContext applicationContext,
			String path, HttpServletRequest request) {
		List mappings = applicationContext.getHandleMappings();
		for (Iterator it = mappings.iterator(); it.hasNext();) {
			HandleMapping sum = (HandleMapping) it.next();
			String controlerName = (String) sum.lookupHandler(path, request);
			if (controlerName != null) return controlerName;
		}
		return null;
	}

	public void init() {
		String realPath = null;
		String[] xmlpaths = null;
		ServletContext sc = this.getServletContext();
		Server.setServletContext(sc);
		String path = this.getServletConfig().getInitParameter(
				Consts.contextConfigLocation);
		if (path != null) {
			if (path.indexOf(",") > 0) {
				String[] arr = path.split(",");
				xmlpaths = new String[arr.length];
				for (int i = 0; i < arr.length; i++) {
					String str = arr[i].replaceAll("\r\n", "");
					str = str.trim();
					realPath = sc.getRealPath(str);
					xmlpaths[i] = realPath;
				}
			} else {
				xmlpaths = new String[] { sc.getRealPath(path.trim()) };
			}
		} else {
			xmlpaths = new String[] { sc.getRealPath(Consts.XMLPATH_MVC) }; // 默认配置文件
		}
		try {
			WebApplicationContext context = WebApplicationContextUtils
					.getWebApplicationContext(this.getServletContext());
			XmlBeanFactory mvcFactory = new XmlBeanFactory(new ClassPathResource(
					xmlpaths));
			mvcFactory.setParent(context.getBeanFactory()); // 继承父上下文的资源
			ContextLoader loader = new ContextLoader(); // 上下文加载器
			loader.fillContextStack(xmlpaths, mvcFactory);
			WebApplicationContext mvcContext = loader.createWebApplicationContext(mvcFactory, sc);
			mvcContext.setBeanFactory(mvcFactory);
			if (!mvcFactory.iocScanable()) //开启非单例扫描,此功能不可放入setParent,不可重复装载
				mvcFactory.setIocScanable(context.getBeanFactory().iocScanable());
			this.applicationContext = mvcContext;
			/*******
			 * 当多个mvc的DispatcherServlet初始化时 将mvc的每个上下文保存在servlet上下文中
			 * 子上下文共享父上下文的advisor适配器和aspect资源,共享父的所有bean资源
			 * 但父上下文不能访问子上下文资源,前提是父上下文中没有引入mvc子上下文的配置文件
			 ********/
			sc.setAttribute(
					WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE
							+ this.getServletConfig().getServletName(),
							mvcContext);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	

	public String getResult(ViewResolver irvr, ModelAndView view) {
		if (irvr == null || view == null)
			return "/error.jsp";
		String prefix = irvr.getPrefix() == null ? "" : irvr.getPrefix();
		String suffix = irvr.getSuffix() == null ? "" : irvr.getSuffix();
		if (prefix.length() > 0 && !prefix.startsWith("/"))
			prefix = "/" + prefix;
		if (prefix.length() > 0 && !prefix.endsWith("/"))
			prefix += "/";
		if (suffix.length() > 0 && !suffix.startsWith("."))
			suffix = "." + suffix;
		return prefix + view.getPageview() + suffix;
	}
}
