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
            display: flex;
            align-items: center;
            gap: 0.5rem;
            flex-wrap: wrap;
            margin-bottom: 1.5rem;
        }
        .search-filter input[type="text"],
        .search-filter select {
            padding: 0.5rem;
            border: 1px solid #ccc;
            border-radius: 4px;
        }
        .pagination {
            margin-top: 1.5rem;
            display: flex;
            gap: 0.5rem;
            align-items: center;
            justify-content: center;
        }
        .pagination a, .pagination span {
            padding: 0.25rem 0.75rem;
            border: 1px solid #ccc;
            border-radius: 4px;
            text-decoration: none;
            color: #333;
            display: inline-block;
        }
        .pagination a:hover {
            background: #007bff;
            color: #fff;
            border-color: #007bff;
        }
        .pagination .current {
            background: #007bff;
            color: #fff;
            border-color: #007bff;
            font-weight: bold;
        }
        .pagination .disabled {
            pointer-events: none;
            opacity: 0.5;
            cursor: not-allowed;
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

    <c:if test="${not empty param.success}">
        <div class="alert alert-success">✓ ${param.success}</div>
    </c:if>
    <c:if test="${not empty param.error}">
        <div class="alert alert-error">⚠ ${param.error}</div>
    </c:if>

    <div class="search-filter">
        <form action="${pageContext.request.contextPath}/admin/roles" method="get">
            <input type="text" name="search" placeholder="Search by role name"
                   value="${not empty search ? search : ''}">
            <select name="status">
                <option value="all" ${status == 'all' || empty status ? 'selected' : ''}>All status</option>
                <option value="true" ${status == 'true' ? 'selected' : ''}>Active</option>
                <option value="false" ${status == 'false' ? 'selected' : ''}>Inactive</option>
            </select>
            <select name="sort" onchange="this.form.submit()">
                <option value="name_asc" ${sort == 'name_asc' ? 'selected' : ''}>Name A-Z</option>
                <option value="name_desc" ${sort == 'name_desc' ? 'selected' : ''}>Name Z-A</option>
                <option value="perm_desc" ${sort == 'perm_desc' ? 'selected' : ''}>Most permissions</option>
                <option value="perm_asc" ${sort == 'perm_asc' ? 'selected' : ''}>Least permissions</option>
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
                        <td>${(currentPage - 1) * 5 + s.index + 1}</td>
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
                                            <c:otherwise>Activate</c:otherwise>
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

    <c:if test="${totalPages > 1}">
        <div class="pagination">
            <c:url var="firstPageUrl" value="/admin/roles">
                <c:param name="page" value="1" />
                <c:if test="${not empty search}"><c:param name="search" value="${search}" /></c:if>
                <c:if test="${not empty status}"><c:param name="status" value="${status}" /></c:if>
                <c:if test="${not empty sort}"><c:param name="sort" value="${sort}" /></c:if>
            </c:url>
            <a href="${firstPageUrl}" class="${currentPage == 1 ? 'disabled' : ''}">First</a>

            <c:url var="prevPageUrl" value="/admin/roles">
                <c:param name="page" value="${currentPage - 1}" />
                <c:if test="${not empty search}"><c:param name="search" value="${search}" /></c:if>
                <c:if test="${not empty status}"><c:param name="status" value="${status}" /></c:if>
                <c:if test="${not empty sort}"><c:param name="sort" value="${sort}" /></c:if>
            </c:url>
            <a href="${prevPageUrl}" class="${currentPage <= 1 ? 'disabled' : ''}">Previous</a>

            <span>Page <span class="current">${currentPage}</span> / ${totalPages}</span>

            <c:url var="nextPageUrl" value="/admin/roles">
                <c:param name="page" value="${currentPage + 1}" />
                <c:if test="${not empty search}"><c:param name="search" value="${search}" /></c:if>
                <c:if test="${not empty status}"><c:param name="status" value="${status}" /></c:if>
                <c:if test="${not empty sort}"><c:param name="sort" value="${sort}" /></c:if>
            </c:url>
            <a href="${nextPageUrl}" class="${currentPage >= totalPages ? 'disabled' : ''}">Next</a>

            <c:url var="lastPageUrl" value="/admin/roles">
                <c:param name="page" value="${totalPages}" />
                <c:if test="${not empty search}"><c:param name="search" value="${search}" /></c:if>
                <c:if test="${not empty status}"><c:param name="status" value="${status}" /></c:if>
                <c:if test="${not empty sort}"><c:param name="sort" value="${sort}" /></c:if>
            </c:url>
            <a href="${lastPageUrl}" class="${currentPage == totalPages ? 'disabled' : ''}">Last</a>
        </div>
    </c:if>
</div>

</body>
</html>