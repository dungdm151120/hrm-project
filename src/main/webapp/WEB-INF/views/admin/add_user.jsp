<%--
  Created by IntelliJ IDEA.
  User: Admin
  Date: 5/14/2026
  Time: 10:25 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.User" %>

<html>
<head>
    <title>Title</title>
</head>
<body>
    <form action="${pageContext.request.contextPath}/admin/users/add" method="post">
        <table>
            <tr>
                <td>Full Name</td>
                <td><input type="text" name="fullName"></td>
            </tr>
            <tr>
                <td>Email</td>
                <td><input type="text" name="email"></td>
            </tr>
            <tr>
                <td>Password</td>
                <td><input type="password" name="password" required></td>
            </tr>
            <tr>
                <td>Phone</td>
                <td><input type="text" name="phone"></td>
            </tr>
            <tr>
                <td>Gender</td>
                <td>
                    <select name="gender">
                        <option value="Male">Male</option>
                        <option value="Female">Female</option>
                        <option value="Other">Other</option>
                    </select>
                </td>
            </tr>
            <tr>
                <td>Date of Birth</td>
                <td><input type="date" name="dateOfBirth"></td>
            </tr>
            <tr>
                <td>Address</td>
                <td><input type="text" name="address"></td>
            </tr>
            <tr>
                <td>Avatar Url</td>
                <td><input type="text" name="avatarUrl"></td>
            </tr>
            <tr>
                <td>Role Id</td>
                <td><input type="text" name="roleId" required></td>
            </tr>
            <tr>
                <td>Active Status</td>
                <td>
                    <input type="radio" name="active" value="true" checked> Active
                    <input type="radio" name="active" value="false"> Inactive
                </td>
            </tr>
            <tr>
                <td><input type="submit" value="Add User"></td>
                <td><a href="${pageContext.request.contextPath}/admin/users">Cancel</a></td>
            </tr>
        </table>
    </form>
</body>
</html>
