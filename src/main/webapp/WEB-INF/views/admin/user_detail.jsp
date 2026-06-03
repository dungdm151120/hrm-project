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
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    <style>
        .detail-card {
            background: var(--bg-card);
            border-radius: var(--radius-lg);
            padding: 2rem;
            box-shadow: var(--shadow-card);
            border: 1px solid var(--border-color);
            display: flex;
            flex-direction: column;
            align-items: center;
            gap: 2rem;
        }
        .detail-avatar {
            width: 140px;
            height: 140px;
            border-radius: 50%;
            overflow: hidden;
            background: var(--bg-card-inner);
            border: 3px solid var(--border-color);
            box-shadow: 0 0 20px rgba(59,130,246,0.2);
        }
        .detail-avatar img {
            width: 100%;
            height: 100%;
            object-fit: cover;
        }
        .avatar-placeholder {
            width: 100%;
            height: 100%;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 4rem;
            font-weight: 700;
            color: var(--primary-light);
            background: var(--primary-bg);
        }
        .detail-info {
            width: 100%;
            max-width: 600px;
            display: flex;
            flex-direction: column;
        }
        .detail-row {
            display: flex;
            align-items: center;
            padding: 0.8rem 1rem;
            border-bottom: 1px solid var(--border-color);
        }
        .detail-row:last-child {
            border-bottom: none;
        }
        .detail-label {
            width: 160px;
            font-size: 0.85rem;
            font-weight: 600;
            text-transform: uppercase;
            letter-spacing: 0.04em;
            color: var(--text-muted);
            flex-shrink: 0;
        }
        .detail-value {
            flex: 1;
            font-size: 1rem;
            font-weight: 500;
            color: var(--text-primary);
        }
    </style>
</head>
<body class="dashboard-page">
<div class="dashboard-layout">
    <jsp:include page="/WEB-INF/views/common/sidebar.jsp" />

    <main class="main-content">
        <header class="main-header">
            <h1 class="header-title">User Detail</h1>
            <div class="header-right">
                <a href="${pageContext.request.contextPath}/user_list" class="btn btn-secondary">← Back to List</a>
            </div>
        </header>

        <div class="dashboard-content">
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
                            <span class="detail-value">
                                <%= user.getDateOfBirth() != null ? user.getDateOfBirth().toLocalDate() : "" %>
                            </span>
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
                            <span class="detail-value">
                                <strong>${not empty user.departmentName ? user.departmentName : 'N/A'}</strong>
                            </span>
                        </div>
                        <div class="detail-row">
                            <span class="detail-label">Role</span>
                            <span class="detail-value">
                                <strong>${user.roleName}</strong>
                            </span>
                        </div>
                    </div>
                </div>

                <div class="form-actions" style="margin-top: 2rem; justify-content: center;">
                    <a href="${pageContext.request.contextPath}/users/update?id=${user.id}" class="btn btn-primary">Update</a>
                    <a href="${pageContext.request.contextPath}/user_list" class="btn btn-cancel">Back to user list</a>
                </div>
            </c:if>
        </div>
    </main>
</div>
</body>
</html>