<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>List Of Roles</title>
</head>
<body>

<h2>List Of Roles</h2>

<c:if test="${not empty param.success}">
    <p>${param.success}</p>
</c:if>
<c:if test="${not empty param.error}">
    <p>${param.error}</p>
</c:if>

<table border="1" cellpadding="5" cellspacing="0">
    <thead>
        <tr>
            <th>ID</th>
            <th>Role name</th>
            <th>Description</th>
            <th>Status</th>
            <th>Inspect</th>
        </tr>
    </thead>
    <tbody>
        <c:forEach var="role" items="${roles}" varStatus="s">
            <tr>
                <td>${s.index + 1}</td>
                <td>${role.name}</td>
                <td>${role.description}</td>
                <td>
                    <c:choose>
                        <c:when test="${role.active}">Active</c:when>
                        <c:otherwise>Vo hieu</c:otherwise>
                    </c:choose>
                </td>
                <td>
                    <a href="${pageContext.request.contextPath}/admin/roles/permissions?roleId=${role.id}">Inspect Role</a>
                    |
                    <a href="${pageContext.request.contextPath}/admin/roles/edit-permissions?roleId=${role.id}">Edit role</a>
                    |
                    <form action="${pageContext.request.contextPath}/admin/roles/toggle-status" method="post" style="display:inline;">
                        <input type="hidden" name="roleId" value="${role.id}">
                        <button type="submit"
                                onclick="return confirm('Are you sure?')">
                            <c:choose>
                                <c:when test="${role.active}">Deactivate</c:when>
                                <c:otherwise>Kich hoat</c:otherwise>
                            </c:choose>
                        </button>
                    </form>
                </td>
            </tr>
        </c:forEach>
        <c:if test="${empty roles}">
            <tr>
                <td colspan="5">There are no role</td>
            </tr>
        </c:if>
    </tbody>
</table>

</body>
</html>
