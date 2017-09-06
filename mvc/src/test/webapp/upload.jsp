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
	<script src="js/add.js"></script>
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
  </head>
	
  <body>
    <form action="user/test.do" method="post" enctype="multipart/form-data">  
    <input type="hidden" name="action" value="add"> 
    <input type="hidden" name="room" value="school">
    <table width="15%" cellpadding=0 cellspacing=0 border=0 id="att">
    <input type="hidden" name="annoName" value="注解参数" />
    <tr><td>姓名：<input type="text" name="name"></td></tr>
    <tr><td>密码：<input type="text" name="passWord"></td></tr>
    <tr><td>年龄：<input type="text" name="age"></td></tr>
    <tr><td>分数：<input type="text" name="score"></td></tr>
    <tr><td>生日：<input type="text" name="birthday"></td></tr>
    <tr><td>爱好：<input type="checkbox" name="loves" value="swim">游泳
    <input type="checkbox" name="person.loves" value="music">弹琴</td></tr>
    <tr><td><input name="attach" id="attach" type="file"></td></tr>
    <tr><td><input name="person.user.atts" id="atts" type="file"></td></tr>
   
    </table>
    <div style="width:17%" align="right" onclick="addRowToTable()">+</div>
    <input type="submit" value="提交">
    
    </form>
    
  </body>
</html>
