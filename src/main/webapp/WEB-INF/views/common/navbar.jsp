<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<nav class="navbar">
    <a href="${pageContext.request.contextPath}/home" class="navbar-brand">HRM System</a>
    <div class="navbar-links">
        <a href="${pageContext.request.contextPath}/home">Home</a>
        <a href="${pageContext.request.contextPath}/profile">Profile</a>
        <a href="${pageContext.request.contextPath}/change-password">Change Password</a>
        <a href="${pageContext.request.contextPath}/logout">Logout</a>
    </div>
</nav>
