<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Edit Role Permissions - ${role.name} | HRM</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body class="dashboard-page">
<div class="dashboard-layout">
    <jsp:include page="/WEB-INF/views/common/sidebar.jsp" />

    <main class="main-content">
        <header class="main-header">
            <h1 class="header-title">Edit Permissions: ${role.name}</h1>
            <div class="header-right">
                <a href="${pageContext.request.contextPath}/admin/roles/permissions?roleId=${role.id}" class="btn btn-secondary">← Back</a>
            </div>
        </header>

        <div class="dashboard-content">
            <nav class="breadcrumb">
                <a href="${pageContext.request.contextPath}/home">Home</a>
                <span class="separator">›</span>
                <a href="${pageContext.request.contextPath}/admin/roles">Roles</a>
                <span class="separator">›</span>
                <a href="${pageContext.request.contextPath}/admin/roles/permissions?roleId=${role.id}">Permissions of "${role.name}"</a>
                <span class="separator">›</span>
                <span class="current">Edit</span>
            </nav>

            <div class="role-id-badge" style="margin-bottom: 1rem;">Role: ${role.name}</div>
            <p class="role-description">Description: ${role.description}</p>

            <form action="${pageContext.request.contextPath}/admin/roles/edit_permissions" method="post">
                <input type="hidden" name="roleId" value="${role.id}">

                <div class="toolbar">
                    <button type="button" class="btn-secondary" onclick="selectAll()">Select All</button>
                    <button type="button" class="btn-secondary" onclick="clearAll()">Deselect All</button>
                    <span class="selected-count-wrapper">
                        Selected: <span id="selectedCount" class="selected-count">0</span> / ${allPermissions.size()} permissions
                    </span>
                </div>

                <div class="table-wrapper">
                    <table>
                        <thead>
                            <tr>
                                <th style="width: 60px;">Select</th>
                                <th>Code</th>
                                <th>Name</th>
                                <th>Description</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="perm" items="${allPermissions}">
                                <c:set var="isAssigned" value="false"/>
                                <c:forEach var="assignedId" items="${assignedPermissionIds}">
                                    <c:if test="${assignedId == perm.id}">
                                        <c:set var="isAssigned" value="true"/>
                                    </c:if>
                                </c:forEach>
                                <tr>
                                    <td style="text-align: center;">
                                        <input type="checkbox"
                                               name="permissionIds"
                                               value="${perm.id}"
                                               class="perm-checkbox"
                                               ${isAssigned ? 'checked' : ''}>
                                    </td>
                                    <td><code>${perm.code}</code></td>
                                    <td>${perm.name}</td>
                                    <td>${perm.description}</td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>

                <div class="form-actions" style="margin-top: 1.5rem;">
                    <button type="submit" class="btn-save">Save Changes</button>
                    <a href="${pageContext.request.contextPath}/admin/roles/permissions?roleId=${role.id}" class="btn-cancel">Cancel</a>
                </div>
            </form>
        </div>
    </main>
</div>

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