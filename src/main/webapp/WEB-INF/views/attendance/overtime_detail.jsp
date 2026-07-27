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
            <div class="ot-detail-page">
                <section class="ot-detail-summary">
                    <div class="ot-detail-person">
                        <span class="ot-detail-eyebrow">Employee</span>
                        <h2><c:out value="${detail.userFullName}"/></h2>
                        <span><c:out value="${detail.employeeCode}"/></span>
                    </div>

                    <div class="ot-detail-grid">
                        <div class="ot-detail-field">
                            <span>Overtime Date</span>
                            <strong><c:out value="${detail.overtimeDate}"/></strong>
                        </div>
                        <div class="ot-detail-field">
                            <span>Shift Time</span>
                            <strong><c:out value="${detail.shiftStart}"/> - <c:out value="${detail.shiftEnd}"/></strong>
                        </div>
                        <div class="ot-detail-field">
                            <span>Request Status</span>
                            <strong class="ot-detail-status"><c:out value="${detail.requestStatus}"/></strong>
                        </div>
                        <div class="ot-detail-field">
                            <span>Actual OT Hours</span>
                            <strong><fmt:formatNumber value="${detail.hoursActual}" maxFractionDigits="2"/> h</strong>
                        </div>
                    </div>

                    <div class="ot-detail-reason">
                        <span>Reason</span>
                        <p><c:out value="${detail.reason}"/></p>
                    </div>
                </section>

                <section class="ot-detail-participants">
                    <div class="ot-detail-section-heading">
                        <h3>Participants</h3>
                        <span>${empty detail.participants ? 0 : detail.participants.size()} employee(s)</span>
                    </div>
                    <div class="ot-detail-table-wrap">
                        <table class="participants-table">
                            <thead>
                                <tr>
                                    <th>Employee Code</th>
                                    <th>Full Name</th>
                                    <th>Position</th>
                                    <th>Status</th>
                                    <th>Actual OT Hours</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="p" items="${detail.participants}">
                                    <tr>
                                        <td><c:out value="${p.employeeCode}"/></td>
                                        <td><c:out value="${p.userFullName}"/></td>
                                        <td><c:out value="${p.positionName}"/></td>
                                        <td><span class="ot-detail-status"><c:out value="${p.status}"/></span></td>
                                        <td><fmt:formatNumber value="${p.hoursActual}" maxFractionDigits="2"/> h</td>
                                    </tr>
                                </c:forEach>
                                <c:if test="${empty detail.participants}">
                                    <tr>
                                        <td colspan="5" class="ot-detail-empty">No participants found.</td>
                                    </tr>
                                </c:if>
                            </tbody>
                        </table>
                    </div>
                </section>
            </div>
        </div>
    </main>
</div>
</body>
</html>
