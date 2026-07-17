<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>PIT Version Details | HRM</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body class="dashboard-body">

<div class="dashboard-wrapper">
    <jsp:include page="/WEB-INF/views/common/sidebar.jsp"/>

    <div class="dashboard-main">
        <div class="dashboard-header">
            <div class="header-left">
                <h1 class="header-title">PIT Version Details</h1>
            </div>
        </div>

        <div class="dashboard-content">
            <a class="back-link" href="${pageContext.request.contextPath}/payroll/pit/list">Back to PIT versions</a>
            <h2 class="form-title">PIT Tax Brackets List (Effective Date: <span style="color: #2563EB;">${effectiveDate}</span>)</h2>

            <c:if test="${not empty sessionScope.error}">
                <div class="alert alert-danger">${sessionScope.error}</div>
                <% session.removeAttribute("error"); %>
            </c:if>
            <c:if test="${not empty sessionScope.message}">
                <div class="alert alert-success">${sessionScope.message}</div>
                <% session.removeAttribute("message"); %>
            </c:if>

            <table class="table">
                <thead>
                    <tr>
                        <th>Level</th>
                        <th>Min Value (VND)</th>
                        <th>Max Value (VND)</th>
                        <th>Tax Rate (%)</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="b" items="${bracketList}">
                        <tr>
                            <td>${b.bracketLevel}</td>
                            <td>${b.minValue}</td>
                            <td>
                                <c:choose>
                                    <c:when test="${b.maxValue != null}">
                                        ${b.maxValue}
                                    </c:when>
                                    <c:otherwise>
                                        Infinite
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td>${b.taxRate}</td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </div>
</div>

</body>
</html>