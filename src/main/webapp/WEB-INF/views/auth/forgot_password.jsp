<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Forgot Password | HRM</title>
    <!-- Font Inter -->
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
    <!-- CSS chung -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body class="login-page">

<div class="login-wrapper">
    <div class="login-card">
        <div class="login-header">
            <div class="brand">HRM</div>
            <h2>Quên mật khẩu</h2>
        </div>

        <% if (request.getAttribute("error") != null) { %>
            <div class="alert alert-error">
                <span>⚠</span> <%= request.getAttribute("error") %>
            </div>
        <% } %>

        <% if (request.getAttribute("success") != null) { %>
            <div class="alert alert-success">
                <span>✓</span> <%= request.getAttribute("success") %>
            </div>
        <% } %>

        <form class="login-form" action="<%= request.getContextPath() %>/forgot-password" method="post">
            <div class="form-group">
                <label for="email">Email <span style="color: var(--danger);">*</span></label>
                <input type="email" id="email" name="email" placeholder="Nhập email đã đăng ký" required>
            </div>

            <div class="form-group">
                <label for="reason">Lý do</label>
                <textarea id="reason" name="reason" rows="4" placeholder="Mô tả lý do cần reset mật khẩu..."></textarea>
            </div>

            <button type="submit" class="btn-login">Gửi yêu cầu đến admin</button>
        </form>

        <div class="login-footer">
            <a href="<%= request.getContextPath() %>/login">← Quay lại đăng nhập</a>
        </div>
    </div>
</div>

</body>
</html>