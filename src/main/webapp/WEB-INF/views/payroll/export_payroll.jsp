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
    <title>Export Payrolls | HRM</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body class="dashboard-body">

<div class="dashboard-wrapper">

    <jsp:include page="/WEB-INF/views/common/sidebar.jsp"/>

    <div class="dashboard-main">
        <div class="dashboard-header">
            <div class="header-left">
                <h1 class="header-title">Export Payrolls</h1>
            </div>
        </div>

        <div class="dashboard-content">
            <a class="back-link" href="${pageContext.request.contextPath}/payroll/department">Return to payroll department list</a>

            <c:if test="${not empty error}">
                <div class="alert alert-error">${error}</div>
            </c:if>

            <c:if test="${not empty success}">
                <div class="alert alert-success">${success}</div>
            </c:if>

            <form action="${pageContext.request.contextPath}/payroll/export" method="post">
                <div class="form-group">
                    <label for="departmentId">Department <span class="required-star">*</span></label>
                    <select id="departmentId" name="departmentId" required>
                        <option value="0" ${param.departmentId == '0' ? 'selected' : ''}>-- All Departments (Whole Company) --</option>
                        <c:forEach items="${departments}" var="dept">
                            <option value="${dept.id}" ${dept.id == param.departmentId ? 'selected' : ''}>
                                ${dept.name}
                            </option>
                        </c:forEach>
                    </select>
                </div>

                <div class="form-row" style="display: flex; gap: 20px;">
                    <div class="form-group" style="flex: 1;">
                        <label for="month">Payroll Month <span class="required-star">*</span></label>
                        <c:set var="monthNames" value="${fn:split('January,February,March,April,May,June,July,August,September,October,November,December', ',')}" />

                        <select name="month" required>
                            <c:forEach var="m" begin="1" end="12">
                                <option value="${m}" ${month == m ? 'selected' : ''}>${monthNames[m - 1]}</option>
                            </c:forEach>
                        </select>
                    </div>

                    <div class="form-group" style="flex: 1;">
                        <label for="year">Payroll Year <span class="required-star">*</span></label>
                        <select id="year" name="year" required>
                            <c:forEach var="y" begin="${currentYear - 1}" end="${currentYear + 1}">
                                <c:set var="yearSelected" value="${not empty param.year ? param.year : currentYear}" />
                                <option value="${y}" ${y == yearSelected ? 'selected' : ''}>
                                    Year ${y}
                                </option>
                            </c:forEach>
                        </select>
                    </div>
                </div>

                <div class="form-actions">
                    <button type="submit" class="btn-save" onclick="return confirm('Are you sure you want to export payrolls of this department for the selected period?');">Export Payrolls</button>
                </div>
            </form>
        </div>
    </div>
</div>

</body>
</html>