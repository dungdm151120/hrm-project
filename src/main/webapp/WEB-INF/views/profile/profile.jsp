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
            flex-shrink: 0;
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
        .text-muted {
            color: var(--text-muted);
            font-style: italic;
        }
    </style>
</head>
<body class="dashboard-page">
<div class="dashboard-layout">
    <jsp:include page="/WEB-INF/views/common/sidebar.jsp" />

    <main class="main-content">
        <header class="main-header">
            <h1 class="header-title">My Profile</h1>
        </header>

        <div class="dashboard-content">
            <c:if test="${empty user}">
                <div class="empty-state">Profile not found.</div>
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
                            <span class="detail-label">Full Name</span>
                            <span class="detail-value">${user.fullName}</span>
                        </div>
                        <div class="detail-row">
                            <span class="detail-label">Email</span>
                            <span class="detail-value">${user.email}</span>
                        </div>
                        <div class="detail-row">
                            <span class="detail-label">Phone</span>
                            <span class="detail-value">
                                <c:choose>
                                    <c:when test="${not empty user.phone}">${user.phone}</c:when>
                                    <c:otherwise><span class="text-muted">Not updated</span></c:otherwise>
                                </c:choose>
                            </span>
                        </div>
                        <div class="detail-row">
                            <span class="detail-label">Gender</span>
                            <span class="detail-value">
                                <c:choose>
                                    <c:when test="${not empty user.gender}">${user.gender}</c:when>
                                    <c:otherwise><span class="text-muted">Not updated</span></c:otherwise>
                                </c:choose>
                            </span>
                        </div>
                        <div class="detail-row">
                            <span class="detail-label">Date of Birth</span>
                            <span class="detail-value">
                                <% if (!dob.isEmpty()) { %>
                                    <%= dob %>
                                <% } else { %>
                                    <span class="text-muted">Not updated</span>
                                <% } %>
                            </span>
                        </div>
                        <div class="detail-row">
                            <span class="detail-label">Address</span>
                            <span class="detail-value">
                                <c:choose>
                                    <c:when test="${not empty user.address}">${user.address}</c:when>
                                    <c:otherwise><span class="text-muted">Not updated</span></c:otherwise>
                                </c:choose>
                            </span>
                        </div>
                        <div class="detail-row">
                            <span class="detail-label">Department</span>
                            <span class="detail-value">
                                <c:choose>
                                    <c:when test="${not empty user.departmentName}">${user.departmentName}</c:when>
                                    <c:otherwise><span class="text-muted">No department</span></c:otherwise>
                                </c:choose>
                            </span>
                        </div>
                        <div class="detail-row">
                            <span class="detail-label">Position</span>
                            <span class="detail-value">
                                <c:choose>
                                    <c:when test="${not empty user.positionName}">${user.positionName}</c:when>
                                    <c:otherwise><span class="text-muted">No position</span></c:otherwise>
                                </c:choose>
                            </span>
                        </div>
                    </div>
                </div>

                <div class="form-actions" style="margin-top: 2rem; justify-content: center;">
                    <a href="${pageContext.request.contextPath}/change-password" class="btn btn-primary">Change Password</a>
                </div>
            </c:if>
        </div>
    </main>
</div>
</body>
</html>