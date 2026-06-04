<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dashboard | HRM</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body class="dashboard-body">

<div class="dashboard-wrapper">
    <jsp:include page="/WEB-INF/views/common/sidebar.jsp" />

    <main class="dashboard-main">
        <header class="dashboard-header">
            <div class="header-left">
                <h1 class="header-title">Dashboard</h1>
            </div>
            <div class="header-right">

                <button class="header-icon">
                    <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9"></path>
                        <path d="M13.73 21a2 2 0 0 1-3.46 0"></path>
                    </svg>
                </button>
                <div class="header-profile" id="profileDropdownToggle">
                    <c:choose>
                        <c:when test="${not empty currentUser.avatarUrl}">
                            <img src="${currentUser.avatarUrl}" alt="User" class="profile-avatar">
                        </c:when>
                        <c:otherwise>
                            <div class="avatar-placeholder-small">${currentUser.fullName.substring(0,1)}</div>
                        </c:otherwise>
                    </c:choose>
                    <div class="profile-info">
                        <p class="profile-name">${currentUser.fullName}</p>
                        <p class="profile-status">Online</p>
                    </div>
                    <div class="dropdown-menu" id="profileDropdown">
                        <a href="${pageContext.request.contextPath}/profile" class="dropdown-item">View My Profile</a>
                        <a href="${pageContext.request.contextPath}/change-password" class="dropdown-item">Change Password</a>
                        <a href="${pageContext.request.contextPath}/logout" class="dropdown-item">Logout</a>
                    </div>
                </div>
            </div>
        </header>

        <div class="dashboard-content">
            <div class="stats-grid">
                <!-- Active Employees / Total Employees -->
                <div class="stat-card">
                    <div class="stat-icon stat-icon-1">
                        <svg viewBox="0 0 24 24" fill="currentColor">
                            <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm0 18c-4.41 0-8-3.59-8-8s3.59-8 8-8 8 3.59 8 8-3.59 8-8 8zm0-13c-2.76 0-5 2.24-5 5s2.24 5 5 5 5-2.24 5-5-2.24-5-5-5z"/>
                        </svg>
                    </div>
                    <div class="stat-body">
                        <p class="stat-value">${activeUsers}</p>
                        <p class="stat-label">Total Employees</p>
                        <p class="stat-change">/ ${totalUsers}</p>
                    </div>
                </div>

                <!-- Active Departments / Total Departments -->
                <div class="stat-card">
                    <div class="stat-icon stat-icon-2">
                        <svg viewBox="0 0 24 24" fill="currentColor">
                            <path d="M7 10c-1.1 0-2 .9-2 2s.9 2 2 2 2-.9 2-2-.9-2-2-2zm5-3c-1.1 0-2 .9-2 2s.9 2 2 2 2-.9 2-2-.9-2-2-2zm5 5c-1.1 0-2 .9-2 2s.9 2 2 2 2-.9 2-2-.9-2-2-2zM7 19c-1.1 0-2 .9-2 2s.9 2 2 2 2-.9 2-2-.9-2-2-2zm5-2c-1.1 0-2 .9-2 2s.9 2 2 2 2-.9 2-2-.9-2-2-2zm5-4c-1.1 0-2 .9-2 2s.9 2 2 2 2-.9 2-2-.9-2-2-2z"/>
                        </svg>
                    </div>
                    <div class="stat-body">
                        <p class="stat-value">${activeDepartments}</p>
                        <p class="stat-label">Active Departments</p>
                        <p class="stat-change">/ ${totalDepartments}</p>
                    </div>
                </div>

                <!-- Active Positions / Total Positions -->
                <div class="stat-card">
                    <div class="stat-icon stat-icon-3">
                        <svg viewBox="0 0 24 24" fill="currentColor">
                            <path d="M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z"/>
                        </svg>
                    </div>
                    <div class="stat-body">
                        <p class="stat-value">${activePositions}</p>
                        <p class="stat-label">Active Positions</p>
                        <p class="stat-change">/ ${totalPositions}</p>
                    </div>
                </div>

                <!-- Active Roles / Total Roles -->
                <div class="stat-card">
                    <div class="stat-icon stat-icon-4">
                        <svg viewBox="0 0 24 24" fill="currentColor">
                            <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-2 15l-5-5 1.41-1.41L10 14.17l7.59-7.59L19 8l-9 9z"/>
                        </svg>
                    </div>
                    <div class="stat-body">
                        <p class="stat-value">${activeRoles}</p>
                        <p class="stat-label">Active Roles</p>
                        <p class="stat-change">/ ${totalRoles}</p>
                    </div>
                </div>
            </div>
        </div>
    </main>
</div>

<script>
    document.addEventListener('DOMContentLoaded', function() {
        const toggle = document.getElementById('profileDropdownToggle');
        const dropdown = document.getElementById('profileDropdown');

        toggle.addEventListener('click', function(e) {
            e.stopPropagation();
            dropdown.classList.toggle('show');
        });

        document.addEventListener('click', function() {
            dropdown.classList.remove('show');
        });
    });
</script>

</body>
</html>