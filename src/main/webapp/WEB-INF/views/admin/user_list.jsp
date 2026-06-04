<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>User List | HRM</title>
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
            margin-top: 1rem;
            display: flex;
            gap: 0.5rem;
            align-items: center;
        }

        .pagination a, .pagination span {
            padding: 0.25rem 0.75rem;
            border: 1px solid #ccc;
            border-radius: 4px;
            text-decoration: none;
            color: #333;
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
        }
    </style>
</head>
<body>

<jsp:include page="/WEB-INF/views/common/navbar.jsp"/>

<div class="container" style="margin-top: 2rem;">
    <div class="page-header">
        <h2>User List</h2>
        <a href="${pageContext.request.contextPath}/admin/users/add" class="btn btn-primary">Add New User</a>
    </div>

    <div class="search-filter">
        <form action="${pageContext.request.contextPath}/user_list" method="GET">
            <input type="text" name="keyword" placeholder="Search name" value="${keyword}">
            <select name="status">
                <option value="all" ${status == 'all' ? 'selected' : ''}>All Status</option>
                <option value="active" ${status == 'active' ? 'selected' : ''}>Active</option>
                <option value="inactive" ${status == 'inactive' ? 'selected' : ''}>Inactive</option>
            </select>

            <select name="sort" onchange="this.form.submit()">
                <option value="name_asc" ${sort == 'name_asc' ? 'selected' : ''}>Name (A-Z)</option>
                <option value="name_desc" ${sort == 'name_desc' ? 'selected' : ''}>Name (Z-A)</option>
            </select>
            <button type="submit" class="btn btn-primary">Search</button>
            <a href="${pageContext.request.contextPath}/user_list" class="btn btn-secondary">Clear</a>
        </form>
    </div>

    <div class="table-wrapper">
        <table>
            <thead>
            <tr>
                <th>ID</th>
                <th>Full Name</th>
                <th>Department</th>
                <th>Position</th>
                <th>Status</th>
                <th>Actions</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach items="${userList}" var="user" varStatus="s">
                <tr>
                    <td>${(currentPage - 1) * pageSize + s.index + 1}</td>
                    <td><strong>${user.fullName}</strong></td>
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
                            <a href="user_detail?id=${user.id}" class="btn btn-secondary">View</a>
                            <a href="${pageContext.request.contextPath}/users/update?id=${user.id}"
                               class="btn btn-secondary">Update</a>

                            <form action="${pageContext.request.contextPath}/users/toggle-status" method="GET"
                                  style="display:inline;">
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
                    <td colspan="7" style="text-align: center; color: #666; font-style: italic; padding: 15px;">No users
                        found.
                    </td>
                </tr>
            </c:if>
            </tbody>
        </table>
    </div>

    <div class="pagination employee-pagination">
        <span class="page-link active">
            <c:choose>
                <c:when test="${totalPages == 0}">1/1</c:when>
                <c:otherwise>${currentPage}/${totalPages}</c:otherwise>
            </c:choose>
        </span>

        <c:if test="${totalPages > 1}">
            <c:choose>
                <c:when test="${currentPage <= 1}">
                    <span class="page-link disabled">Previous</span>
                </c:when>
                <c:otherwise>
                    <c:url var="previousPageUrl" value="/user_list">
                        <c:param name="keyword" value="${keyword}"/>
                        <c:param name="status" value="${status}"/>
                        <c:param name="sort" value="${sort}"/>
                        <c:param name="page" value="${currentPage - 1}"/>
                    </c:url>
                    <a href="${previousPageUrl}" class="page-link">Previous</a>
                </c:otherwise>
            </c:choose>

            <c:forEach begin="1" end="${totalPages}" var="pageNumber">
                <c:url var="pageUrl" value="/user_list">
                    <c:param name="keyword" value="${keyword}"/>
                    <c:param name="status" value="${status}"/>
                    <c:param name="sort" value="${sort}"/>
                    <c:param name="page" value="${pageNumber}"/>
                </c:url>
                <a href="${pageUrl}" class="page-link ${currentPage == pageNumber ? 'active' : ''}">${pageNumber}</a>
            </c:forEach>

            <c:choose>
                <c:when test="${currentPage >= totalPages}">
                    <span class="page-link disabled">Next</span>
                </c:when>
                <c:otherwise>
                    <c:url var="nextPageUrl" value="/user_list">
                        <c:param name="keyword" value="${keyword}"/>
                        <c:param name="status" value="${status}"/>
                        <c:param name="sort" value="${sort}"/>
                        <c:param name="page" value="${currentPage + 1}"/>
                    </c:url>
                    <a href="${nextPageUrl}" class="page-link">Next</a>
                </c:otherwise>
            </c:choose>
        </c:if>
    </div>
</div>

</body>
</html>