<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Department Detail | HRM</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    <style>
        /* Override detail-wrapper for dark theme */
        .detail-wrapper {
            background: var(--bg-card);
            border-radius: var(--radius-lg);
            padding: 2rem;
            box-shadow: var(--shadow-card);
            border: 1px solid var(--border-color);
        }
        .detail-table {
            width: 100%;
            border-collapse: collapse;
        }
        .detail-table th,
        .detail-table td {
            padding: 0.75rem 1rem;
            text-align: left;
            border-bottom: 1px solid var(--border-color);
            color: var(--text-primary);
        }
        .detail-table th {
            width: 180px;
            background-color: var(--bg-card-inner);
            font-weight: 600;
            color: var(--text-secondary);
            font-size: 0.85rem;
            text-transform: uppercase;
            letter-spacing: 0.03em;
        }
        .detail-table td {
            background: transparent;
        }
        .text-muted {
            color: var(--text-muted);
            font-style: italic;
        }
        .form-actions {
            display: flex;
            gap: 1rem;
            margin-top: 1.5rem;
        }
        .header-actions {
            display: flex;
            gap: 1rem;
            align-items: center;
        }
    </style>
</head>
<body class="dashboard-page">
<div class="dashboard-layout">
    <jsp:include page="/WEB-INF/views/common/sidebar.jsp" />

    <main class="main-content">
        <header class="main-header">
            <h1 class="header-title">Department Detail</h1>
            <div class="header-right">
                <a href="${pageContext.request.contextPath}/admin/departments/employees?id=${department.id}" class="btn btn-secondary">View employee list</a>
                <a href="${pageContext.request.contextPath}/admin/departments" class="btn btn-secondary">← Back to List</a>
            </div>
        </header>

        <div class="dashboard-content">
            <c:if test="${empty department}">
                <div class="alert alert-error">
                    <span>⚠</span> Department not found.
                </div>
            </c:if>

            <c:if test="${not empty department}">
                <div class="detail-wrapper">
                    <table class="detail-table">
                        <tr>
                            <th>ID</th>
                            <td>${department.id}</td>
                        </tr>
                        <tr>
                            <th>Name</th>
                            <td><strong>${department.name}</strong></td>
                        </tr>
                        <tr>
                            <th>Description</th>
                            <td>${not empty department.description ? department.description : '<span class="text-muted">No description</span>'}</td>
                        </tr>
                        <tr>
                            <th>Manager</th>
                            <td>
                                <c:choose>
                                    <c:when test="${not empty department.managerName}">
                                        ${department.managerName}
                                    </c:when>
                                    <c:otherwise>
                                        <span class="text-muted">No manager assigned</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                        </tr>
                        <tr>
                            <th>Status</th>
                            <td>
                                <c:choose>
                                    <c:when test="${department.active}">
                                        <span class="badge badge-active">Active</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="badge badge-inactive">Inactive</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                        </tr>
                        <tr>
                            <th>Created At</th>
                            <td>${department.createdAt}</td>
                        </tr>
                        <tr>
                            <th>Updated At</th>
                            <td>${not empty department.updatedAt ? department.updatedAt : '<span class="text-muted">Not updated</span>'}</td>
                        </tr>
                    </table>

                    <div class="form-actions">
                        <a href="${pageContext.request.contextPath}/admin/departments/update?id=${department.id}" class="btn btn-primary">Update Info</a>
                        <form action="${pageContext.request.contextPath}/admin/departments/toggle-status" method="post">
                            <input type="hidden" name="id" value="${department.id}">
                            <button type="submit" class="btn ${department.active ? 'btn-danger' : 'btn-warning'}" onclick="return confirm('Are you sure?')">
                                <c:choose>
                                    <c:when test="${department.active}">Deactivate</c:when>
                                    <c:otherwise>Activate</c:otherwise>
                                </c:choose>
                            </button>
                        </form>
                    </div>
                </div>
            </c:if>
        </div>
    </main>
</div>
</body>
</html>