package com.spring.mvc.servlet.mappings;

import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import com.spring.beans.common.utils.Server;
import com.spring.context.WebApplicationContext;
import com.spring.mvc.common.RequestContextUtils;
import com.spring.mvc.controller.AnnotationController;
import com.spring.mvc.controller.Controller;

public class AnnotationHandleMapping implements HandleMapping{

	/* (non-Javadoc)
	 * @Controller检索器
	 */
	@Override
	public Controller lookupHandler(String path, HttpServletRequest request) {
		WebApplicationContext mvcContext = 
			RequestContextUtils.getWebApplicationContext(request);
		String mvcName = path.substring(0, path.contains(".") ? path
				.lastIndexOf(".") : path.length());
		Map<String, AnnotationController> controllers = (Map)mvcContext.getControllers();
		AnnotationController annoControl = matchController(mvcName, controllers, request);
		if (annoControl == null) return null;
		try {
			//此处载入真正的controller对象,可以是单例也可以是原型或者AOP代理
			annoControl.loadingController(request);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return annoControl;
	}
	

	/**
	 * 利用正则表达式匹配Controller
	 * @param mvcName
	 * @param controllers
	 * @return
	 */
	private AnnotationController matchController(String mvcName,
			Map<String, AnnotationController> controllers, HttpServletRequest request) {
		String method = request.getMethod(); //获得请求方式(request代理)
		for (Iterator<String> it = controllers.keySet().iterator(); it.hasNext();) {
			String key = it.next();
			String methodPath = key.substring(0, key.lastIndexOf("_"));
			AnnotationController act = controllers.get(key);
			String regx = methodPath.replaceAll("['{'][0-9a-zA-Z_-]*['}']", "((?!/).)*");
			if (Pattern.matches(regx, mvcName)) {
				if (!Server.isEmpty(act.getRequestMethod())) {
					if (method.equalsIgnoreCase(act.getRequestMethod())) {
						return controllers.get(key);
					}
				} else {
					return controllers.get(key);
				}
			}
		}
		return null;
	}

}
