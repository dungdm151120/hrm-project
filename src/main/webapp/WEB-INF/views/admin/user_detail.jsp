<%@ page import="model.User" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
    User user = (User) request.getAttribute("user");
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>User Detail - ${user.fullName} | HRM</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body class="dashboard-body">

<div class="dashboard-wrapper">

    <jsp:include page="/WEB-INF/views/common/sidebar.jsp"/>

    <div class="dashboard-main">
        <div class="dashboard-header">
            <div class="header-left">
                <h1 class="header-title">User Detail</h1>
            </div>
        </div>

        <div class="dashboard-content">
            <c:if test="${empty user}">
                <div class="empty-state">User not found.</div>
            </c:if>

            <c:if test="${not empty user}">
                <div class="detail-card">
                    <!-- Avatar tròn, căn giữa -->
                    <div class="detail-avatar-wrapper">
                        <c:choose>
                            <c:when test="${not empty user.avatarUrl}">
                                <img src="${user.avatarUrl}" alt="Avatar of ${user.fullName}" class="avatar-circle">
                            </c:when>
                            <c:otherwise>
                                <div class="avatar-placeholder-circle">
                                    ${user.fullName.substring(0,1)}
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>

                    <!-- Thông tin bên dưới -->
                    <div class="detail-info">
                        <div class="detail-row">
                            <span class="detail-label">ID</span>
                            <span class="detail-value">${user.id}</span>
                        </div>
                        <div class="detail-row">
                            <span class="detail-label">Full Name</span>
                            <span class="detail-value">${user.fullName}</span>
                        </div>
                        <div class="detail-row">
                            <span class="detail-label">Email</span>
                            <span class="detail-value">${user.email}</span>
                        </div>
                        <div class="detail-row">
                            <span class="detail-label">Phone</span>
                            <span class="detail-value">${user.phone}</span>
                        </div>
                        <div class="detail-row">
                            <span class="detail-label">Address</span>
                            <span class="detail-value">${user.address}</span>
                        </div>
                        <div class="detail-row">
                            <span class="detail-label">Gender</span>
                            <span class="detail-value">${user.gender}</span>
                        </div>
                        <div class="detail-row">
                            <span class="detail-label">Date of Birth</span>
                            <span class="detail-value"><%= user.getDateOfBirth() != null ? user.getDateOfBirth().toLocalDate() : "" %></span>
                        </div>
                        <div class="detail-row">
                            <span class="detail-label">Status</span>
                            <span class="detail-value">
                                <c:choose>
                                    <c:when test="${user.active}">
                                        <span class="badge badge-active">Active</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="badge badge-inactive">Inactive</span>
                                    </c:otherwise>
                                </c:choose>
                            </span>
                        </div>
                        <div class="detail-row">
                            <span class="detail-label">Department</span>
                            <span class="detail-value"><strong>${not empty user.departmentName ? user.departmentName : 'N/A'}</strong></span>
                        </div>
                        <div class="detail-row">
                            <span class="detail-label">Position</span>
                            <span class="detail-value"><strong>${not empty user.positionName ? user.positionName : 'N/A'}</strong></span>
                        </div>
                        <div class="detail-row">
                            <span class="detail-label">Role</span>
                            <span class="detail-value"><strong>${user.roleName}</strong></span>
                        </div>
                    </div>
                </div>

                <div class="form-actions">
                    <a href="${pageContext.request.contextPath}/users/update?id=${user.id}" class="btn-save">Update</a>
                    <a href="user_list" class="btn-cancel">Back to user list</a>
                </div>
            </c:if>
        </div>
    </div>
</div>

</body>
</html>