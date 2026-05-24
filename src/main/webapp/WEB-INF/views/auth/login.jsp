<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đăng nhập | HRM</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body class="login-page">

<div class="login-wrapper">
    <div class="login-card">
        <div class="login-header">
            <div class="brand">HRM</div>
            <h2>Đăng nhập</h2>
        </div>

        <% if (request.getAttribute("error") != null) { %>
            <div class="alert alert-error">
                <span>⚠</span> <%= request.getAttribute("error") %>
            </div>
        <% } %>

        <form class="login-form" action="<%= request.getContextPath() %>/login" method="post">
            <div class="form-group">
                <label for="email">Email</label>
                <input type="email" id="email" name="email" placeholder="Nhập địa chỉ email" required>
            </div>

            <div class="form-group">
                <label for="password">Mật khẩu</label>
                <input type="password" id="password" name="password" placeholder="Nhập mật khẩu" required>
            </div>

            <button type="submit" class="btn-login">Đăng nhập</button>
        </form>

        <div class="login-footer">
            <a href="<%= request.getContextPath() %>/forgot-password">Quên mật khẩu?</a>
        </div>
    </div>
</div>

</body>
</html>