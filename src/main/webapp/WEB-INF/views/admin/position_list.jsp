<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>User List | HRM</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
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
            <input type="text" name="search" placeholder="Search name" value="${oldKeyword}">

            <select name="status">
                <option value="all" ${status == 'all' || empty status ? 'selected' : ''}>All status</option>
                <option value="true" ${status == 'true' ? 'selected' : ''}>Active</option>
                <option value="false" ${status == 'false' ? 'selected' : ''}>Inactive</option>
            </select>

            <button type="submit" class="btn btn-primary">Search</button>

            <c:if test="${not empty oldKeyword}">
                <a href="${pageContext.request.contextPath}/position/list" class="btn btn-reset">Clear</a>
            </c:if>
        </form>
    </div>

    <div class="table-wrapper">
        <table>
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Name</th>
                    <th>Description</th>
                    <th>Status</th>
                    <th>Last Updated</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach items="${positionList}" var="position">
                    <tr>
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
                                <c:choose>
                                    <c:when test="${position.active}">
                                        <a href="${pageContext.request.contextPath}/position/toggle-status?id=${position.id}&action=deactivate"
                                           class="btn btn-danger"
                                           onclick="return confirm('Deactivate this position?')">Deactivate</a>
                                    </c:when>
                                    <c:otherwise>
                                        <a href="${pageContext.request.contextPath}/position/toggle-status?id=${position.id}&action=activate"
                                           class="btn btn-warning"
                                           onclick="return confirm('Activate this position?')">Activate</a>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty positionList}">
                    <tr>
                        <td colspan="6" class="empty-state">No position found.</td>
                    </tr>
                </c:if>
            </tbody>
        </table>
    </div>
</div>

</body>
</html>
