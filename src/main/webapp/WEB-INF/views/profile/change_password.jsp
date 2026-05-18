<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>Change Password</title>
</head>
<body>

<jsp:include page="/WEB-INF/views/common/navbar.jsp" />

<div>
    <h2>Change Password</h2>

    <form action="${pageContext.request.contextPath}/change-password" method="post">
        <label>Old Password:</label><br>
        <input type="password" name="oldPassword" required><br><br>

        <label>New Password:</label><br>
        <input type="password" name="newPassword" required><br><br>

        <label>Confirm New Password:</label><br>
        <input type="password" name="confirmPassword" required><br><br>

        <button type="submit">Change Password</button>
    </form>

    <c:if test="${not empty error}">
        <p><font color="red">${error}</font></p>
    </c:if>

    <c:if test="${not empty success}">
        <p>${success}</p>
    </c:if>
</div>

</body>
</html>
