<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Login</title>
</head>
<body>
<h2>Login</h2>

<% if (request.getAttribute("error") != null) { %>
<p style="color:red;"><%= request.getAttribute("error") %></p>
<% } %>

<form action="<%= request.getContextPath() %>/login" method="post">
    <div>
        <label>Email</label>
        <input type="email" name="email" required>
    </div>

    <div>
        <label>Password</label>
        <input type="password" name="password" required>
    </div>

    <button type="submit">Login</button>
</form>

<br>
<a href="<%= request.getContextPath() %>/forgot-password">Forgot password?</a>
</body>
</html>
