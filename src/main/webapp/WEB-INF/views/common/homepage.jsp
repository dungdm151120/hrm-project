<%@ page import="model.User" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    User currentUser = (User) session.getAttribute("currentUser");
%>
<html>
<head>
    <title>Home</title>
</head>
<body>
<h2>Welcome, <%= currentUser.getFullName() %></h2>
<p>Email: <%= currentUser.getEmail() %></p>
<p>Role: <%= currentUser.getRoleName() %></p>

<a href="<%= request.getContextPath() %>/logout">Logout</a>
</body>
</html>
