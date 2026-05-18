<%@ page import="model.User" %>
<%@ page contentType="text/html;charset=UTF-8" %>

<%
    User currentUser = (User) request.getAttribute("currentUser");
    String roleName = currentUser.getRoleName();
%>

<!DOCTYPE html>
<html>
<head>
    <title>Dashboard</title>
</head>
<body>

<jsp:include page="/WEB-INF/views/common/navbar.jsp" />

<div>
    <% if ("EMPLOYEE".equalsIgnoreCase(roleName)) { %>

        <h2>Employee Dashboard</h2>

        <table>
            <tr>
                <td>[ <a href="${pageContext.request.contextPath}/profile">View My Profile</a> ]</td>
                <td>[ <a href="${pageContext.request.contextPath}/change-password">Change Password</a> ]</td>
            </tr>
        </table>

    <% } else if ("MANAGER".equalsIgnoreCase(roleName) || "HR".equalsIgnoreCase(roleName)) { %>

        <h2>Manager Dashboard</h2>

        <h3>Personal</h3>
        <table>
            <tr>
                <td>[ <a href="${pageContext.request.contextPath}/profile">View My Profile</a> ]</td>
                <td>[ <a href="${pageContext.request.contextPath}/change-password">Change Password</a> ]</td>
            </tr>
        </table>

        <h3>User Management</h3>
        <table>
            <tr>
                <td>[ <a href="${pageContext.request.contextPath}/user_list">View User List</a> ]</td>
                <td>[ <a href="${pageContext.request.contextPath}/admin/users/add">Add New User</a> ]</td>
            </tr>
            <tr>
                <td>[ <a href="${pageContext.request.contextPath}/user_list">Update User Info</a> ]</td>
                <td>[ <a href="${pageContext.request.contextPath}/user_list">Active/Deactive User</a> ]</td>
            </tr>
        </table>

    <% } else if ("ADMIN".equalsIgnoreCase(roleName)) { %>

        <h2>Admin Dashboard</h2>

        <h3>Personal</h3>
        <table>
            <tr>
                <td>[ <a href="${pageContext.request.contextPath}/profile">View My Profile</a> ]</td>
                <td>[ <a href="${pageContext.request.contextPath}/change-password">Change Password</a> ]</td>
            </tr>
        </table>

        <h3>User Management</h3>
        <table>
            <tr>
                <td>[ <a href="${pageContext.request.contextPath}/user_list">View User List</a> ]</td>
                <td>[ <a href="${pageContext.request.contextPath}/admin/users/add">Add New User</a> ]</td>
            </tr>
            <tr>
                <td>[ <a href="${pageContext.request.contextPath}/user_list">Update User Info</a> ]</td>
                <td>[ <a href="${pageContext.request.contextPath}/user_list">Active/Deactive User</a> ]</td>
            </tr>
        </table>

        <h3>Role Management</h3>
        <table>
            <tr>
                <td>[ <a href="${pageContext.request.contextPath}/admin/roles">View Role List</a> ]</td>
                <td>[ <a href="${pageContext.request.contextPath}/admin/roles/permissions?roleId=1">View Role Permissions</a> ]</td>
            </tr>
            <tr>
                <td>[ <a href="${pageContext.request.contextPath}/admin/roles/edit_permissions?roleId=1">Update Role Info</a> ]</td>
                <td>[ <a href="${pageContext.request.contextPath}/admin/roles">Active/Deactive Role</a> ]</td>
            </tr>
            <tr>
                <td>[ <a href="${pageContext.request.contextPath}/admin/roles/edit_permissions?roleId=1">Edit Role Permissions</a> ]</td>
            </tr>
        </table>

<% } else { %>

    <p>Role không hợp lệ.</p>

    <% } %>
</div>

</body>
</html>
