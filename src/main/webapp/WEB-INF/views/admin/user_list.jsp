<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<html>
<head>
    <title>User List</title>
</head>
<body>

<h2>User List</h2>

<table>
    <thead>
    <tr>
        <th>ID</th>
        <th>Full Name</th>
        <th>Email</th>
        <th>Role</th>
        <th>Status</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${userList}" var="user">
        <tr>
            <td>${user.id}</td>
            <td>${user.fullName}</td>
            <td>${user.email}</td>
            <td>${user.roleName}</td>
            <td>${user.active ? "Active" : "Inactive"}</td>
            <td>
                <a href="user_detail?id=${user.id}">View Detail</a>
            </td>
            <td><a href="${pageContext.request.contextPath}/users/update?id=${user.id}">Update</a></td>
            <td>
                <c:choose>
                    <c:when test="${user.active}">
                        <a href="${pageContext.request.contextPath}/users/toggle-status?id=${user.id}&action=deactivate"
                           onclick="return confirm('Deactivate this user?')">Deactivate</a>
                    </c:when>

                    <c:otherwise>
                        <a href="${pageContext.request.contextPath}/users/toggle-status?id=${user.id}&action=activate"
                           onclick="return confirm('Activate this user?')">Activate</a>
                    </c:otherwise>
                </c:choose>
            </td>
        </tr>
    </c:forEach>
    </tbody>
</table>

<br>
<a href="${pageContext.request.contextPath}/home">Home</a>

</body>
</html>
