<%--
  Created by IntelliJ IDEA.
  User: Admin
  Date: 5/14/2026
  Time: 10:24 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.User" %>
<%@ page import="java.util.List" %>

<html>
<head>
    <title>Users</title>
</head>
<body>
    <%
        List<User> list = (List<User>) request.getAttribute("users");
        User currentUser = (User) session.getAttribute("currentUser");
        int currentRoleId = currentUser != null ? currentUser.getRoleId() : 0;
    %>
    <table border="1">
        <thead>
        <tr>
            <td>id</td>
            <td>full_name</td>
            <td>email</td>

            <% if (currentRoleId == 1) {%>
            <td>password</td>
            <%}%>

            <td>phone</td>
            <td>gender</td>
            <td>date_of_birth</td>
            <td>address</td>
            <td>avatar_url</td>
            <td>role_id</td>
            <td>active</td>
            <td>actions</td>
        </tr>
        </thead>
        <tbody>
        <% if (list != null) {
            for (User user : list) { %>
        <tr>
            <td><%= user.getId()%></td>
            <td><%= user.getFullName()%></td>
            <td><%= user.getEmail()%></td>

            <% if (currentRoleId == 1) {%>
            <td><%= user.getPassword()%></td>
            <%}%>

            <td><%= user.getPhone()%></td>
            <td><%= user.getGender()%></td>
            <td><%= user.getDateOfBirth()%></td>
            <td><%=user.getAddress()%></td>
            <td><%=user.getAvatarUrl()%></td>
            <td><%=user.getRoleId()%></td>
            <td><%=user.isActive() ? "Active" : "Inactive"%></td>
            <td><a href="${pageContext.request.contextPath}/users/update?id=<%= user.getId()%>">update</a></td>
            <td><a href="${pageContext.request.contextPath}/users/toggle-status?id=<%= user.getId()%>&action=activate" onclick="return confirm('Activate this user?')">activate</a></td>
            <td><a href="${pageContext.request.contextPath}/users/toggle-status?id=<%= user.getId()%>&action=deactivate" onclick="return confirm('Deactivate this user?')">deactivate</a></td>
        </tr>
        <% } }%>
        </tbody>
    </table>

    <a href="${pageContext.request.contextPath}/home">Return to dashboard</a>
</body>
</html>
