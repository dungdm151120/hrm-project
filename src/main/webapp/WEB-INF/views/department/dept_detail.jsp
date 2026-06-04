<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Department Detail | HRM</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body class="dashboard-body">

<div class="dashboard-wrapper">

    <jsp:include page="/WEB-INF/views/common/sidebar.jsp"/>

    <div class="dashboard-main">
        <div class="dashboard-header">
            <div class="header-left">
                <h1 class="header-title">Department Detail</h1>
            </div>
            <div class="header-right">
                <a href="${pageContext.request.contextPath}/admin/departments" class="btn-secondary">← Back to List</a>
            </div>
        </div>

        <div class="dashboard-content">
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

            <c:if test="${empty department}">
                <div class="alert alert-error">
                    <span>⚠</span> Department not found.
                </div>
            </c:if>

            <c:if test="${not empty department}">

                <!-- Nút hành động màu xanh, đặt trên cùng của nội dung chi tiết -->
                <div class="department-actions">
                    <a href="${pageContext.request.contextPath}/admin/departments/employees?id=${department.id}" class="btn-primary">View employee list</a>
                    <a href="${pageContext.request.contextPath}/admin/departments/assign-manager?id=${department.id}" class="btn-primary">Assign Manager</a>
                    <a href="${pageContext.request.contextPath}/admin/departments/assign-positions?id=${department.id}" class="btn-primary">Assign Positions</a>
                </div>

                <div class="detail-content">
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
                        <a href="${pageContext.request.contextPath}/admin/departments/update?id=${department.id}" class="btn-primary">Update Info</a>
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
    </div>
</div>

</body>
</html>