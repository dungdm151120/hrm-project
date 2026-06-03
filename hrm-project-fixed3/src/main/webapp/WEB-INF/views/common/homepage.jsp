<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dashboard | HRSync</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    <style>
        .user-avatar-placeholder {
            width: 40px; height: 40px; border-radius: 50%;
            background: var(--primary-bg); color: var(--primary-light);
            display: flex; align-items: center; justify-content: center;
            font-weight: 600; font-size: 1.1rem;
            border: 2px solid var(--border-color); flex-shrink: 0;
        }
        .profile-dropdown {
            position: absolute; top: 60px; right: 24px;
            background: var(--bg-card); border: 1px solid var(--border-color);
            border-radius: var(--radius); box-shadow: var(--shadow-lg);
            min-width: 200px; display: none; flex-direction: column;
            z-index: 1000; padding: 8px 0;
        }
        .profile-dropdown.show { display: flex; }
        .profile-dropdown a {
            display: flex; align-items: center; gap: 10px;
            padding: 10px 16px; color: var(--text-secondary);
            text-decoration: none; font-size: 0.9rem; transition: background 0.2s;
        }
        .profile-dropdown a:hover { background: var(--bg-hover); color: var(--text-primary); }
        .profile-dropdown a svg { width: 18px; height: 18px; }
    </style>
