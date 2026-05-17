<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Edit role permission - ${role.name}</title>
</head>
<body>

<p><a href="${pageContext.request.contextPath}/home">Home</a>
    <a href="${pageContext.request.contextPath}/admin/roles">List Of Roles</a>
    &gt;
    <a href="${pageContext.request.contextPath}/admin/roles/permissions?roleId=${role.id}">Permission of "${role.name}"</a>
    &gt; Edit
</p>

<h2>Edit role permission: ${role.name}</h2>
<p>Mo ta: ${role.description}</p>

<form action="${pageContext.request.contextPath}/admin/roles/edit_permissions" method="post">
    <input type="hidden" name="roleId" value="${role.id}">

    <p>
        <button type="button" onclick="selectAll()">Select All</button>
        <button type="button" onclick="clearAll()">Deselect All</button>
        Select: <span id="selectedCount">0</span> / ${allPermissions.size()} permission
    </p>

    <table border="1" cellpadding="5" cellspacing="0">
        <thead>
            <tr>
                <th>Select</th>
                <th>Permission (Code)</th>
                <th>Permission (Name)</th>
                <th>Description</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="perm" items="${allPermissions}">
                <%-- Kiem tra xem permission nay da duoc gan chua --%>
                <c:set var="isAssigned" value="false"/>
                <c:forEach var="assignedId" items="${assignedPermissionIds}">
                    <c:if test="${assignedId == perm.id}">
                        <c:set var="isAssigned" value="true"/>
                    </c:if>
                </c:forEach>
                <tr>
                    <td>
                        <input type="checkbox"
                               name="permissionIds"
                               value="${perm.id}"
                               class="perm-checkbox"
                               ${isAssigned ? 'checked' : ''}>
                    </td>
                    <td>${perm.code}</td>
                    <td>${perm.name}</td>
                    <td>${perm.description}</td>
                </tr>
            </c:forEach>
        </tbody>
    </table>

    <br>
    <button type="submit">Save Changes</button>
    <a href="${pageContext.request.contextPath}/admin/roles/permissions?roleId=${role.id}">Huy</a>
</form>

<script>
    function updateCount() {
        var checked = document.querySelectorAll('.perm-checkbox:checked').length;
        document.getElementById('selectedCount').textContent = checked;
    }
    function selectAll() {
        document.querySelectorAll('.perm-checkbox').forEach(function(cb) { cb.checked = true; });
        updateCount();
    }
    function clearAll() {
        document.querySelectorAll('.perm-checkbox').forEach(function(cb) { cb.checked = false; });
        updateCount();
    }
    document.querySelectorAll('.perm-checkbox').forEach(function(cb) {
        cb.addEventListener('change', updateCount);
    });
    updateCount();
</script>

</body>
</html>
