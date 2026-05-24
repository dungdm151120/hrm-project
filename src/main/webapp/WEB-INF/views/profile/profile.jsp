<%@ page import="model.User" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    User user = (User) request.getAttribute("user");
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Profile | HRM</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body>

<jsp:include page="/WEB-INF/views/common/navbar.jsp" />

<div class="container" style="margin-top: 2rem;">
    <div class="page-header">
        <h2>My Profile</h2>
    </div>

    <div class="detail-card">
        <div class="detail-avatar">
            <c:choose>
                <c:when test="${not empty user.avatarUrl}">
                    <img src="${user.avatarUrl}" alt="Avatar of ${user.fullName}">
                </c:when>
                <c:otherwise>
                    <div class="avatar-placeholder">
                        <%= user.getFullName().substring(0,1) %>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
        <div class="detail-info">
            <div class="detail-row">
                <span class="detail-label">Full Name</span>
                <span class="detail-value"><%= user.getFullName() %></span>
            </div>
            <div class="detail-row">
                <span class="detail-label">Email</span>
                <span class="detail-value"><%= user.getEmail() %></span>
            </div>
            <div class="detail-row">
                <span class="detail-label">Phone</span>
                <span class="detail-value"><%= user.getPhone() %></span>
            </div>
            <div class="detail-row">
                <span class="detail-label">Gender</span>
                <span class="detail-value"><%= user.getGender() %></span>
            </div>
            <div class="detail-row">
                <span class="detail-label">Date of Birth</span>
                <span class="detail-value"><%= user.getDateOfBirth() != null ? user.getDateOfBirth().toLocalDate() : "" %></span>
            </div>
            <div class="detail-row">
                <span class="detail-label">Address</span>
                <span class="detail-value"><%= user.getAddress() %></span>
            </div>
            <div class="detail-row">
                <span class="detail-label">Role</span>
                <span class="detail-value"><strong><%= user.getRoleName() %></strong></span>
            </div>
            <div class="detail-row">
                <span class="detail-label">Status</span>
                <span class="detail-value">
                    <% if (user.isActive()) { %>
                        <span class="badge badge-active">Active</span>
                    <% } else { %>
                        <span class="badge badge-inactive">Inactive</span>
                    <% } %>
                </span>
            </div>
        </div>
    </div>

    <div class="form-actions" style="margin-top: 2rem;">
        <a href="${pageContext.request.contextPath}/change-password" class="btn-save">Change Password</a>
    </div>
</div>

</body>
</html>