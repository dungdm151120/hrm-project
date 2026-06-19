<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Role Permission - ${role.name} | HRM</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body class="dashboard-body">

<div class="dashboard-wrapper">
    <jsp:include page="/WEB-INF/views/common/sidebar.jsp"/>

    <div class="dashboard-main">
        <div class="dashboard-header">
            <div class="header-left">
                <h1 class="header-title">Permissions of ${role.name}</h1>
            </div>
            <div class="header-right">
                <a href="${pageContext.request.contextPath}/admin/roles/edit_permissions?roleId=${role.id}" class="btn-primary">Edit Permissions</a>
            </div>
        </div>

        <div class="dashboard-content">
            <nav class="breadcrumb">
                <a href="${pageContext.request.contextPath}/home">Home</a>
                <span class="separator">›</span>
                <a href="${pageContext.request.contextPath}/admin/roles">Roles</a>
                <span class="separator">›</span>
                <span class="current">${role.name}</span>
            </nav>

            <div class="role-detail">
                <div class="role-meta">
                    <span class="role-meta-label">Description</span>
                    <span class="role-meta-value">${role.description}</span>
                </div>
                <div class="role-meta">
                    <span class="role-meta-label">Status</span>
                    <span class="role-meta-value">
                        <c:choose>
                            <c:when test="${role.active}">
                                <span class="badge badge-active">Active</span>
                            </c:when>
                            <c:otherwise>
                                <span class="badge badge-inactive">Inactive</span>
                            </c:otherwise>
                        </c:choose>
                    </span>
                </div>
            </div>

            <c:if test="${not empty param.success}">
                <div class="alert alert-success">✓ ${param.success}</div>
            </c:if>

            <!-- Permission Matrix Horizontal -->
            <div class="permission-matrix-horizontal">
                <c:forEach var="entry" items="${moduleMap}">
                    <c:if test="${not empty entry.value}">
                        <div class="permission-module-row">
                            <div class="module-label">
                                <c:choose>
                                    <c:when test="${entry.key eq 'HOMEPAGE'}">Home</c:when>
                                    <c:when test="${entry.key eq 'PROFILE'}">Profile</c:when>
                                    <c:when test="${entry.key eq 'AUTH'}">Authentication</c:when>
                                    <c:when test="${entry.key eq 'USER'}">User Management</c:when>
                                    <c:when test="${entry.key eq 'ROLE'}">Role Management</c:when>
                                    <c:when test="${entry.key eq 'DEPARTMENT'}">Department</c:when>
                                    <c:when test="${entry.key eq 'POSITION'}">Position</c:when>
                                    <c:when test="${entry.key eq 'CONTRACT'}">Contract</c:when>
                                    <c:when test="${entry.key eq 'ATTENDANCE'}">Attendance</c:when>
                                    <c:when test="${entry.key eq 'PAYROLL'}">Payroll</c:when>
                                    <c:when test="${entry.key eq 'ANNOUNCEMENT'}">Announcement</c:when>
                                    <c:when test="${entry.key eq 'REQUEST'}">Request</c:when>
                                    <c:when test="${entry.key eq 'TASK'}">Task</c:when>
                                    <c:otherwise>Other</c:otherwise>
                                </c:choose>
                            </div>
                            <div class="module-permissions">
                                <c:forEach var="perm" items="${entry.value}">
                                    <div class="permission-item">
                                        <div class="permission-info">
                                            <code class="perm-code">${perm.code}</code>
                                            <span class="perm-desc">${perm.description}</span>
                                        </div>
                                        <div class="permission-icon">
                                            <c:choose>
                                                <c:when test="${assignedPermissionIds.contains(perm.code)}">
                                                    <span class="icon-check">✅</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="icon-cross">❌</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </div>
                                    </div>
                                </c:forEach>
                            </div>
                        </div>
                    </c:if>
                </c:forEach>
            </div>
        </div>
    </div>
</div>

</body>
</html>