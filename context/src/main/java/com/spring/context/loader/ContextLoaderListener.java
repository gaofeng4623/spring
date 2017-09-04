package com.spring.context.loader;

import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import com.spring.beans.common.utils.Consts;

public class ContextLoaderListener implements ServletContextListener,
		HttpSessionListener, ServletRequestListener {
	private Map requestBean;
	private Map sessionBean;
	private ContextLoader contextLoader;

	/***** 容器关闭时销毁所有含有destroy-method配置的bean *******/
	public void contextDestroyed(ServletContextEvent sc) {
		contextLoader.getBeanFactory().destroySingletons();
	}

	public void contextInitialized(ServletContextEvent sc) {
		try {
			this.contextLoader = createContextLoader();
			this.contextLoader.initWebApplicationContext(sc.getServletContext());
			this.requestBean = this.contextLoader.getBeanFactory().getScopeBeans(
					Consts.requestScope);
			this.sessionBean = this.contextLoader.getBeanFactory().getScopeBeans(
					Consts.sessionScope);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected ContextLoader createContextLoader() {
		return new ContextLoader();
	}

	public void requestDestroyed(ServletRequestEvent se) {
	}

	public void requestInitialized(ServletRequestEvent se) {
		HttpServletRequest request = (HttpServletRequest) se.getServletRequest();
		if (this.requestBean != null) {
			for (Iterator it = requestBean.keySet().iterator(); it.hasNext();) {
				String key = it.next().toString();
				request.setAttribute(key, requestBean.get(key));
			}
		}
	}
	
	public void sessionCreated(HttpSessionEvent se) {
		HttpSession session = se.getSession();
		if (this.sessionBean != null) {
			for (Iterator it = sessionBean.keySet().iterator(); it.hasNext();) {
				String key = it.next().toString();
				session.setAttribute(key, sessionBean.get(key));
			}
		}
	}

	public void sessionDestroyed(HttpSessionEvent se) {

	}

}
