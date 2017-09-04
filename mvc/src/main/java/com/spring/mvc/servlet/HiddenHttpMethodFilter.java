package com.spring.mvc.servlet;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import com.spring.beans.common.utils.Server;

public class HiddenHttpMethodFilter extends AbstractHttpFilter{
	private final String methodParam = "_method"; 
	@Override
	public void doFilterInternal(HttpServletRequest request,
			HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
		String paramValue = request.getParameter(methodParam);
		if (Server.isEmpty(paramValue)) paramValue = request.getMethod();
		// 这里规定了客户端必须使用POST方式
        if ("POST".equals(request.getMethod())) {
            String method = paramValue.toUpperCase(Locale.ENGLISH);
           // 新建一个 HttpMethodRequestWrapper 实例
            HttpServletRequest wrapper = new HttpMethodRequestWrapper(request, method);
            chain.doFilter(wrapper, response);
        } else {
        	chain.doFilter(request, response);
        }
	}

	private static class HttpMethodRequestWrapper extends HttpServletRequestWrapper {

        private final String method;

        public HttpMethodRequestWrapper(HttpServletRequest request, String method) {
            super(request);
            this.method = method;
        }

       // 覆写了 getMethod 方法,后续我们调用 getMethod 方法获取到的将都是前端传来的那个假的请求方式(_method),真正的POST请求方式被隐藏掉了。 
        @Override
        public String getMethod() {
            return this.method;
        }
    }

}
