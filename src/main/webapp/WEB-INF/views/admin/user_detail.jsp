<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<html>
<head>
    <title>User Detail</title>
</head>
<body>

<h2>User Detail</h2>

<table>
    <tr>
        <td><strong>ID:</strong></td>
        <td>${user.id}</td>
    </tr>
    <tr>
        <td><strong>Avatar:</strong></td>
        <td><img src="${user.avatarUrl}" width="100" alt="No Avatar"></td>
    </tr>
    <tr>
        <td><strong>Full Name:</strong></td>
        <td>${user.fullName}</td>
    </tr>
    <tr>
        <td><strong>Email:</strong></td>
        <td>${user.email}</td>
    </tr>
    <tr>
        <td><strong>Phone:</strong></td>
        <td>${user.phone}</td>
    </tr>
    <tr>
        <td><strong>Address:</strong></td>
        <td>${user.address}</td>
    </tr>
    <tr>
        <td><strong>Gender:</strong></td>
        <td>${user.gender}</td>
    </tr>
    <tr>
        <td><strong>DOB:</strong></td>
        <td>${user.dateOfBirth}</td>
    </tr>
    <tr>
        <td><strong>Status:</strong></td>
        <td>${user.active ? "Active" : "Inactive"}</td>
    </tr>
    <tr>
        <td><strong>Role (Role ID):</strong></td>
        <td>${user.roleId}</td>
    </tr>
</table>

<br>
<a href="user_list">Back to user list</a>

</body>
</html>
