<%@ page contentType="text/html;charset=UTF-8" %>
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
        <label>
            <input type="email" name="email" required>
        </label>
    </div>

    <div>
        <label>Password</label>
        <label>
            <input type="password" name="password" required>
        </label>
    </div>

    <button type="submit">Login</button>
</form>
</body>
</html>
