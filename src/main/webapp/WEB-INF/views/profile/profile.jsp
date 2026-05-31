<%@ page import="model.User" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
    User user = (User) request.getAttribute("user");
    String dob = "";
    if (user != null && user.getDateOfBirth() != null) {
        dob = user.getDateOfBirth().toLocalDate().toString();
    }
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

    <c:if test="${empty user}">
        <div class="empty-state">Profile not found.</div>
    </c:if>

    <c:if test="${not empty user}">
        <div class="detail-card">
            <div class="detail-avatar">
                <c:choose>
                    <c:when test="${not empty user.avatarUrl}">
                        <img src="${user.avatarUrl}" alt="Avatar of ${user.fullName}" style="width: 120px; height: 120px;">
                    </c:when>
                    <c:otherwise>
                        <img src="${pageContext.request.contextPath}/assets/images/default_avatar.jpg" alt="Default Avatar" style="width: 120px; height: 120px;">
                    </c:otherwise>
                </c:choose>
            </div>

            <div class="detail-info">
                <div class="detail-row">
                    <span class="detail-label">Full Name:</span>
                    <span class="detail-value">${user.fullName}</span>
                </div>
                <div class="detail-row">
                    <span class="detail-label">Email:</span>
                    <span class="detail-value">${user.email}</span>
                </div>
                <div class="detail-row">
                    <span class="detail-label">Phone:</span>
                    <span class="detail-value">
                        <c:choose>
                            <c:when test="${not empty user.phone}">${user.phone}</c:when>
                            <c:otherwise><span class="text-muted">Not updated</span></c:otherwise>
                        </c:choose>
                    </span>
                </div>
                <div class="detail-row">
                    <span class="detail-label">Gender:</span>
                    <span class="detail-value">
                        <c:choose>
                            <c:when test="${not empty user.gender}">${user.gender}</c:when>
                            <c:otherwise><span class="text-muted">Not updated</span></c:otherwise>
                        </c:choose>
                    </span>
                </div>
                <div class="detail-row">
                    <span class="detail-label">Date of Birth:</span>
                    <span class="detail-value">
                        <% if (!dob.isEmpty()) { %>
                            <%= dob %>
                        <% } else { %>
                            <span class="text-muted">Not updated</span>
                        <% } %>
                    </span>
                </div>
                <div class="detail-row">
                    <span class="detail-label">Address:</span>
                    <span class="detail-value">
                        <c:choose>
                            <c:when test="${not empty user.address}">${user.address}</c:when>
                            <c:otherwise><span class="text-muted">Not updated</span></c:otherwise>
                        </c:choose>
                    </span>
                </div>
                <div class="detail-row">
                    <span class="detail-label">Department:</span>
                    <span class="detail-value">
                        <c:choose>
                            <c:when test="${not empty user.departmentName}">${user.departmentName}</c:when>
                            <c:otherwise><span class="text-muted">No department</span></c:otherwise>
                        </c:choose>
                    </span>
                </div>
                <div class="detail-row">
                    <span class="detail-label">Position:</span>
                    <span class="detail-value">
                        <c:choose>
                            <c:when test="${not empty user.positionName}">${user.positionName}</c:when>
                            <c:otherwise><span class="text-muted">No position</span></c:otherwise>
                        </c:choose>
                    </span>
                </div>
            </div>
        </div>

        <div class="form-actions" style="margin-top: 2rem;">
            <a href="${pageContext.request.contextPath}/change-password" class="btn-save">Change Password</a>
        </div>
    </c:if>
</div>

</body>
</html>
