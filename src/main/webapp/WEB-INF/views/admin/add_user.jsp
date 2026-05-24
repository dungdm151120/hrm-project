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
    <a class="back-link" href="${pageContext.request.contextPath}/user_list">← Quay lại danh sách người dùng</a>
    <h2 class="form-title">Thêm người dùng mới</h2>

    <c:if test="${not empty error}">
        <div>${error}</div>
    </c:if>

    <form action="${pageContext.request.contextPath}/admin/users/add" method="post">
        <div class="form-group">
            <label for="fullName">Full Name <span style="color: var(--danger);">*</span></label>
            <input type="text" id="fullName" name="fullName" placeholder="Nhập họ tên" required>
        </div>

        <div class="form-group">
            <label for="email">Email <span style="color: var(--danger);">*</span></label>
            <input type="email" id="email" name="email" placeholder="Nhập địa chỉ email" required>
        </div>

        <div class="form-group">
            <label for="password">Password <span style="color: var(--danger);">*</span></label>
            <input type="password" id="password" name="password" placeholder="Nhập mật khẩu" required>
        </div>

        <div class="form-group">
            <label for="phone">Phone</label>
            <input type="text" id="phone" name="phone" placeholder="Nhập số điện thoại">
        </div>

        <div class="form-group">
            <label for="gender">Gender</label>
            <select id="gender" name="gender" class="form-select">
                <option value="Male">Male</option>
                <option value="Female">Female</option>
                <option value="Other">Other</option>
            </select>
        </div>

        <div class="form-group">
            <label for="dateOfBirth">Date of Birth</label>
            <input type="date" id="dateOfBirth" name="dateOfBirth">
        </div>

        <div class="form-group">
            <label for="address">Address</label>
            <input type="text" id="address" name="address" placeholder="Nhập địa chỉ">
        </div>

        <div class="form-group">
            <label for="avatarUrl">Avatar URL</label>
            <input type="text" id="avatarUrl" name="avatarUrl" placeholder="Nhập đường dẫn ảnh đại diện">
        </div>

        <div class="form-group">
            <label for="roleId">Role ID <span style="color: var(--danger);">*</span></label>
            <input type="text" id="roleId" name="roleId" placeholder="Nhập ID vai trò" required>
        </div>

        <div class="form-group">
            <label>Active Status</label>
            <div class="radio-group">
                <label class="radio-label">
                    <input type="radio" name="active" value="true" checked>
                    <span>Active</span>
                </label>
                <label class="radio-label">
                    <input type="radio" name="active" value="false">
                    <span>Inactive</span>
                </label>
            </div>
        </div>

        <div class="form-actions">
            <button type="submit" class="btn-save">Thêm người dùng</button>
            <a href="${pageContext.request.contextPath}/user_list" class="btn-cancel">Hủy</a>
        </div>
    </form>
</div>

</body>
</html>