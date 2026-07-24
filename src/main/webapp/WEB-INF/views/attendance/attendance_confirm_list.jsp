<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Attendance Confirmed List | HRM</title>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/attendance.css">
</head>
<body class="dashboard-body">
<div class="dashboard-wrapper">
    <jsp:include page="/WEB-INF/views/common/sidebar.jsp"/>

    <main class="dashboard-main">
        <header class="dashboard-header">
            <div class="header-left">
                <h1 class="header-title">Attendance Confirmed List</h1>
                <a href="${pageContext.request.contextPath}/attendance/confirm" class="btn-back">Back to Confirm Attendance</a>
            </div>
        </header>

        <div class="dashboard-content">
            <div class="filter-bar">
                <form id="filterForm" action="${pageContext.request.contextPath}/attendance/confirm_list" method="get">
                    <label for="yearSelect">Year: </label>
                    <select id="yearSelect" name="year" class="year-select">
                        <c:forEach var="y" begin="${currentYear - 5}" end="${currentYear}">
                            <option value="${y}" ${y == selectedYear ? 'selected' : ''}>${y}</option>
                        </c:forEach>
                    </select>
                </form>
            </div>

            <table class="data-table">
                <thead>
                    <tr>
                        <th>Month / Year</th>
                        <th>Confirmed Date</th>
                        <th>Confirmed By</th>
                        <th>Employee Count</th>
                        <th>Total Hours</th>
                        <th>Action</th>
                    </tr>
                </thead>
                <tbody>
                    <c:choose>
                        <c:when test="${not empty confirmedList}">
                            <c:forEach var="item" items="${confirmedList}">
                                <tr>
                                    <td><strong><fmt:formatNumber value="${item.month}" minIntegerDigits="2"/> / ${item.year}</strong></td>
                                    <td><fmt:formatDate value="${item.confirmedAt}" pattern="dd/MM/yyyy HH:mm"/></td>
                                    <td>${item.confirmedBy}</td>
                                    <td>${item.employeeCount}</td>
                                    <td><fmt:formatNumber value="${item.totalHours}" maxFractionDigits="2"/> h</td>
                                    <td>
                                        <a href="${pageContext.request.contextPath}/attendance/confirm_detail?month=${item.month}&year=${item.year}" class="btn-view">📊 View Detail</a>
                                    </td>
                                </tr>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <tr>
                                <td colspan="6" class="empty-table-msg">No confirmed attendance data found for this year.</td>
                            </tr>
                        </c:otherwise>
                    </c:choose>
                </tbody>
            </table>
        </div>
    </main>
</div>
<script>
    document.getElementById('yearSelect').addEventListener('change', function() {
        document.getElementById('filterForm').submit();
    });
</script>
</body>
</html>
