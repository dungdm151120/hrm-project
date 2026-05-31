<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<nav class="navbar">
    <a href="${pageContext.request.contextPath}/home" class="navbar-brand">HRM System</a>
    <div class="navbar-links">
        <c:set var="showContracts" value="false" />
        <c:forEach var="permission" items="${sessionScope.userPermissions}">
            <c:if test="${permission == 'CONTRACT_VIEW_OWN' || permission == 'CONTRACT_VIEW_LIST'}">
                <c:set var="showContracts" value="true" />
            </c:if>
        </c:forEach>
        <a href="${pageContext.request.contextPath}/home">Home</a>
        <a href="${pageContext.request.contextPath}/profile">Profile</a>
        <c:if test="${showContracts}">
            <a href="${pageContext.request.contextPath}/contracts">Contracts</a>
        </c:if>
        <a href="${pageContext.request.contextPath}/change-password">Change Password</a>
        <a href="${pageContext.request.contextPath}/logout">Logout</a>
    </div>
</nav>
