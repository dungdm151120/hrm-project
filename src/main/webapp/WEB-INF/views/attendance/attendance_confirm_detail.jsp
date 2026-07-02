<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Attendance Confirmed Detail | HRM</title>
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
                <h1 class="header-title"> Confirmed Detail - <fmt:formatNumber value="${month}" minIntegerDigits="2"/>/${year}</h1>

            </div>
            <div class="header-right">
                 <!-- Export button placeholder as requested -->
                 <a href="#" class="btn-export" title="Export feature coming soon" onclick="alert('Export Excel functionality will be implemented soon.'); return false;">
                     📤 Export Excel
                 </a>
            </div>
        </header>

        <div class="dashboard-content">
            <div class="detail-header">
                <a href="${pageContext.request.contextPath}/attendance/confirm-list" class="btn-back">🔙 Back to List</a>
            </div>

            <!-- Summary Cards -->
            <div class="summary-cards">
                <div class="summary-card">
                    <h3>Total Employees</h3>
                    <div class="value">${overview.totalEmployees}</div>
                </div>
                <div class="summary-card">
                    <h3>Total Work Days</h3>
                    <div class="value">${overview.totalWorkDays}</div>
                </div>
                <div class="summary-card">
                    <h3>Total Work Hours</h3>
                    <div class="value"><fmt:formatNumber value="${overview.totalWorkHours}" maxFractionDigits="2"/> h</div>
                </div>
                <div class="summary-card">
                    <h3>Total OT Hours</h3>
                    <div class="value"><fmt:formatNumber value="${overview.totalOvertimeHours}" maxFractionDigits="2"/> h</div>
                </div>
            </div>

            <!-- Toolbar & Search -->
            <div class="toolbar">
                <form id="searchForm" action="${pageContext.request.contextPath}/attendance/confirm-detail" method="get">
                    <input type="hidden" name="month" value="${month}">
                    <input type="hidden" name="year" value="${year}">
                    <input type="text" name="search" class="search-box" placeholder="Search Employee Code or Name" value="${searchQuery}">
                    <button type="submit" class="search-btn">Search</button>
                </form>
            </div>

            <!-- Data Table -->
            <table class="data-table">
                <thead>
                    <tr>
                        <th>Emp Code</th>
                        <th>Full Name</th>
                        <th>Department</th>
                        <th>Work Days</th>
                        <th>Total Hours</th>
                        <th>OT Hours</th>
                    </tr>
                </thead>
                <tbody>
                    <c:choose>
                        <c:when test="${not empty details}">
                            <c:forEach var="emp" items="${details}">
                                <tr>
                                    <td>${emp.employeeCode}</td>
                                    <td>${emp.employeeName}</td>
                                    <td>${emp.departmentName != null ? emp.departmentName : 'N/A'}</td>
                                    <td>${emp.workDays}</td>
                                    <td><fmt:formatNumber value="${emp.totalHours}" maxFractionDigits="2"/></td>
                                    <td><fmt:formatNumber value="${emp.overtimeHours}" maxFractionDigits="2"/></td>
                                </tr>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <tr>
                                <td colspan="6" class="empty-table-msg">No attendance data found for this month.</td>
                            </tr>
                        </c:otherwise>
                    </c:choose>
                </tbody>
            </table>

            <!-- Pagination -->
            <c:if test="${totalPages > 1}">
                <div class="pagination">
                    <c:if test="${currentPage > 1}">
                        <a href="?month=${month}&year=${year}&search=${searchQuery}&page=${currentPage - 1}" class="page-item">◀</a>
                    </c:if>
                    
                    <c:forEach var="i" begin="1" end="${totalPages}">
                        <a href="?month=${month}&year=${year}&search=${searchQuery}&page=${i}" class="page-item ${i == currentPage ? 'active' : ''}">${i}</a>
                    </c:forEach>
                    
                    <c:if test="${currentPage < totalPages}">
                        <a href="?month=${month}&year=${year}&search=${searchQuery}&page=${currentPage + 1}" class="page-item">▶</a>
                    </c:if>
                </div>
            </c:if>
        </div>
    </main>
</div>
</body>
</html>
