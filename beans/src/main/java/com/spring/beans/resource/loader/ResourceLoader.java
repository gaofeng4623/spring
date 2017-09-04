package com.spring.beans.resource.loader;

import com.spring.beans.resource.Resource;


public interface ResourceLoader {
	public Resource getSource(String path);
	public Resource getSource();
}
