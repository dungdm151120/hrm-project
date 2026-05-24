<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Role Permission - ${role.name} | HRM</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body>
<div class="container">
    <div class="nav">
        <a href="${pageContext.request.contextPath}/home">Trang chủ</a>
        <span style="margin:0 0.5rem; color:var(--gray-400)">/</span>
        <a href="${pageContext.request.contextPath}/admin/roles">Danh sách vai trò</a>
    </div>

    <div class="page-header">
        <h2>Role Permission: ${role.name}</h2>
    </div>

    <!-- Thông tin chi tiết role -->
    <div class="role-detail">
        <div class="role-meta">
            <span class="role-meta-label">Mô tả</span>
            <span class="role-meta-value">${role.description}</span>
        </div>
        <div class="role-meta">
            <span class="role-meta-label">Trạng thái</span>
            <span class="role-meta-value">
                <c:choose>
                    <c:when test="${role.active}">
                        <span class="badge badge-active">Active</span>
                    </c:when>
                    <c:otherwise>
                        <span class="badge badge-inactive">Vô hiệu</span>
                    </c:otherwise>
                </c:choose>
            </span>
        </div>
    </div>

    <c:if test="${not empty param.success}">
        <div class="alert alert-success">
            <span>✓</span> ${param.success}
        </div>
    </c:if>

    <a href="${pageContext.request.contextPath}/admin/roles/edit_permissions?roleId=${role.id}" class="btn-primary">
        ✎ Chỉnh sửa quyền
    </a>

    <c:choose>
        <c:when test="${not empty rolePermissions}">
            <p class="total-permission">Tổng số quyền: <span>${rolePermissions.size()}</span></p>
            <div class="table-wrapper">
                <table>
                    <thead>
                        <tr>
                            <th>STT</th>
                            <th>Mã quyền</th>
                            <th>Tên quyền</th>
                            <th>Mô tả</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="perm" items="${rolePermissions}" varStatus="s">
                            <tr>
                                <td>${s.index + 1}</td>
                                <td><code>${perm.code}</code></td>
                                <td>${perm.name}</td>
                                <td>${perm.description}</td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </c:when>
        <c:otherwise>
            <p class="empty-state">Vai trò này hiện chưa có quyền nào.</p>
        </c:otherwise>
    </c:choose>
</div>
</body>
</html>