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
    <%
        User userToUpdate = (User) request.getAttribute("userToUpdate");
    %>
    <form action="${pageContext.request.contextPath}/users/update" method="post">
        <input type="hidden" name="id" value="<%= userToUpdate.getId() %>">

        <table>
            <tr>
                <td>Full Name</td>
                <td><input type="text" name="fullName" value="<%= userToUpdate.getFullName()%>"></td>
            </tr>
            <tr>
                <td>Email</td>
                <td><input type="text" name="email" value="<%= userToUpdate.getEmail()%>"></td>
            </tr>
            <tr>
                <td>Phone</td>
                <td><input type="text" name="phone" value="<%= userToUpdate.getPhone()%>"></td>
            </tr>
            <tr>
                <td>Gender</td>
                <td>
                    <select name="gender">
                        <option value="Male" <%= "Male".equalsIgnoreCase(userToUpdate.getGender()) ? "selected" : "" %>>Male</option>
                        <option value="Female" <%= "Female".equalsIgnoreCase(userToUpdate.getGender()) ? "selected" : "" %>>Female</option>
                        <option value="Other" <%= "Other".equalsIgnoreCase(userToUpdate.getGender()) ? "selected" : "" %>>Other</option>
                    </select>
                </td>
            </tr>
            <tr>
                <td>Date of Birth</td>
                <td>
                    <%
                        String dobString = "";
                        if (userToUpdate.getDateOfBirth() != null) {
                            dobString = userToUpdate.getDateOfBirth().toLocalDate().toString();
                        }
                    %>
                    <input type="date" name="dateOfBirth" value="<%= dobString %>">
                </td>
            </tr>
            <tr>
                <td>Address</td>
                <td><input type="text" name="address" value="<%= userToUpdate.getAddress()%>"></td>
            </tr>
            <tr>
                <td>Avatar Url</td>
                <td><input type="text" name="avatarUrl" value="<%= userToUpdate.getAvatarUrl()%>"></td>
            </tr>
            <tr>
                <td>Role Id</td>
                <td><input type="text" name="roleId" value="<%= userToUpdate.getRoleId()%>" required></td>
            </tr>
            <tr>
                <td>Active Status</td>
                <td>
                    <input type="radio" name="active" value="true" <%= userToUpdate.isActive() ? "checked" : "" %>> Active
                    <input type="radio" name="active" value="false" <%= !userToUpdate.isActive() ? "checked" : "" %>> Inactive
                </td>
            </tr>
            <tr>
                <td><input type="submit" value="Update Info"></td>
                <td><a href="${pageContext.request.contextPath}/admin/users">Cancel</a></td>
            </tr>
        </table>
    </form>
</body>
</html>
