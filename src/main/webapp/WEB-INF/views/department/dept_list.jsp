<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Department List | HRM</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body>

<jsp:include page="/WEB-INF/views/common/navbar.jsp" />

<div class="container" style="margin-top: 2rem;">
    <div class="page-header">
        <h2>Department List</h2>
        <a href="${pageContext.request.contextPath}/admin/departments/add" class="btn btn-primary">Add New Department</a>
    </div>

    <c:if test="${not empty param.success}">
        <div class="alert alert-success">
            <span>✓</span> ${param.success}
        </div>
    </c:if>
    <c:if test="${not empty param.error}">
        <div class="alert alert-error">
            <span>⚠</span> ${param.error}
        </div>
    </c:if>

    <div class="search-filter">
        <form action="${pageContext.request.contextPath}/admin/departments" method="get">
            <input type="text" name="search" placeholder="Search by name..." value="${not empty search ? search : ''}">
            <select name="status">
                <option value="all" ${status == 'all' || empty status ? 'selected' : ''}>All Status</option>
                <option value="true" ${status == 'true' ? 'selected' : ''}>Active</option>
                <option value="false" ${status == 'false' ? 'selected' : ''}>Inactive</option>
            </select>
            <button type="submit" class="btn btn-primary">Search</button>
            <a href="${pageContext.request.contextPath}/admin/departments" class="btn btn-reset">Clear</a>
        </form>
    </div>

    <div class="table-wrapper">
        <table>
            <thead>
            <tr>
                <th>ID</th>
                <th>Name</th>
                <th>Description</th>
                <th>Manager</th>
                <th>Status</th>
                <th>Actions</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="dept" items="${departmentList}" varStatus="s">
                <tr>
                    <td>${s.index + 1}</td>
                    <td><strong>${dept.name}</strong></td>
                    <td>${dept.description}</td>
                    <td>
                        <c:choose>
                            <c:when test="${not empty dept.managerName}">
                                ${dept.managerName}
                            </c:when>
                            <c:otherwise>
                                <c:if test="${not empty dept.managerUserId}">
                                    Manager #${dept.managerUserId}
                                </c:if>
                                <c:if test="${empty dept.managerUserId}">
                                    <span class="text-muted">No manager</span>
                                </c:if>
                            </c:otherwise>
                        </c:choose>
                    </td>
                    <td>
                        <c:choose>
                            <c:when test="${dept.active}">
                                <span class="badge badge-active">Active</span>
                            </c:when>
                            <c:otherwise>
                                <span class="badge badge-inactive">Inactive</span>
                            </c:otherwise>
                        </c:choose>
                    </td>
                    <td>
                        <div class="actions">
                            <a href="${pageContext.request.contextPath}/admin/departments/detail?id=${dept.id}">View Detail</a>
                            <a href="${pageContext.request.contextPath}/admin/departments/update?id=${dept.id}">Update</a>
                            <form action="${pageContext.request.contextPath}/admin/departments/toggle-status" method="post" style="display:inline;">
                                <input type="hidden" name="id" value="${dept.id}">
                                <button type="submit"
                                        class="btn ${dept.active ? 'btn-danger' : 'btn-warning'}"
                                        onclick="return confirm('Are you sure?')">
                                    <c:choose>
                                        <c:when test="${dept.active}">Deactivate</c:when>
                                        <c:otherwise>Activate</c:otherwise>
                                    </c:choose>
                                </button>
                            </form>
                        </div>
                    </td>
                </tr>
            </c:forEach>
            <c:if test="${empty departmentList}">
                <tr>
                    <td colspan="6" class="empty-state">No departments found.</td>
                </tr>
            </c:if>
            </tbody>
        </table>
    </div>
</div>

</body>
</html>
