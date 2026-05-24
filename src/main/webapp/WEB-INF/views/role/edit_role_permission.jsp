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
<body>

<div class="container">
    <!-- Breadcrumb -->
    <nav class="breadcrumb">
        <a href="${pageContext.request.contextPath}/home">Trang chủ</a>
        <span class="separator">›</span>
        <a href="${pageContext.request.contextPath}/admin/roles">Danh sách vai trò</a>
        <span class="separator">›</span>
        <a href="${pageContext.request.contextPath}/admin/roles/permissions?roleId=${role.id}">Quyền của "${role.name}"</a>
        <span class="separator">›</span>
        <span class="current">Chỉnh sửa</span>
    </nav>

    <h2 class="form-title">Chỉnh sửa quyền: ${role.name}</h2>
    <p class="role-description">Mô tả: ${role.description}</p>

    <form action="${pageContext.request.contextPath}/admin/roles/edit_permissions" method="post">
        <input type="hidden" name="roleId" value="${role.id}">

        <!-- Toolbar -->
        <div class="toolbar">
            <button type="button" class="btn-secondary" onclick="selectAll()">Chọn tất cả</button>
            <button type="button" class="btn-secondary" onclick="clearAll()">Bỏ chọn tất cả</button>
            <span class="selected-count-wrapper">
                Đã chọn: <span id="selectedCount" class="selected-count">0</span> / ${allPermissions.size()} quyền
            </span>
        </div>

        <div class="table-wrapper">
            <table>
                <thead>
                    <tr>
                        <th style="width: 60px;">Chọn</th>
                        <th>Mã quyền</th>
                        <th>Tên quyền</th>
                        <th>Mô tả</th>
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

        <div class="form-actions">
            <button type="submit" class="btn-save">Lưu thay đổi</button>
            <a href="${pageContext.request.contextPath}/admin/roles/permissions?roleId=${role.id}" class="btn-cancel">Hủy</a>
        </div>
    </form>
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