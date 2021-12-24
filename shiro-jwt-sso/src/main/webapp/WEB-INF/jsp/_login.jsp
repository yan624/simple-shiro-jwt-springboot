<%--
  Created by IntelliJ IDEA.
  User: 89753
  Date: 2021/12/15
  Time: 10:25
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>登录页面 | Login</title>
</head>
<body>
<h1>单点登录系统</h1>
<form action="login" method="post">
    <input type="hidden" name="loginUrl" value="${loginUrl}"/>
    <input type="hidden" name="backUrl" value="${backUrl}"/>
    <input type="hidden" name="aud" value="${aud}"/>
    Username: <input type="text" name="account"/> <br/>
    Password: <input type="password" name="password"/><br/><br/>
    <input type="submit" value="登录 | Login">
</form>
</body>
</html>
