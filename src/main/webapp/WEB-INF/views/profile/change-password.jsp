
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>Change Password</title>
</head>
<body>

<div>
    <div>HRM System</div>

    <div>
        <a href="${pageContext.request.contextPath}/home">Home</a>
        <a href="${pageContext.request.contextPath}/profile">View My Profile</a>
        <a href="${pageContext.request.contextPath}/logout">Logout</a>
    </div>
</div>

<div>
    <h2>Change Password</h2>

    <form action="${pageContext.request.contextPath}/change-password" method="post">
        <label>Old Password</label>
        <label>
            <input type="password" name="oldPassword" required>
        </label>

        <label>New Password</label>
        <label>
            <input type="password" name="newPassword" required>
        </label>

        <label>Confirm New Password</label>
        <label>
            <input type="password" name="confirmPassword" required>
        </label>

        <button type="submit">Change Password</button>
    </form>
    <c:if test="${not empty error}">
        <p class="error">${error}</p>
    </c:if>

    <%-- Hiển thị thông báo thành công nếu có --%>
    <c:if test="${not empty success}">
        <p class="success">${success}</p>
    </c:if>


</div>

</body>
</html>