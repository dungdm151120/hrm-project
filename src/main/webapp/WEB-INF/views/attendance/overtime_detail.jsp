<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Overtime Detail | HRM</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
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
               <h1 class="header-title">Overtime Detail</h1>
            </div>
            <div class="header-right">
                <c:choose>
                    <c:when test="${detail.userId == sessionScope.currentUser.id}">
                        <c:url var="backUrl" value="/attendance/my">
                            <c:param name="month" value="${detail.overtimeDate.monthValue}" />
                            <c:param name="year" value="${detail.overtimeDate.year}" />
                        </c:url>
                    </c:when>
                    <c:otherwise>
                        <c:url var="backUrl" value="/attendance/employee">
                            <c:param name="userId" value="${detail.userId}" />
                            <c:param name="month" value="${detail.overtimeDate.monthValue}" />
                            <c:param name="year" value="${detail.overtimeDate.year}" />
                        </c:url>
                    </c:otherwise>
                </c:choose>
                <a href="${backUrl}" class="btn-back">
                    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="19" y1="12" x2="5" y2="12"></line><polyline points="12 19 5 12 12 5"></polyline></svg>
                    Back to Attendance
                </a>
            </div>
        </header>

        <div class="dashboard-content">
            <div class="ot-detail-container">
                <h2 style="margin-bottom: 20px; font-size: 18px; color: #0f172a;">${detail.userFullName} (${detail.employeeCode})</h2>
                
                <div class="ot-info-grid">
                    <div class="ot-info-item">
                        <span class="ot-info-label">Overtime Date</span>
                        <span class="ot-info-value"><fmt:parseDate value="${detail.overtimeDate}" pattern="yyyy-MM-dd" var="parsedDate" type="date" />
                        <fmt:formatDate value="${parsedDate}" type="date" pattern="MMM dd, yyyy" /></span>
                    </div>
                    <div class="ot-info-item">
                        <span class="ot-info-label">Shift Duration</span>
                        <span class="ot-info-value">${detail.shiftStart} - ${detail.shiftEnd}</span>
                    </div>
                    <div class="ot-info-item">
                        <span class="ot-info-label">Request Status</span>
                        <span class="ot-status-badge status-${detail.requestStatus}">${detail.requestStatus}</span>
                    </div>
                    <div class="ot-info-item">
                        <span class="ot-info-label">Actual OT Hours</span>
                        <span class="ot-info-value"><fmt:formatNumber value="${detail.hoursActual}" maxFractionDigits="2"/> h</span>
                    </div>
                    <div class="ot-info-item" style="grid-column: 1 / -1;">
                        <span class="ot-info-label">Reason</span>
                        <span class="ot-info-value" style="font-weight: 400;">${detail.reason}</span>
                    </div>
                </div>

                <h3 style="margin-top: 30px; font-size: 16px; color: #334155; border-bottom: 1px solid #e2e8f0; padding-bottom: 10px;">Participants</h3>
                <div style="overflow-x: auto;">
                    <table class="participants-table">
                        <thead>
                            <tr>
                                <th>Employee Code</th>
                                <th>Full Name</th>
                                <th>Position</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="p" items="${detail.participants}">
                                <tr>
                                    <td>${p.employeeCode}</td>
                                    <td>${p.userFullName}</td>
                                    <td>${p.positionName}</td>
                                </tr>
                            </c:forEach>
                            <c:if test="${empty detail.participants}">
                                <tr>
                                    <td colspan="4" style="text-align: center; color: #64748b;">No participants found.</td>
                                </tr>
                            </c:if>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </main>
</div>
</body>
</html>
