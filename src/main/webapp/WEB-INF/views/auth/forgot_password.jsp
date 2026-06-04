<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Forgot Password | HRM</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body class="forgot-password-page">

<div class="forgot-password-wrapper">
    <div class="forgot-password-card">
        <div class="forgot-password-header">
            <div class="brand">HRM</div>
            <h2>Quên mật khẩu</h2>
        </div>

        <% if (request.getAttribute("error") != null) { %>
            <div class="alert alert-error">⚠ <%= request.getAttribute("error") %></div>
        <% } %>

        <% if (request.getAttribute("success") != null) { %>
            <div class="alert alert-success">✓ <%= request.getAttribute("success") %></div>
        <% } %>

        <form class="forgot-password-form" action="<%= request.getContextPath() %>/forgot-password" method="post">
            <div class="form-group">
                <label for="email">Email <span class="required-star">*</span></label>
                <input type="email" id="email" name="email" placeholder="Nhập email đã đăng ký" required>
            </div>

            <div class="form-group">
                <label for="reason">Lý do</label>
                <textarea id="reason" name="reason" rows="4" placeholder="Mô tả lý do cần reset mật khẩu..."></textarea>
            </div>

            <button type="submit" class="btn-login">Gửi yêu cầu đến admin</button>
        </form>

        <div class="forgot-password-footer">
            <a href="<%= request.getContextPath() %>/login">← Quay lại đăng nhập</a>
        </div>
    </div>
</div>

</body>
</html>