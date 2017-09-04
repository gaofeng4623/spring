package com.spring.mvc.controller.reflect;

import java.util.List;

import org.apache.commons.fileupload.FileItem;


public class ControllerContext {
	public static ThreadLocal<List<FileItem>> threadLocal = new ThreadLocal<List<FileItem>>();
	
	public static List<FileItem> getUploadRequestList() {
		return threadLocal.get();
	}
	
	public static void put(List<FileItem> fileList) {
		threadLocal.set(fileList);
	}
}
