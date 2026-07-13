<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.time.LocalDate" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%
    int currentYear = LocalDate.now().getYear();
    int currentMonth = LocalDate.now().getMonthValue();
    request.setAttribute("currentYear", currentYear);
    request.setAttribute("currentMonth", currentMonth);
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Payroll Components | HRM</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body class="dashboard-body">

<div class="dashboard-wrapper">

    <jsp:include page="/WEB-INF/views/common/sidebar.jsp"/>

    <div class="dashboard-main">

        <div class="dashboard-header">
            <div class="header-left">
                <h1 class="header-title">Payroll Setting</h1>
            </div>
        </div>

        <div class="dashboard-content">
            <c:if test="${not empty error}">
                <div class="alert alert-error">${error}</div>
            </c:if>

            <c:if test="${not empty success}">
                <div class="alert alert-success">${success}</div>
            </c:if>

            <div class="settings-grid" style="display: flex; justify-content: space-between">
                <div class="setting-card">
                    <div>
                        <h2 class="setting-card-title">PIT Bracket</h2>
                        <p class="setting-card-desc">Update personal interest brackets</p>
                    </div>
                    <a href="${pageContext.request.contextPath}/payroll/pit/list" class="btn-save">
                        Update PIT Bracket
                    </a>
                </div>

                <div class="setting-card">
                    <div>
                        <h2 class="setting-card-title">Insurance Rate</h2>
                        <p class="setting-card-desc">Adjust insurance for both employee and company, self deductions, union rate, etc</p>
                    </div>
                    <a href="${pageContext.request.contextPath}/payroll/setting/list" class="btn-save">
                        Update Insurance Rate
                    </a>
                </div>
            </div>
        </div>
    </div>
</div>

</body>
</html>