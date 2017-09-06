<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ page import="com.spring.context.WebApplicationContextUtils" %>
<%@ page import="com.spring.context.WebApplicationContext" %>
<%@ page import="test.*" %>

<%
	WebApplicationContext context = WebApplicationContextUtils
				.getWebApplicationContext(application);
	Dao dao1 = (Dao) context.getBean("nametest"); 
	out.println("dao1 " +dao1 + "<br>");
	Dao dao2 = (Dao) context.getBean("nametest");
	out.println("dao2 " +dao2 + "<br>");
	Engine engine1 = (Engine) context.getBean("engine");
	out.println("engine1 " + engine1 + "<br>");
	Engine engine2 = (Engine) context.getBean("engine"); 
	out.println("engine2 " + engine2 + "<br>");
	/******测试域*******/
	RequestTest rt = (RequestTest) request.getAttribute("requestTest");
	rt.show();
	SessionTest st = (SessionTest) session.getAttribute("sessionTest");
	st.show();
	/*******测试数据的单一性*********/
	out.println(context.getBean("requestTest") + " --PK-- " + rt + "<br>");
	out.println(context.getBean("sessionTest") + " --PK-- " + st + "<br>");
%>

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
    This is my JSP page. <br>
  </body>
</html>
