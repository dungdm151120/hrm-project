<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:choose>
    <c:when test="${empty availableEmployees}">
        <p class="overtime-empty-employees">No available employees for this date.</p>
    </c:when>
    <c:otherwise>
        <c:forEach var="emp" items="${availableEmployees}">
            <label class="overtime-employee-option">
                <input type="checkbox" name="employeeIds" value="${emp.id}">
                <c:out value="${emp.fullName}"/> - <c:out value="${emp.positionName}"/>
            </label>
        </c:forEach>
    </c:otherwise>
</c:choose>
