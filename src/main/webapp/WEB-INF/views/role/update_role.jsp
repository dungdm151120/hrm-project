<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Update Role | HRM</title>
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
            <h1 class="header-title">Update Role</h1>
            <div class="header-right">
                <a href="${pageContext.request.contextPath}/admin/roles" class="btn btn-secondary">← Back to List</a>
            </div>
        </header>

        <div class="dashboard-content">
            <c:if test="${not empty error}">
                <div class="alert alert-error">⚠ ${error}</div>
            </c:if>

            <c:choose>
                <c:when test="${not empty role}">
                    <div class="role-id-badge" style="margin-bottom: 1.5rem;">ID: ${role.id}</div>

                    <div class="form-wrapper">
                        <form action="${pageContext.request.contextPath}/admin/roles/update" method="post">
                            <input type="hidden" name="roleId" value="${role.id}">

                            <div class="form-group">
                                <label for="name">Role Name <span style="color: var(--danger);">*</span></label>
                                <input type="text" id="name" name="name"
                                       value="${role.name}"
                                       placeholder="Enter role name"
                                       required>
                            </div>

                            <div class="form-group">
                                <label for="description">Description</label>
                                <textarea id="description" name="description"
                                          placeholder="Enter description">${role.description}</textarea>
                            </div>

                            <div class="form-actions">
                                <button type="submit" class="btn-save">Save Changes</button>
                                <a href="${pageContext.request.contextPath}/admin/roles" class="btn-cancel">Cancel</a>
                            </div>
                        </form>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="empty-state">
                        Role not found.
                        <a href="${pageContext.request.contextPath}/admin/roles" style="color: var(--primary); font-weight: 500;">Back to list</a>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </main>
</div>
</body>
</html>