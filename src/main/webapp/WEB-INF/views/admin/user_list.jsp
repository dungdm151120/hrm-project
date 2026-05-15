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
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${userList}" var="user">
        <tr>
            <td>${user.id}</td>
            <td>${user.fullName}</td>
            <td>
            <td>
                <a href="user_detail?id=${user.id}">View</a>
            </td>
            </td>
        </tr>
    </c:forEach>
    </tbody>
</table>

<br>
<a href="index.jsp">Back to homepage</a>

</body>
</html>
