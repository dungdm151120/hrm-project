<%@ page import="model.User" %>
<%@ page contentType="text/html;charset=UTF-8" %>

<%
    User currentUser = (User) request.getAttribute("currentUser");
    String roleName = currentUser.getRoleName();
%>

<!DOCTYPE html>
<html>
<head>
    <title>Homepage</title>
</head>
<body>

<div>
    <div>HRM System</div>

    <div>
        <span>Hello, <%= currentUser.getFullName() %> - <%= roleName %></span>
        <a href="${pageContext.request.contextPath}/profile">View My Profile</a>
        <a href="${pageContext.request.contextPath}/logout">Logout</a>

    </div>
</div>

<div>
    <h1>Homepage</h1>

    <% if ("EMPLOYEE".equalsIgnoreCase(roleName)) { %>

    <h2>Employee Dashboard</h2>

    <div>
        <a href="${pageContext.request.contextPath}/profile">
            <h3>View My Profile</h3>

        </a>

        <a href="${pageContext.request.contextPath}/change-password">
            <h3>Change Password</h3>

        </a>
    </div>

    <% } else if ("MANAGER".equalsIgnoreCase(roleName) || "HR".equalsIgnoreCase(roleName)) { %>

    <h2>Manager Dashboard</h2>



        <a href="${pageContext.request.contextPath}/admin/users">
            <h3>View User List</h3>

        </a>

        <a href="${pageContext.request.contextPath}/admin/users/add">
            <h3>Add New User</h3>

        </a>

        <a href="${pageContext.request.contextPath}/admin/users">
            <h3>Update User Information</h3>

        </a>

        <a href="${pageContext.request.contextPath}/admin/users">
            <h3>Active/Deactive User</h3>

        </a>
    </div>

    <% } else if ("ADMIN".equalsIgnoreCase(roleName)) { %>

    <h2>Admin Dashboard</h2>


        <a href="${pageContext.request.contextPath}/admin/users">
            <h3>View User List</h3>

        </a>

        <a href="${pageContext.request.contextPath}/admin/users/add">
            <h3>Add New User</h3>

        </a>

        <a href="${pageContext.request.contextPath}/admin/roles">
            <h3>View Role List</h3>

        </a>

        <a href="${pageContext.request.contextPath}/admin/roles/permissions">
            <h3>View Role Permissions</h3>

        </a>

        <a href="${pageContext.request.contextPath}/admin/roles/update">
            <h3>Update Role Information</h3>

        </a>

        <a href="${pageContext.request.contextPath}/admin/roles">
            <h3>Active/Deactive Role</h3>

        </a>

        <a href="${pageContext.request.contextPath}/admin/roles/permissions/edit">
            <h3>Edit Role Permissions</h3>
        </a>

<% } else { %>

    <p>Role không hợp lệ.</p>

    <% } %>
</div>

</body>
</html>