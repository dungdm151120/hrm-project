<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Forgot Password</title>
</head>
<body>
<h2>Forgot Password</h2>

<% if (request.getAttribute("error") != null) { %>
    <p style="color:red;"><%= request.getAttribute("error") %></p>
<% } %>

<% if (request.getAttribute("success") != null) { %>
    <p style="color:green;"><%= request.getAttribute("success") %></p>
<% } %>

<form action="<%= request.getContextPath() %>/forgot-password" method="post">
    <div>
        <label>Email</label>
        <input type="email" name="email" required>
    </div>

    <div>
        <label>Reason</label>
        <textarea name="reason" rows="4" cols="40"></textarea>
    </div>

    <button type="submit">Send request to admin</button>
</form>

<br>
<a href="<%= request.getContextPath() %>/login">Back to login</a>
</body>
</html>
