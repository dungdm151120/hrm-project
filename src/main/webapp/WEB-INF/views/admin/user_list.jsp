<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>User List | HRM</title>
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
            <h1 class="header-title">User List</h1>
            <div class="header-right">
                <a href="${pageContext.request.contextPath}/admin/users/add" class="btn btn-primary">Add New User</a>
            </div>
        </header>

        <div class="dashboard-content">
            <c:if test="${not empty param.success}">
                <div class="alert alert-success">✓ ${param.success}</div>
            </c:if>
            <c:if test="${not empty param.error}">
                <div class="alert alert-error">⚠ ${param.error}</div>
            </c:if>

            <div class="search-filter">
                <form action="${pageContext.request.contextPath}/user_list" method="GET">
                    <input type="text" name="search" placeholder="Search name" value="${oldKeyword}">
                    <select name="status">
                        <option value="all" ${status == 'all' || empty status ? 'selected' : ''}>All Status</option>
                        <option value="true" ${status == 'true' ? 'selected' : ''}>Active</option>
                        <option value="false" ${status == 'false' ? 'selected' : ''}>Inactive</option>
                    </select>
                    <select name="sort" onchange="this.form.submit()">
                        <option value="name_asc" ${sort == 'name_asc' ? 'selected' : ''}>Name A-Z</option>
                        <option value="name_desc" ${sort == 'name_desc' ? 'selected' : ''}>Name Z-A</option>
                    </select>
                    <button type="submit" class="btn btn-primary">Search</button>
                    <a href="${pageContext.request.contextPath}/user_list" class="btn btn-reset">Clear</a>
                </form>
            </div>

            <div class="table-wrapper">
                <table>
                    <thead>
                    <tr>
                        <th>ID</th>
                        <th>Full Name</th>
                        <th>Role</th>
                        <th>Department</th>
                        <th>Position</th>
                        <th>Status</th>
                        <th>Actions</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach items="${userList}" var="user" varStatus="s">
                        <tr>
                            <td>${(currentPage - 1) * 5 + s.index + 1}</td>
                            <td><strong>${user.fullName}</strong></td>
                            <td>${user.roleName}</td>
                            <td>${not empty user.departmentName ? user.departmentName : 'N/A'}</td>
                            <td>${user.positionName}</td>
                            <td>
                                <c:choose>
                                    <c:when test="${user.active}">
                                        <span class="badge badge-active">Active</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="badge badge-inactive">Inactive</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                <div class="actions">
                                    <a href="user_detail?id=${user.id}">View</a>
                                    <a href="${pageContext.request.contextPath}/users/update?id=${user.id}">Update</a>
                                    <form action="${pageContext.request.contextPath}/users/toggle-status" method="GET" style="display:inline;">
                                        <input type="hidden" name="id" value="${user.id}">
                                        <input type="hidden" name="action" value="${user.active ? 'Deactivate' : 'Activate'}">
                                        <button type="submit"
                                                class="btn ${user.active ? 'btn-danger' : 'btn-warning'}"
                                                onclick="return confirm('${user.active ? 'Deactivate' : 'Activate'} this user?')">
                                                ${user.active ? 'Deactivate' : 'Activate'}
                                        </button>
                                    </form>
                                </div>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty userList}">
                        <tr>
                            <td colspan="7" class="empty-state">No users found matching the criteria.</td>
                        </tr>
                    </c:if>
                    </tbody>
                </table>
            </div>

            <c:if test="${totalPages > 1}">
                <div class="pagination" style="margin-top: 1.5rem; display: flex; gap: 0.5rem; align-items: center; justify-content: center;">
                    <c:url var="firstPageUrl" value="/user_list">
                        <c:param name="page" value="1" />
                        <c:if test="${not empty oldKeyword}"><c:param name="search" value="${oldKeyword}" /></c:if>
                        <c:if test="${not empty status}"><c:param name="status" value="${status}" /></c:if>
                        <c:if test="${not empty sort}"><c:param name="sort" value="${sort}" /></c:if>
                    </c:url>
                    <a href="${firstPageUrl}" class="${currentPage == 1 ? 'disabled' : ''}">First</a>

                    <c:url var="prevPageUrl" value="/user_list">
                        <c:param name="page" value="${currentPage - 1}" />
                        <c:if test="${not empty oldKeyword}"><c:param name="search" value="${oldKeyword}" /></c:if>
                        <c:if test="${not empty status}"><c:param name="status" value="${status}" /></c:if>
                        <c:if test="${not empty sort}"><c:param name="sort" value="${sort}" /></c:if>
                    </c:url>
                    <a href="${prevPageUrl}" class="${currentPage <= 1 ? 'disabled' : ''}">Previous</a>

                    <span>Page <span class="current">${currentPage}</span> / ${totalPages}</span>

                    <c:url var="nextPageUrl" value="/user_list">
                        <c:param name="page" value="${currentPage + 1}" />
                        <c:if test="${not empty oldKeyword}"><c:param name="search" value="${oldKeyword}" /></c:if>
                        <c:if test="${not empty status}"><c:param name="status" value="${status}" /></c:if>
                        <c:if test="${not empty sort}"><c:param name="sort" value="${sort}" /></c:if>
                    </c:url>
                    <a href="${nextPageUrl}" class="${currentPage >= totalPages ? 'disabled' : ''}">Next</a>

                    <c:url var="lastPageUrl" value="/user_list">
                        <c:param name="page" value="${totalPages}" />
                        <c:if test="${not empty oldKeyword}"><c:param name="search" value="${oldKeyword}" /></c:if>
                        <c:if test="${not empty status}"><c:param name="status" value="${status}" /></c:if>
                        <c:if test="${not empty sort}"><c:param name="sort" value="${sort}" /></c:if>
                    </c:url>
                    <a href="${lastPageUrl}" class="${currentPage == totalPages ? 'disabled' : ''}">Last</a>
                </div>
            </c:if>
        </div>
    </main>
</div>
</body>
</html>
