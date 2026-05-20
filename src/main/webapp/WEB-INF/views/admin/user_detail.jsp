<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>User Detail - ${user.fullName} | HRM</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body>

<jsp:include page="/WEB-INF/views/common/navbar.jsp" />

<div class="container" style="margin-top: 2rem;">
    <div class="page-header">
        <h2>User Detail</h2>
    </div>

    <c:if test="${empty user}">
        <div class="empty-state">User not found.</div>
    </c:if>

    <c:if test="${not empty user}">
        <div class="detail-card">
            <div class="detail-avatar">
                <c:choose>
                    <c:when test="${not empty user.avatarUrl}">
                        <img src="${user.avatarUrl}" alt="Avatar of ${user.fullName}">
                    </c:when>
                    <c:otherwise>
                        <div class="avatar-placeholder">
                            ${user.fullName.substring(0,1)}
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
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
                    <span class="detail-value">${user.dateOfBirth}</span>
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
                    <span class="detail-label">Role</span>
                    <span class="detail-value"><strong>${user.roleName}</strong></span>
                </div>
            </div>
        </div>

        <div class="form-actions" style="margin-top: 2rem;">
            <a href="${pageContext.request.contextPath}/users/update?id=${user.id}" class="btn-save">Update</a>
            <a href="user_list" class="btn-cancel">Back to user list</a>
        </div>
    </c:if>
</div>

</body>
</html>