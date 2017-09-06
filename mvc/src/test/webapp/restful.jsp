<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>


<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>My JSP 'index.jsp' starting page</title>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
  </head>
  
  <body>
  	<form action="/Spring/rest/process-instance/1094" method="get">
  		<input type="submit" value="查询"/>
  	</form>
  	<form action="/Spring/rest/process-instance/1094" method="post">
  		<input type="submit" value="插入"/>
  	</form>
  	<form action="/Spring/rest/process-instance/1094" method="post">
  		<input type="hidden" name="_method" value="put" />
  		<input type="submit" value="更新"/>
  	</form>
  	<form action="/Spring/rest/process-instance/1094" method="post">
  		<input type="hidden" name="_method" value="delete" />
  		<input type="submit" value="删除"/>
  	</form>
  </body>
</html>
