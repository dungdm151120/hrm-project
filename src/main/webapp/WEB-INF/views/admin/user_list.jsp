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
        <h2>User List</h2>
    </div>

    <div class="search-container" style="margin-bottom: 1.5rem; display: flex; gap: 8px; align-items: center;">
        <form action="${pageContext.request.contextPath}/user_list" method="GET" style="display: flex; gap: 8px; align-items: center;">
            <input type="text" name="search" placeholder="Search name or email..."
                   value="${oldKeyword}"
                   style="height: 38px; padding: 0 12px; border: 1px solid #ccc; border-radius: 4px; width: 280px; box-sizing: border-box; font-size: 14px;">

            <button type="submit" style="height: 38px; padding: 0 20px; background-color: #1E40AF; color: white; border: none; border-radius: 4px; cursor: pointer; font-weight: 600; font-size: 14px;">
                Search
            </button>

            <c:if test="${not empty oldKeyword}">
                <a href="${pageContext.request.contextPath}/user_list"
                   style="height: 38px; line-height: 38px; padding: 0 16px; background-color: #6c757d; color: white; text-decoration: none; border-radius: 4px; font-size: 14px;">
                    Clear
                </a>
            </c:if>
        </form>
    </div>

    <div class="table-wrapper">
        <table>
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Full Name</th>
                    <th>Email</th>
                    <th>Role</th>
                    <th>Status</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach items="${userList}" var="user">
                    <tr>
                        <td>${user.id}</td>
                        <td><strong>${user.fullName}</strong></td>
                        <td>${user.email}</td>
                        <td>${user.roleName}</td>
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
                                <a href="user_detail?id=${user.id}">View Detail</a>
                                <a href="${pageContext.request.contextPath}/users/update?id=${user.id}">Update</a>
                                <c:choose>
                                    <c:when test="${user.active}">
                                        <a href="${pageContext.request.contextPath}/users/toggle-status?id=${user.id}&action=deactivate"
                                           class="btn btn-danger"
                                           onclick="return confirm('Deactivate this user?')">Deactivate</a>
                                    </c:when>
                                    <c:otherwise>
                                        <a href="${pageContext.request.contextPath}/users/toggle-status?id=${user.id}&action=activate"
                                           class="btn btn-warning"
                                           onclick="return confirm('Activate this user?')">Activate</a>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty userList}">
                    <tr>
                        <td colspan="6" class="empty-state">No users found.</td>
                    </tr>
                </c:if>
            </tbody>
        </table>
    </div>
</div>

</body>
</html>
