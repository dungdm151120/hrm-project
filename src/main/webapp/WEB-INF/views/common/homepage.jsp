<%@ page import="model.User" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<%
    User currentUser = (User) request.getAttribute("currentUser");
    String roleName = currentUser.getRoleName();
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dashboard | HRM</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body>

<jsp:include page="/WEB-INF/views/common/navbar.jsp" />

<div class="dashboard-container">

    <% if ("EMPLOYEE".equalsIgnoreCase(roleName)) { %>

        <h2 class="dashboard-title">Employee Dashboard</h2>

        <div class="dashboard-grid">
            <div class="dashboard-card">
                <h3>👤 Personal</h3>
                <ul>
                    <li><a href="${pageContext.request.contextPath}/profile">View My Profile</a></li>
                    <li><a href="${pageContext.request.contextPath}/change-password">Change Password</a></li>
                </ul>
            </div>
        </div>

    <% } else if ("MANAGER".equalsIgnoreCase(roleName) || "HR".equalsIgnoreCase(roleName)) { %>

        <h2 class="dashboard-title">Manager Dashboard</h2>

        <div class="dashboard-grid">
            <div class="dashboard-card">
                <h3>👤 Personal</h3>
                <ul>
                    <li><a href="${pageContext.request.contextPath}/profile">View My Profile</a></li>
                    <li><a href="${pageContext.request.contextPath}/change-password">Change Password</a></li>
                </ul>
            </div>

            <div class="dashboard-card">
                <h3>👥 User Management</h3>
                <ul>
                    <li><a href="${pageContext.request.contextPath}/user_list">View User List</a></li>
                    <li><a href="${pageContext.request.contextPath}/admin/users/add">Add New User</a></li>
                    <li><a href="${pageContext.request.contextPath}/user_list">Update User Info</a></li>
                    <li><a href="${pageContext.request.contextPath}/user_list">Active/Deactive User</a></li>
                </ul>
            </div>
        </div>

    <% } else if ("ADMIN".equalsIgnoreCase(roleName)) { %>

        <h2 class="dashboard-title">Admin Dashboard</h2>

        <div class="dashboard-grid">
            <div class="dashboard-card">
                <h3>👤 Personal</h3>
                <ul>
                    <li><a href="${pageContext.request.contextPath}/profile">View My Profile</a></li>
                    <li><a href="${pageContext.request.contextPath}/change-password">Change Password</a></li>
                </ul>
            </div>

            <div class="dashboard-card">
                <h3>👥 User Management</h3>
                <ul>
                    <li><a href="${pageContext.request.contextPath}/user_list">View User List</a></li>
                    <li><a href="${pageContext.request.contextPath}/admin/users/add">Add New User</a></li>
                    <li><a href="${pageContext.request.contextPath}/user_list">Update User Info</a></li>
                    <li><a href="${pageContext.request.contextPath}/user_list">Active/Deactive User</a></li>
                    <li><a href="${pageContext.request.contextPath}/admin/password-reset-requests">Request Reset Password</a></li>
                </ul>
            </div>

            <div class="dashboard-card">
                <h3>🛡️ Role Management</h3>
                <ul>
                    <li><a href="${pageContext.request.contextPath}/admin/roles">View Role List</a></li>
                    <li><a href="${pageContext.request.contextPath}/admin/roles/permissions?roleId=1">View Role Permissions</a></li>
                    <li><a href="${pageContext.request.contextPath}/admin/roles/update?roleId=1">Update Role Info</a></li>
                    <li><a href="${pageContext.request.contextPath}/admin/roles">Active/Deactive Role</a></li>
                    <li><a href="${pageContext.request.contextPath}/admin/roles/edit_permissions?roleId=1">Edit Role Permissions</a></li>
                </ul>
            </div>
        </div>

    <% } else { %>

        <div class="role-invalid">
            <p>⚠ Role không hợp lệ hoặc chưa được hỗ trợ.</p>
        </div>

    <% } %>

</div>

</body>
</html>