<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Change Password | HRM</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body class="dashboard-body">

<div class="dashboard-wrapper">

    <jsp:include page="/WEB-INF/views/common/sidebar.jsp"/>

    <div class="dashboard-main">
        <div class="dashboard-header">
            <div class="header-left">
                <h1 class="header-title">Change Password</h1>
            </div>
        </div>

        <div class="dashboard-content">
            <c:if test="${not empty error}">
                <div class="alert alert-error">${error}</div>
            </c:if>
            <c:if test="${not empty success}">
                <div class="alert alert-success">✓ ${success}</div>
            </c:if>

            <div class="detail-wrapper">
                <form action="${pageContext.request.contextPath}/change-password" method="post">
                    <div class="form-group">
                        <label for="oldPassword">Old Password <span class="required-star">*</span></label>
                        <input type="password" id="oldPassword" name="oldPassword" placeholder="Nhập mật khẩu cũ" required>
                    </div>

                    <div class="form-group">
                        <label for="newPassword">New Password <span class="required-star">*</span></label>
                        <input type="password" id="newPassword" name="newPassword" placeholder="Nhập mật khẩu mới" required>
                    </div>

                    <div class="form-group">
                        <label for="confirmPassword">Confirm New Password <span class="required-star">*</span></label>
                        <input type="password" id="confirmPassword" name="confirmPassword" placeholder="Xác nhận mật khẩu mới" required>
                    </div>

                    <div class="form-actions">
                        <button type="submit" class="btn-save">Change Password</button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

</body>
</html>