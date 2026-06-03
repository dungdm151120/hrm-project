<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Role Permissions - ${role.name} | HRM</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body class="dashboard-page">
<div class="dashboard-layout">
    <jsp:include page="/WEB-INF/views/common/sidebar.jsp" />

    <main class="main-content">
        <header class="main-header">
            <h1 class="header-title">Role Permissions: ${role.name}</h1>
            <div class="header-right">
                <a href="${pageContext.request.contextPath}/admin/roles/edit_permissions?roleId=${role.id}" class="btn btn-primary">✎ Edit Permissions</a>
                <a href="${pageContext.request.contextPath}/admin/roles" class="btn btn-secondary">← Back to Roles</a>
            </div>
        </header>

        <div class="dashboard-content">
            <nav class="breadcrumb">
                <a href="${pageContext.request.contextPath}/home">Home</a>
                <span class="separator">›</span>
                <a href="${pageContext.request.contextPath}/admin/roles">Roles</a>
                <span class="separator">›</span>
                <span class="current">${role.name}</span>
            </nav>

            <c:if test="${not empty param.success}">
                <div class="alert alert-success">✓ ${param.success}</div>
            </c:if>

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

            <c:choose>
                <c:when test="${not empty rolePermissions}">
                    <p class="total-permission">Total permissions: <span>${rolePermissions.size()}</span></p>
                    <div class="table-wrapper">
                        <table>
                            <thead>
                                <tr>
                                    <th>No.</th>
                                    <th>Code</th>
                                    <th>Name</th>
                                    <th>Description</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="perm" items="${rolePermissions}" varStatus="s">
                                    <tr>
                                        <td>${s.index + 1}</td>
                                        <td><code>${perm.code}</code></td>
                                        <td>${perm.name}</td>
                                        <td>${perm.description}</td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </c:when>
                <c:otherwise>
                    <p class="empty-state">This role has no permissions yet.</p>
                </c:otherwise>
            </c:choose>
        </div>
    </main>
</div>
</body>
</html>