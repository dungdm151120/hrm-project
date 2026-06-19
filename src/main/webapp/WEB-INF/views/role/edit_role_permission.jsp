<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Edit role permission - ${role.name} | HRM</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body class="dashboard-body">

<div class="dashboard-wrapper">
    <jsp:include page="/WEB-INF/views/common/sidebar.jsp"/>
    <div class="dashboard-main">
        <div class="dashboard-header">
            <div class="header-left">
                <h1 class="header-title">Edit permissions: ${role.name}</h1>
            </div>
            <div class="header-right">
                <a href="${pageContext.request.contextPath}/admin/roles/permissions?roleId=${role.id}" class="btn-secondary">← Back to permissions</a>
            </div>
        </div>

        <div class="dashboard-content">
            <nav class="breadcrumb">
                <a href="${pageContext.request.contextPath}/home">Trang chủ</a>
                <span class="separator">›</span>
                <a href="${pageContext.request.contextPath}/admin/roles">Danh sách vai trò</a>
                <span class="separator">›</span>
                <a href="${pageContext.request.contextPath}/admin/roles/permissions?roleId=${role.id}">Quyền của "${role.name}"</a>
                <span class="separator">›</span>
                <span class="current">Chỉnh sửa</span>
            </nav>

            <p class="role-description">Mô tả: ${role.description}</p>

            <form action="${pageContext.request.contextPath}/admin/roles/edit_permissions" method="post">
                <input type="hidden" name="roleId" value="${role.id}">

                <div class="toolbar">
                    <button type="button" class="btn-secondary" onclick="selectAll()">Chọn tất cả</button>
                    <button type="button" class="btn-secondary" onclick="clearAll()">Bỏ chọn tất cả</button>
                    <span class="selected-count-wrapper">
                        Đã chọn: <span id="selectedCount" class="selected-count">0</span> / ${allPermissions.size()} quyền
                    </span>
                </div>

                <!-- Edit Permission Matrix -->
                <div class="permission-matrix-horizontal">
                    <c:forEach var="entry" items="${moduleMap}">
                        <c:if test="${not empty entry.value}">
                            <div class="permission-module-row">
                                <div class="module-label">
                                    <c:choose>
                                        <c:when test="${entry.key eq 'HOMEPAGE'}">Home</c:when>
                                        <c:when test="${entry.key eq 'PROFILE'}">Profile</c:when>
                                        <c:when test="${entry.key eq 'AUTH'}">Authentication</c:when>
                                        <c:when test="${entry.key eq 'USER'}">User Management</c:when>
                                        <c:when test="${entry.key eq 'ROLE'}">Role Management</c:when>
                                        <c:when test="${entry.key eq 'DEPARTMENT'}">Department</c:when>
                                        <c:when test="${entry.key eq 'POSITION'}">Position</c:when>
                                        <c:when test="${entry.key eq 'CONTRACT'}">Contract</c:when>
                                        <c:when test="${entry.key eq 'ATTENDANCE'}">Attendance</c:when>
                                        <c:when test="${entry.key eq 'PAYROLL'}">Payroll</c:when>
                                        <c:when test="${entry.key eq 'ANNOUNCEMENT'}">Announcement</c:when>
                                        <c:when test="${entry.key eq 'REQUEST'}">Request</c:when>
                                        <c:when test="${entry.key eq 'TASK'}">Task</c:when>
                                        <c:otherwise>Other</c:otherwise>
                                    </c:choose>
                                </div>
                                <div class="module-permissions">
                                    <c:forEach var="perm" items="${entry.value}">
                                        <c:set var="isAssigned" value="false"/>
                                        <c:forEach var="assignedId" items="${assignedPermissionIds}">
                                            <c:if test="${assignedId == perm.id}">
                                                <c:set var="isAssigned" value="true"/>
                                            </c:if>
                                        </c:forEach>
                                        <label class="permission-item">
                                            <input type="checkbox"
                                                   name="permissionIds"
                                                   value="${perm.id}"
                                                   class="perm-checkbox"
                                                   ${isAssigned ? 'checked' : ''}
                                                   onchange="updateCount()">
                                            <div class="permission-info">
                                                <code class="perm-code">${perm.code}</code>
                                                <span class="perm-desc">${perm.description}</span>
                                            </div>
                                        </label>
                                    </c:forEach>
                                </div>
                            </div>
                        </c:if>
                    </c:forEach>
                </div>

                <div class="form-actions">
                    <button type="submit" class="btn-save">Lưu thay đổi</button>
                    <a href="${pageContext.request.contextPath}/admin/roles/permissions?roleId=${role.id}" class="btn-cancel">Hủy</a>
                </div>
            </form>
        </div>
    </div>
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