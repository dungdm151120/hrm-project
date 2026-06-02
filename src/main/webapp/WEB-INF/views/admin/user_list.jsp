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
        <a href="${pageContext.request.contextPath}/admin/users/add" class="btn btn-primary">Add New User</a>
    </div>

    <div class="search-filter">
        <form action="${pageContext.request.contextPath}/user_list" method="GET" style="display: flex; gap: 0.5rem; align-items: center;">
            <input type="text" name="search" placeholder="Search name or email..." value="${oldKeyword}">

            <select name="status">
                <option value="all" ${status == 'all' || empty status ? 'selected' : ''}>All Status</option>
                <option value="true" ${status == 'true' ? 'selected' : ''}>Active</option>
                <option value="false" ${status == 'false' ? 'selected' : ''}>Inactive</option>
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
                <th>Email</th>
                <th>Role</th>
                <th>Department</th>
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
                    <td>${not empty user.departmentName ? user.departmentName : 'N/A'}</td>
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
                            <a href="${pageContext.request.contextPath}/users/update?id=${user.id}" class="btn btn-secondary">Update</a>

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
            </tbody>
        </table>
    </div>
</div>

</body>
</html>
