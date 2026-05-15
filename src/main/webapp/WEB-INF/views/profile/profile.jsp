<%@ page import="model.User" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    User user = (User) request.getAttribute("user");
%>

<!DOCTYPE html>
<html>
<head>
    <title>My Profile</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body>

<div class="navbar">
    <div class="logo">HRM System</div>

    <div class="nav-right">
        <a href="${pageContext.request.contextPath}/home">Home</a>
        <a href="${pageContext.request.contextPath}/change-password">Change Password</a>
        <a href="${pageContext.request.contextPath}/logout">Logout</a>
    </div>
</div>

<div class="page-container">
    <h1>My Profile</h1>

    <table class="profile-table">
        <tr>
            <th>Full Name</th>
            <td><%= user.getFullName() %></td>
        </tr>

        <tr>
            <th>Email</th>
            <td><%= user.getEmail() %></td>
        </tr>

        <tr>
            <th>Phone</th>
            <td><%= user.getPhone() %></td>
        </tr>

        <tr>
            <th>Gender</th>
            <td><%= user.getGender() %></td>
        </tr>

        <tr>
            <th>Date of Birth</th>
            <td><%= user.getDateOfBirth() %></td>
        </tr>

        <tr>
            <th>Address</th>
            <td><%= user.getAddress() %></td>
        </tr>

        <tr>
            <th>Role</th>
            <td><%= user.getRoleName() %></td>
        </tr>

        <tr>
            <th>Status</th>
            <td><%= user.isActive() ? "Active" : "Inactive" %></td>
        </tr>
    </table>
</div>

</body>
</html>