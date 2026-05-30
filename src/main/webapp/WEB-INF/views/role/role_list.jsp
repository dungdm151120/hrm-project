<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>List Of Roles | HRM</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    <style>
        .search-filter {
            margin-bottom: 1.5rem;
            display: flex;
            align-items: center;
            gap: 1rem;
            flex-wrap: wrap;
        }
        .search-filter input[type="text"],
        .search-filter select {
            padding: 0.5rem;
            border: 1px solid #ccc;
            border-radius: 4px;
        }
        .search-filter button, .search-filter .btn-reset {
            padding: 0.5rem 1rem;
            cursor: pointer;
        }
    </style>
</head>
<body>

<jsp:include page="/WEB-INF/views/common/navbar.jsp" />

<div class="container" style="margin-top: 2rem;">
    <div class="page-header">
        <h2>List Of Roles</h2>
        <a href="${pageContext.request.contextPath}/admin/roles/add" class="btn btn-primary">Add New Role</a>
    </div>

    <%-- Hiển thị thông báo từ param (nếu có) --%>
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

    <%-- Form tìm kiếm và lọc --%>
    <div class="search-filter">
        <form action="${pageContext.request.contextPath}/admin/roles" method="get">
            <input type="text" name="search" placeholder="Search by role name"
                   value="${not empty search ? search : ''}">
            <select name="status">
                <option value="all" ${status == 'all' || empty status ? 'selected' : ''}>All status</option>
                <option value="true" ${status == 'true' ? 'selected' : ''}>Active</option>
                <option value="false" ${status == 'false' ? 'selected' : ''}>Inactive</option>
            </select>
            <button type="submit" class="btn btn-primary">Search</button>
            <a href="${pageContext.request.contextPath}/admin/roles" class="btn btn-reset">Reset</a>
        </form>
    </div>

    <div class="table-wrapper">
        <table>
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Role name</th>
                    <th>Description</th>
                    <th>Status</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="role" items="${roles}" varStatus="s">
                    <tr>
                        <td>${s.index + 1}</td>
                        <td><strong>${role.name}</strong></td>
                        <td>${role.description}</td>
                        <td>
                            <c:choose>
                                <c:when test="${role.active}">
                                    <span class="badge badge-active">Active</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="badge badge-inactive">Vô hiệu</span>
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td>
                            <div class="actions">
                                <a href="${pageContext.request.contextPath}/admin/roles/permissions?roleId=${role.id}">Inspect Role</a>
                                <a href="${pageContext.request.contextPath}/admin/roles/edit_permissions?roleId=${role.id}">Edit Permissions</a>
                                <a href="${pageContext.request.contextPath}/admin/roles/update?roleId=${role.id}">Update Info</a>
                                <form action="${pageContext.request.contextPath}/admin/roles/toggle_status" method="post" style="display:inline;">
                                    <input type="hidden" name="roleId" value="${role.id}">
                                    <button type="submit"
                                            class="btn ${role.active ? 'btn-danger' : 'btn-warning'}"
                                            onclick="return confirm('Are you sure?')">
                                        <c:choose>
                                            <c:when test="${role.active}">Deactivate</c:when>
                                            <c:otherwise>Kích hoạt</c:otherwise>
                                        </c:choose>
                                    </button>
                                </form>
                            </div>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty roles}">
                    <tr>
                        <td colspan="5" class="empty-state">There are no roles.</td>
                    </tr>
                </c:if>
            </tbody>
        </table>
    </div>
</div>

</body>
</html>