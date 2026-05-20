<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Cập nhật thông tin Role | HRM</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body>

<div class="container">
    <a class="back-link" href="${pageContext.request.contextPath}/admin/roles">Quay lại danh sách Role</a>

    <h2 class="form-title">Cập nhật thông tin Role</h2>

    <c:if test="${not empty error}">
        <div class="alert alert-error">⚠ ${error}</div>
    </c:if>

    <c:choose>
        <c:when test="${not empty role}">
            <span class="role-id-badge">ID: ${role.id}</span>

            <form action="${pageContext.request.contextPath}/admin/roles/update" method="post">
                <input type="hidden" name="roleId" value="${role.id}">

                <div class="form-group">
                    <label for="name">Tên Role <span style="color: var(--danger);">*</span></label>
                    <input type="text" id="name" name="name"
                           value="${role.name}"
                           placeholder="Nhập tên role (VD: HR, MANAGER...)"
                           required>
                </div>

                <div class="form-group">
                    <label for="description">Mô tả</label>
                    <textarea id="description" name="description"
                              placeholder="Mô tả chức năng của role này...">${role.description}</textarea>
                </div>

                <div class="form-actions">
                    <button type="submit" class="btn-save">Lưu thay đổi</button>
                    <a href="${pageContext.request.contextPath}/admin/roles" class="btn-cancel">Hủy</a>
                </div>
            </form>
        </c:when>
        <c:otherwise>
            <p class="empty-state">
                Không tìm thấy role.
                <a href="${pageContext.request.contextPath}/admin/roles" style="color: var(--primary); font-weight: 500;">Quay lại danh sách</a>
            </p>
        </c:otherwise>
    </c:choose>
</div>

</body>
</html>