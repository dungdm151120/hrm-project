<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Position List | HRM</title>
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
        --%>
</head>
<body>

<jsp:include page="/WEB-INF/views/common/navbar.jsp" />

<div class="container" style="margin-top: 2rem;">
    <div class="page-header">
        <h2>Position List</h2>
        <a href="${pageContext.request.contextPath}/position/add" class="btn btn-primary">Add New Position</a>
    </div>

    <div class="search-filter">
        <form action="${pageContext.request.contextPath}/position/list" method="GET">
            <input type="text" name="search" placeholder="Search name" value="${keyword}">

            <select name="status" onchange="this.form.submit()">
                <option value="all" ${status == 'all' || empty status ? 'selected' : ''}>All status</option>
                <option value="true" ${status == 'true' ? 'selected' : ''}>Active</option>
                <option value="false" ${status == 'false' ? 'selected' : ''}>Inactive</option>
            </select>

            <select name="sort" onchange="this.form.submit()">
                <option value="name_asc" ${sort == 'name_asc' ? 'selected' : ''}>Name A-Z</option>
                <option value="name_desc" ${sort == 'name_desc' ? 'selected' : ''}>Name Z-A</option>
                <option value="id_desc" ${sort == 'id_desc' || empty sort ? 'selected' : ''}>Newest</option>
                <option value="id_asc" ${sort == 'id_asc'  ? 'selected' : ''}>Oldest</option>
            </select>

            <button type="submit" class="btn btn-primary">Search</button>

            <c:if test="${not empty keyword}">
                <a href="${pageContext.request.contextPath}/position/list" class="btn btn-reset">Clear</a>
            </c:if>
        </form>
    </div>

    <div class="table-wrapper">
        <table>
            <thead>
                <tr>
                    <th>#</th> <th>ID</th> <th>Name</th>
                    <th>Description</th>
                    <th>Status</th>
                    <th>Last Updated</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach items="${positionList}" var="position" varStatus="s">
                    <tr>
                        <td>${(currentPage - 1) * 5 + s.index + 1}</td>
                        <td>${position.id}</td>
                        <td><strong>${position.name}</strong></td>
                        <td>${position.description}</td>
                        <td>
                            <c:choose>
                                <c:when test="${position.active}">
                                    <span class="badge badge-active">Active</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="badge badge-inactive">Inactive</span>
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td>
                            ${position.updatedAt != null ? position.updatedAt.toLocalDate() : (position.createdAt != null ? position.createdAt.toLocalDate() : '')}
                        </td>
                        <td>
                            <div class="actions">
                                <a href="${pageContext.request.contextPath}/position/update?id=${position.id}">Update</a>
                                </div>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty positionList}">
                    <tr>
                        <td colspan="7" class="empty-state">No position found.</td>
                    </tr>
                </c:if>
            </tbody>
        </table>
    </div>

    <c:if test="${totalPage > 1}">
        <div class="pagination">
            <c:url var="firstPageUrl" value="/position/list">
                <c:param name="page" value="1" />
                <c:if test="${not empty keyword}"><c:param name="search" value="${keyword}" /></c:if>
                <c:if test="${not empty status}"><c:param name="status" value="${status}" /></c:if>
                <c:if test="${not empty sort}"><c:param name="sort" value="${sort}" /></c:if>
            </c:url>
            <a href="${firstPageUrl}" class="${currentPage == 1 ? 'disabled' : ''}">First</a>

            <c:url var="prevPageUrl" value="/position/list">
                <c:param name="page" value="${currentPage - 1}" />
                <c:if test="${not empty keyword}"><c:param name="search" value="${keyword}" /></c:if>
                <c:if test="${not empty status}"><c:param name="status" value="${status}" /></c:if>
                <c:if test="${not empty sort}"><c:param name="sort" value="${sort}" /></c:if>
            </c:url>
            <a href="${prevPageUrl}" class="${currentPage <= 1 ? 'disabled' : ''}">Previous</a>

            <span>Page <span class="current">${currentPage}</span> / ${totalPage}</span>

            <c:url var="nextPageUrl" value="/position/list">
                <c:param name="page" value="${currentPage + 1}" />
                <c:if test="${not empty keyword}"><c:param name="search" value="${keyword}" /></c:if>
                <c:if test="${not empty status}"><c:param name="status" value="${status}" /></c:if>
                <c:if test="${not empty sort}"><c:param name="sort" value="${sort}" /></c:if>
            </c:url>
            <a href="${nextPageUrl}" class="${currentPage >= totalPage ? 'disabled' : ''}">Next</a>

            <c:url var="lastPageUrl" value="/position/list">
                <c:param name="page" value="${totalPage}" />
                <c:if test="${not empty keyword}"><c:param name="search" value="${keyword}" /></c:if>
                <c:if test="${not empty status}"><c:param name="status" value="${status}" /></c:if>
                <c:if test="${not empty sort}"><c:param name="sort" value="${sort}" /></c:if>
            </c:url>
            <a href="${lastPageUrl}" class="${currentPage == totalPage ? 'disabled' : ''}">Last</a>
        </div>
    </c:if>
</div>

</body>
</html>
