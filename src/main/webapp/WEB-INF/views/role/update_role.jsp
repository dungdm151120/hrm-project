<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Update Role | HRM</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body class="dashboard-body">

<div class="dashboard-wrapper">

    <jsp:include page="/WEB-INF/views/common/sidebar.jsp"/>

    <div class="dashboard-main">
        <div class="dashboard-header">
            <div class="header-left">
                <h1 class="header-title">Update Role</h1>
            </div>
            <div class="header-right">
                <a href="${pageContext.request.contextPath}/admin/roles" class="btn-secondary">← Back to List</a>
            </div>
        </div>

        <div class="dashboard-content">
            <c:if test="${not empty error}">
                <div class="alert alert-error">⚠ ${error}</div>
            </c:if>

            <c:choose>
                <c:when test="${not empty role}">
                    <span class="role-id-badge">ID: ${role.id}</span>

                    <form action="${pageContext.request.contextPath}/admin/roles/update" method="post">
                        <input type="hidden" name="roleId" value="${role.id}">

                        <div class="form-group">
                            <label for="name">Tên Role <span class="required-star">*</span></label>
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
                    <div class="empty-state">
                        <p>Không tìm thấy role.</p>
                        <a href="${pageContext.request.contextPath}/admin/roles" class="btn-primary">Quay lại danh sách</a>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</div>

</body>
</html>