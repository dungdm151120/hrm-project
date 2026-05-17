<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Update Role</title>
</head>
<body>

<p><a href="${pageContext.request.contextPath}/admin/roles">Back to role list</a></p>
<h2>Update Role</h2>

<c:if test="${not empty error}">
    <p>${error}</p>
</c:if>

<c:if test="${not empty role}">
<form action="${pageContext.request.contextPath}/admin/roles/update" method="post">
    <input type="hidden" name="roleId" value="${role.id}">
    <label>Role Name: <input type="text" name="name" value="${role.name}" required></label><br>
    <label>Description: <input type="text" name="description" value="${role.description}"></label><br>
    <button type="submit">Save</button>
</form>
</c:if>

</body>
</html>
