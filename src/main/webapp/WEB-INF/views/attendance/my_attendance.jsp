<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%
    model.AttendanceSummary attendanceSummary =
        (model.AttendanceSummary) request.getAttribute("summary");
    long lateEarlyMinutes = attendanceSummary == null
            ? 0
            : Math.round(attendanceSummary.getTotalLateAndEarlyHours() * 60);
    request.setAttribute("lateEarlyTime",
            String.format("%02d:%02d", lateEarlyMinutes / 60, lateEarlyMinutes % 60));
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${summaryUserId != null ? 'Attendance Detail' : 'My Attendance'} | HRM</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/attendance.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/my-attendance.css">
</head>
<body class="dashboard-body">
<div class="dashboard-wrapper">
    <jsp:include page="/WEB-INF/views/common/sidebar.jsp"/>

    <main class="dashboard-main">
        <header class="dashboard-header">
            <div class="header-left">
               <h1 class="header-title">My Attendance</h1>
            </div>
        </header>

        <div class="dashboard-content">
            <div class="my-attendance-layout">
                <div class="my-attendance-calendar-wrap">
                    <div class="calendar-toolbar">
                        <h2>${displayUser.fullName} (${employeeCode})</h2>
                        <form id="attendancePeriodForm" action="${summaryAction}" method="get">
                            <c:if test="${not empty summaryUserId}">
                                <input type="hidden" name="userId" value="${summaryUserId}">
                            </c:if>
                            <select id="attendanceMonth" name="month" aria-label="Month">
                                <option value="1" ${selectedMonth == 1 ? 'selected' : ''}>January</option>
                                <option value="2" ${selectedMonth == 2 ? 'selected' : ''}>February</option>
                                <option value="3" ${selectedMonth == 3 ? 'selected' : ''}>March</option>
                                <option value="4" ${selectedMonth == 4 ? 'selected' : ''}>April</option>
                                <option value="5" ${selectedMonth == 5 ? 'selected' : ''}>May</option>
                                <option value="6" ${selectedMonth == 6 ? 'selected' : ''}>June</option>
                                <option value="7" ${selectedMonth == 7 ? 'selected' : ''}>July</option>
                                <option value="8" ${selectedMonth == 8 ? 'selected' : ''}>August</option>
                                <option value="9" ${selectedMonth == 9 ? 'selected' : ''}>September</option>
                                <option value="10" ${selectedMonth == 10 ? 'selected' : ''}>October</option>
                                <option value="11" ${selectedMonth == 11 ? 'selected' : ''}>November</option>
                                <option value="12" ${selectedMonth == 12 ? 'selected' : ''}>December</option>
                            </select>
                            <select id="attendanceYear" name="year" aria-label="Year">
                                <c:forEach var="year" items="${years}">
                                    <option value="${year}" ${year == selectedYear ? 'selected' : ''}>${year}</option>
                                </c:forEach>
                            </select>
                        </form>
                        <a href="${pageContext.request.contextPath}/admin/attendance/exportPersonal?userId=${displayUser.id}&month=${selectedMonth}&year=${selectedYear}"
                           class="btn-export"
                           style="margin-left: auto; display: inline-flex; align-items: center; gap: 6px; background: #4361ee; color: white; padding: 8px 16px; border-radius: 6px; text-decoration: none; font-weight: 500; font-size: 14px;">
                            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/><polyline points="7 10 12 15 17 10"/><line x1="12" y1="15" x2="12" y2="3"/></svg>
                            Export
                        </a>
                    </div>

                    <div class="attendance-legend">
                        <span><i class="legend-dot status-on-time"></i>On time</span>
                        <span><i class="legend-dot status-late"></i>Late or early leave</span>
                        <span><i class="legend-dot status-absent"></i>Absent</span>
                        <span><i class="legend-dot status-forgot"></i>Forgot Check In/Out</span>
                        <span><i class="legend-dot status-leave"></i>On leave</span>
                    </div>

                    <div class="attendance-calendar">
                        <div class="calendar-header">
                            <div class="calendar-header-cell">Mon</div>
                            <div class="calendar-header-cell">Tue</div>
                            <div class="calendar-header-cell">Wed</div>
                            <div class="calendar-header-cell">Thu</div>
                            <div class="calendar-header-cell">Fri</div>
                            <div class="calendar-header-cell">Sat</div>
                            <div class="calendar-header-cell">Sun</div>
                        </div>

                        <c:set var="todayDate" value="<%= java.time.LocalDate.now() %>" />
                        <c:set var="firstDayOfMonth" value="${daysInMonth[0]}" />
                        <c:set var="dayOfWeekIndex" value="${firstDayOfMonth.dayOfWeek.value - 1}" />

                        <c:set var="currentDay" value="${firstDayOfMonth.minusDays(dayOfWeekIndex)}" />
                        <c:set var="lastDayOfMonth" value="${daysInMonth[daysInMonth.size() - 1]}" />

                        <c:forEach var="week" begin="0" end="4">
                            <c:if test="${currentDay <= lastDayOfMonth}">
                                <div class="calendar-week">
                                    <c:forEach var="dayOfWeek" begin="0" end="6">
                                        <c:set var="isCurrentMonth" value="${currentDay >= firstDayOfMonth && currentDay <= lastDayOfMonth}" />
                                        <c:set var="isToday" value="${currentDay == todayDate}" />

                                        <div class="calendar-day ${isCurrentMonth ? '' : 'other-month'} ${isToday ? 'today' : ''}">
                                            <div class="day-number">${currentDay.dayOfMonth}</div>
                                            <div class="day-record">
                                                <c:if test="${isCurrentMonth}">
                                                    <c:set var="recordKey" value="${displayUser.id}_${currentDay}" />
                                                    <c:set var="record" value="${attendanceMap[recordKey]}" />
                                                    <c:choose>
                                                        <c:when test="${not empty record}">
                                                            <span class="record-chip ${record.cssClass}"
                                                                  title="${record.status}">
                                                                <span class="chip-dot"></span>
                                                                ${record.checkInText} - ${record.checkOutText}
                                                            </span>
                                                            <c:if test="${record.overtimeHours > 0}">
                                                                <span class="record-chip chip-ot">OT</span>
                                                            </c:if>
                                                            <c:if test="${record.edited}">
                                                                <span class="record-chip chip-edited">Edited</span>
                                                            </c:if>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <div class="no-record">--</div>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </c:if>
                                            </div>
                                        </div>
                                        <c:set var="currentDay" value="${currentDay.plusDays(1)}" />
                                    </c:forEach>
                                </div>
                            </c:if>
                        </c:forEach>
                    </div>
                </div>

                <div class="my-attendance-summary-card">
                    <div class="attendance-work-card">
                        <div class="attendance-work-icon">
                            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <rect x="4" y="5" width="16" height="15" rx="2"></rect>
                                <path d="M8 3v4M16 3v4M8 12l2.5 2.5L16 9"></path>
                            </svg>
                        </div>
                        <div>
                            <span>Actual Work Hour / Standard</span>
                            <strong>
                                <fmt:formatNumber value="${summary.totalWorkHours}" maxFractionDigits="2"/> /
                                <fmt:formatNumber value="${summary.expectedWorkHours}" maxFractionDigits="0"/> h
                            </strong>
                        </div>
                    </div>

                    <div class="attendance-summary-list">
                        <div class="attendance-summary-row">
                            <span>Number of leaves this month</span>
                            <strong><fmt:formatNumber value="${summary.leaveDaysInMonth}" maxFractionDigits="2"/></strong>
                        </div>
                        <div class="attendance-summary-row">
                            <span>Number of absent this month</span>
                            <strong><fmt:formatNumber value="${summary.absentDaysInMonth}" maxFractionDigits="2"/></strong>
                        </div>
                        <div class="attendance-summary-row">
                            <span>Number of Late Arrivals</span>
                            <strong>${summary.lateCount}</strong>
                        </div>
                        <div class="attendance-summary-row">
                            <span>Number of Early Leaves</span>
                            <strong>${summary.earlyLeaveCount}</strong>
                        </div>
                        <div class="attendance-summary-row">
                            <span>Missing Check-in/out</span>
                            <strong>${summary.forgotCheckCount}</strong>
                        </div>
                        <div class="attendance-summary-row">
                            <span>Total Late/Early (hh:mm)</span>
                            <strong>${lateEarlyTime}</strong>
                        </div>
                        <div class="attendance-summary-row">
                            <span>Remaining Leave Balance</span>
                            <strong><fmt:formatNumber value="${summary.remainingLeaveDays}" maxFractionDigits="2"/></strong>
                        </div>
                        <div class="attendance-summary-row">
                            <span>Leave Allowed</span>
                            <strong><fmt:formatNumber value="${summary.entitledLeaveDays}" maxFractionDigits="2"/></strong>
                        </div>
                        <div class="attendance-summary-row">
                            <span>Advanced Leave Taken</span>
                            <strong><fmt:formatNumber value="${summary.advancedLeaveDays}" maxFractionDigits="2"/></strong>
                        </div>
                        <div class="attendance-summary-row">
                            <span>Overtime Hours</span>
                            <strong><fmt:formatNumber value="${summary.overtimeHours}" maxFractionDigits="2"/> h</strong>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </main>
</div>

<script>
    (function() {
        const monthSelect = document.getElementById('attendanceMonth');
        const yearSelect = document.getElementById('attendanceYear');
        const form = document.getElementById('attendancePeriodForm');
        function submitForm() { form.submit(); }
        monthSelect.addEventListener('change', submitForm);
        yearSelect.addEventListener('change', submitForm);
    })();
</script>
</body>
</html>
