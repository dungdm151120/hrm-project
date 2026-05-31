<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Add New User | HRM</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body>

<jsp:include page="/WEB-INF/views/common/navbar.jsp" />

<div class="container" style="margin-top: 2rem;">
    <a class="back-link" href="${pageContext.request.contextPath}/position/list">← Quay lại danh sách vị trí</a>
    <h2 class="form-title">Thêm vị trí mới</h2>

    <c:if test="${not empty error}">
        <div style="color: red; margin-bottom: 15px; font-weight: bold;">
            ${error}
        </div>
    </c:if>

    <form action="${pageContext.request.contextPath}/position/add" method="post">
        <div class="form-group">
            <label for="name">Name <span style="color: var(--danger);">*</span></label>
            <input type="text" id="name" name="name" placeholder="Nhập tên vị trí" value="${newPosition.name}" required>
        </div>

        <div class="form-group">
            <label for="description">Description</label>
            <textarea id="description" name="description" placeholder="Nhập mô tả" >${newPosition.description}</textarea>
        </div>

        <div class="form-group">
            <label>Active Status</label>
            <div class="radio-group">
                <label class="radio-label">
                    <input type="radio" name="active" value="true" ${newPosition == null || newPosition.active ? 'checked' : ''}>
                    <span>Active</span>
                </label>
                <label class="radio-label">
                    <input type="radio" name="active" value="false" ${newPosition != null && !newPosition.active ? 'checked' : ''}>
                    <span>Inactive</span>
                </label>
            </div>
        </div>

        <div class="form-actions">
            <button type="submit" class="btn-save">Add position</button>
            <a href="${pageContext.request.contextPath}/position/list" class="btn-cancel">Hủy</a>
        </div>
    </form>
</div>

</body>
</html>