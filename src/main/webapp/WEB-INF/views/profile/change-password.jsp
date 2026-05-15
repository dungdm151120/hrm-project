<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Change Password</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body>

<div class="navbar">
    <div class="logo">HRM System</div>

    <div class="nav-right">
        <a href="${pageContext.request.contextPath}/home">Home</a>
        <a href="${pageContext.request.contextPath}/profile">View My Profile</a>
        <a href="${pageContext.request.contextPath}/logout">Logout</a>
    </div>
</div>

<div class="form-container">
    <h2>Change Password</h2>

    <form action="${pageContext.request.contextPath}/change-password" method="post">
        <label>Old Password</label>
        <input type="password" name="oldPassword" required>

        <label>New Password</label>
        <input type="password" name="newPassword" required>

        <label>Confirm New Password</label>
        <input type="password" name="confirmPassword" required>

        <button type="submit">Change Password</button>
    </form>

    <p class="error">${error}</p>
    <p class="success">${success}</p>
</div>

</body>
</html>