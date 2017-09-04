package com.spring.mvc.servlet.view;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 通过视图定位到此处,并把数据转换为json格式输出
 *
 */
public interface XmlJsonView {
	public void converToJsonXml(Object data, HttpServletRequest request,
                                HttpServletResponse response);

}
