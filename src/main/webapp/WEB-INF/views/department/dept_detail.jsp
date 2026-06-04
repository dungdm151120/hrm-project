<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Department Detail | HRM</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    <style>
        .detail-wrapper {
            background: #fff;
            border-radius: 8px;
            padding: 2rem;
            box-shadow: 0 2px 8px rgba(0,0,0,0.05);
        }
        .detail-table {
            width: 100%;
            border-collapse: collapse;
        }
        .detail-table th,
        .detail-table td {
            padding: 0.75rem 1rem;
            text-align: left;
            border-bottom: 1px solid #e9ecef;
        }
        .detail-table th {
            width: 180px;
            background-color: #f8f9fa;
            font-weight: 600;
            color: #495057;
        }
        .form-actions {
            display: flex;
            gap: 1rem;
            margin-top: 1.5rem;
        }
        .text-muted {
            color: #6c757d;
            font-style: italic;
        }
        .header-actions {
            display: flex;
            gap: 1rem;
            align-items: center;
        }
        .alert {
            padding: 0.75rem 1rem;
            border-radius: 6px;
            margin-bottom: 1rem;
        }
        .alert-success {
            background-color: #d4edda;
            color: #155724;
            border: 1px solid #c3e6cb;
        }
        .alert-error {
            background-color: #f8d7da;
            color: #721c24;
            border: 1px solid #f5c6cb;
        }
    </style>
</head>
<body>

<jsp:include page="/WEB-INF/views/common/navbar.jsp" />

<div class="container" style="margin-top: 2rem;">

    <%-- Hiển thị thông báo từ session --%>
    <c:if test="${not empty sessionScope.successMessage}">
        <div class="alert alert-success">
            ${sessionScope.successMessage}
        </div>
        <% session.removeAttribute("successMessage"); %>
    </c:if>
    <c:if test="${not empty sessionScope.error}">
        <div class="alert alert-error">
            ${sessionScope.error}
        </div>
        <% session.removeAttribute("error"); %>
    </c:if>

    <div class="page-header">
        <h2>Department Detail</h2>
        <div class="header-actions">
            <a href="${pageContext.request.contextPath}/admin/departments/employees?id=${department.id}" class="btn btn-secondary">View employee list</a>
            <a href="${pageContext.request.contextPath}/admin/departments/assign-manager?id=${department.id}" class="btn btn-primary">Assign Manager</a>
            <a href="${pageContext.request.contextPath}/admin/departments" class="btn btn-secondary">← Back to List</a>
        </div>
    </div>


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
                <form action="${pageContext.request.contextPath}/admin/departments/toggle-status" method="post" style="display:inline;">
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

</body>
</html>