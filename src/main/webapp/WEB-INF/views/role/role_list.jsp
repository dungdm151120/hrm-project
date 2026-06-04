<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Role List | HRM</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body class="dashboard-body">

<div class="dashboard-wrapper">

    <jsp:include page="/WEB-INF/views/common/sidebar.jsp"/>

    <div class="dashboard-main">
        <div class="dashboard-header">
            <div class="header-left">
                <h1 class="header-title">List Of Roles</h1>
            </div>
            <div class="header-right">
                <a href="${pageContext.request.contextPath}/admin/roles/add" class="btn-primary">Add New Role</a>
            </div>
        </div>

        <div class="dashboard-content">
            <c:if test="${not empty param.success}">
                <div class="alert alert-success">✓ ${param.success}</div>
            </c:if>
            <c:if test="${not empty param.error}">
                <div class="alert alert-error">⚠ ${param.error}</div>
            </c:if>

            <div class="search-filter">
                <form action="${pageContext.request.contextPath}/admin/roles" method="get">
                    <input type="text" name="search" placeholder="Search by role name" value="${not empty search ? search : ''}">
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
                    <button type="submit" class="search-btn">Search</button>
                    <a href="${pageContext.request.contextPath}/admin/roles" class="btn-reset">Reset</a>
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
                                            <span class="badge badge-inactive">Inactive</span>
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
                    <a href="${firstPageUrl}" class="page-link ${currentPage == 1 ? 'disabled' : ''}">First</a>

                    <c:url var="prevPageUrl" value="/admin/roles">
                        <c:param name="page" value="${currentPage - 1}" />
                        <c:if test="${not empty search}"><c:param name="search" value="${search}" /></c:if>
                        <c:if test="${not empty status}"><c:param name="status" value="${status}" /></c:if>
                        <c:if test="${not empty sort}"><c:param name="sort" value="${sort}" /></c:if>
                    </c:url>
                    <a href="${prevPageUrl}" class="page-link ${currentPage <= 1 ? 'disabled' : ''}">Previous</a>

                    <span class="page-link active">${currentPage} / ${totalPages}</span>

                    <c:url var="nextPageUrl" value="/admin/roles">
                        <c:param name="page" value="${currentPage + 1}" />
                        <c:if test="${not empty search}"><c:param name="search" value="${search}" /></c:if>
                        <c:if test="${not empty status}"><c:param name="status" value="${status}" /></c:if>
                        <c:if test="${not empty sort}"><c:param name="sort" value="${sort}" /></c:if>
                    </c:url>
                    <a href="${nextPageUrl}" class="page-link ${currentPage >= totalPages ? 'disabled' : ''}">Next</a>

                    <c:url var="lastPageUrl" value="/admin/roles">
                        <c:param name="page" value="${totalPages}" />
                        <c:if test="${not empty search}"><c:param name="search" value="${search}" /></c:if>
                        <c:if test="${not empty status}"><c:param name="status" value="${status}" /></c:if>
                        <c:if test="${not empty sort}"><c:param name="sort" value="${sort}" /></c:if>
                    </c:url>
                    <a href="${lastPageUrl}" class="page-link ${currentPage == totalPages ? 'disabled' : ''}">Last</a>
                </div>
            </c:if>
        </div>
    </div>
</div>

</body>
</html>