</head>
<body class="dashboard-page">
    <div class="dashboard-layout">
        <jsp:include page="/WEB-INF/views/common/sidebar.jsp" />

        <main class="main-content">
            <header class="main-header">
                <h1 class="header-title">Dashboard</h1>

                <div class="header-right">
                    <button class="notification-btn" aria-label="Notifications">
                        <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                            <path d="M6 8a6 6 0 0 1 12 0c0 7 3 9 3 9H3s3-2 3-9"></path>
                            <path d="M10.3 21a1.94 1.94 0 0 0 3.4 0"></path>
                        </svg>
                        <span class="notification-badge"></span>
                    </button>

                    <div class="user-profile" id="userProfile" onclick="toggleProfileDropdown(event)">
                        <c:choose>
                            <c:when test="${not empty currentUser.avatarUrl}">
                                <img src="${currentUser.avatarUrl}" alt="User Avatar" class="user-avatar">
                            </c:when>
                            <c:otherwise>
                                <div class="user-avatar-placeholder">
                                    ${not empty currentUser.fullName ? currentUser.fullName.substring(0,1).toUpperCase() : 'U'}
                                </div>
                            </c:otherwise>
                        </c:choose>
                        <div class="user-info">
                            <span class="user-name">${not empty currentUser.fullName ? currentUser.fullName : 'Jessie Alston'}</span>
                            <span class="user-status"><span class="status-dot"></span> Online</span>
                        </div>
                        <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="user-dropdown-icon">
                            <path d="m6 9 6 6 6-6"/>
                        </svg>

                        <div class="profile-dropdown" id="profileDropdown">
                            <a href="${pageContext.request.contextPath}/profile">
                                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                    <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
                                    <circle cx="12" cy="7" r="4"></circle>
                                </svg>
                                View My Profile
                            </a>
                            <a href="${pageContext.request.contextPath}/change-password">
                                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                    <rect x="3" y="11" width="18" height="11" rx="2" ry="2"></rect>
                                    <path d="M7 11V7a5 5 0 0 1 10 0v4"></path>
                                </svg>
                                Change Password
                            </a>
                            <a href="${pageContext.request.contextPath}/logout">
                                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                    <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"></path>
                                    <polyline points="16 17 21 12 16 7"></polyline>
                                    <line x1="21" y1="12" x2="9" y2="12"></line>
                                </svg>
                                Logout
                            </a>
                        </div>
                    </div>
                </div>
            </header>

            <div class="dashboard-content">
                <section class="summary-cards">
                    <!-- Total Employees: active / total -->
                    <div class="summary-card">
                        <div class="summary-icon blue">
                            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                <path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2"></path>
                                <circle cx="9" cy="7" r="4"></circle>
                                <path d="M22 21v-2a4 4 0 0 0-3-3.87"></path>
                                <path d="M16 3.13a4 4 0 0 1 0 7.75"></path>
                            </svg>
                        </div>
                        <div class="summary-details">
                            <div class="summary-value">
                                <span class="summary-number">${activeUsers != null ? activeUsers : 0}</span>
                                <span class="summary-total">/ ${totalUsers != null ? totalUsers : 0}</span>
                            </div>
                            <div class="summary-label">Total Employees</div>
                        </div>
                    </div>

                    <!-- Total Departments: active / total -->
                    <div class="summary-card">
                        <div class="summary-icon green">
                            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                <path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/>
                                <polyline points="9 22 9 12 15 12 15 22"/>
                            </svg>
                        </div>
                        <div class="summary-details">
                            <div class="summary-value">
                                <span class="summary-number">${activeDepartments != null ? activeDepartments : 0}</span>
                                <span class="summary-total">/ ${totalDepartments != null ? totalDepartments : 0}</span>
                            </div>
                            <div class="summary-label">Total Departments</div>
                        </div>
                    </div>

                    <!-- Total Positions: active / total -->
                    <div class="summary-card">
                        <div class="summary-icon purple">
                            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                <rect x="2" y="7" width="20" height="14" rx="2" ry="2"/>
                                <path d="M16 21V5a2 2 0 0 0-2-2h-4a2 2 0 0 0-2 2v16"/>
                            </svg>
                        </div>
                        <div class="summary-details">
                            <div class="summary-value">
                                <span class="summary-number">${activePositions != null ? activePositions : 0}</span>
                                <span class="summary-total">/ ${totalPositions != null ? totalPositions : 0}</span>
                            </div>
                            <div class="summary-label">Total Positions</div>
                        </div>
                    </div>

                    <!-- Total Roles: active / total -->
                    <div class="summary-card">
                        <div class="summary-icon orange">
                            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                <circle cx="12" cy="12" r="3"/>
                                <path d="M19.4 15a1.65 1.65 0 0 0 .33 1.82l.06.06a2 2 0 0 1 0 2.83 2 2 0 0 1-2.83 0l-.06-.06a1.65 1.65 0 0 0-1.82-.33 1.65 1.65 0 0 0-1 1.51V21a2 2 0 0 1-2 2 2 2 0 0 1-2-2v-.09A1.65 1.65 0 0 0 9 19.4a1.65 1.65 0 0 0-1.82.33l-.06.06a2 2 0 0 1-2.83 0 2 2 0 0 1 0-2.83l.06-.06a1.65 1.65 0 0 0 .33-1.82 1.65 1.65 0 0 0-1.51-1H3a2 2 0 0 1-2-2 2 2 0 0 1 2-2h.09A1.65 1.65 0 0 0 4.6 9a1.65 1.65 0 0 0-.33-1.82l-.06-.06a2 2 0 0 1 0-2.83 2 2 0 0 1 2.83 0l.06.06a1.65 1.65 0 0 0 1.82.33H9a1.65 1.65 0 0 0 1-1.51V3a2 2 0 0 1 2-2 2 2 0 0 1 2 2v.09a1.65 1.65 0 0 0 1 1.51 1.65 1.65 0 0 0 1.82-.33l.06-.06a2 2 0 0 1 2.83 0 2 2 0 0 1 0 2.83l-.06.06a1.65 1.65 0 0 0-.33 1.82V9a1.65 1.65 0 0 0 1.51 1H21a2 2 0 0 1 2 2 2 2 0 0 1-2 2h-.09a1.65 1.65 0 0 0-1.51 1z"/>
                            </svg>
                        </div>
                        <div class="summary-details">
                            <div class="summary-value">
                                <span class="summary-number">${activeRoles != null ? activeRoles : 0}</span>
                                <span class="summary-total">/ ${totalRoles != null ? totalRoles : 0}</span>
                            </div>
                            <div class="summary-label">Total Roles</div>
                        </div>
                    </div>
                </section>
            </div>
        </main>
    </div>

    <script>
        function toggleProfileDropdown(event) {
            event.stopPropagation();
            const dropdown = document.getElementById('profileDropdown');
            dropdown.classList.toggle('show');
        }
        document.addEventListener('click', function(event) {
            const dropdown = document.getElementById('profileDropdown');
            const profile = document.getElementById('userProfile');
            if (!profile.contains(event.target)) {
                dropdown.classList.remove('show');
            }
        });
    </script>
</body>
</html>