package com.spring.mvc.servlet.mappings;

import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import com.spring.context.WebApplicationContext;
import com.spring.mvc.common.RequestContextUtils;

public class BeanNameUrlHandlerMapping implements HandleMapping{


	public Object lookupHandler(String path, HttpServletRequest request) {
		WebApplicationContext mvcContext = 
			RequestContextUtils.getWebApplicationContext(request);
		List idlist = mvcContext.getBeanIdList();
		if (idlist == null) return null;
		for (Iterator it = idlist.iterator(); it.hasNext();) {
			String key = (String) it.next();
			if (key.contains("*")) {
				String regx = key.replace("*", ".*");
				if (Pattern.matches(regx, path)) return key;
			} else {
				if (key.equals(path)) return key;
			}
		}
		return null;
	}

}
