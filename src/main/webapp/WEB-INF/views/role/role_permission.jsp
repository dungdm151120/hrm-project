<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Role Permission - ${role.name}</title>
</head>
<body>

<p><a href="${pageContext.request.contextPath}/admin/roles">Role list</a></p>

<h2>Role Permission: ${role.name}</h2>
<p>Description: ${role.description}</p>
<p>Status:
    <c:choose>
        <c:when test="${role.active}">Active</c:when>
        <c:otherwise>Deactive</c:otherwise>
    </c:choose>
</p>

<c:if test="${not empty param.success}">
    <p style="color:green;">${param.success}</p>
</c:if>

<p>
    <a href="${pageContext.request.contextPath}/admin/roles/edit_permissions?roleId=${role.id}">Edit permissions</a>
</p>

<c:choose>
    <c:when test="${not empty rolePermissions}">
        <p>Total permissions: ${rolePermissions.size()}</p>
        <table border="1" cellpadding="5" cellspacing="0">
            <thead>
                <tr>
                    <th>No</th>
                    <th>Permission (Code)</th>
                    <th>Permission (Name)</th>
                    <th>Description</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="perm" items="${rolePermissions}" varStatus="s">
                    <tr>
                        <td>${s.index + 1}</td>
                        <td>${perm.code}</td>
                        <td>${perm.name}</td>
                        <td>${perm.description}</td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </c:when>
    <c:otherwise>
        <p>This role has no permissions</p>
    </c:otherwise>
</c:choose>

</body>
</html>
