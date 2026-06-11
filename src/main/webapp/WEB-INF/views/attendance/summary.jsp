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
    <title>Attendance Summary | HRM</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body class="dashboard-body">
<div class="dashboard-wrapper">
    <jsp:include page="/WEB-INF/views/common/sidebar.jsp"/>

    <main class="dashboard-main">
        <header class="dashboard-header">
            <div class="header-left">
                <h1 class="header-title">Attendance Summary</h1>
            </div>
        </header>

        <div class="dashboard-content attendance-summary-page">
            <div class="attendance-summary-toolbar">
                <div>
                    <h2>Attendance Summary</h2>
                    <p>${displayUser.fullName}</p>
                </div>

                <form id="attendancePeriodForm"
                      class="attendance-period-form"
                      action="${summaryAction}"
                      method="get">
                    <c:if test="${not empty summaryUserId}">
                        <input type="hidden" name="userId" value="${summaryUserId}">
                    </c:if>
                    <select id="attendanceYear" name="year" aria-label="Chọn năm">
                        <c:forEach var="year" items="${years}">
                            <option value="${year}" ${year == selectedYear ? 'selected' : ''}>${year}</option>
                        </c:forEach>
                    </select>
                    <select id="attendanceMonth" name="month" aria-label="Chọn tháng">
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
                </form>
            </div>

            <section class="attendance-summary-sheet">
                <div class="attendance-work-card">
                    <div class="attendance-work-icon">
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <rect x="4" y="5" width="16" height="15" rx="2"></rect>
                            <path d="M8 3v4M16 3v4M8 12l2.5 2.5L16 9"></path>
                        </svg>
                    </div>
                    <div>
                        <span>Actual Work Hour/Standard Work Hour</span>
                        <strong>
                            <fmt:formatNumber value="${summary.totalWorkHours}" maxFractionDigits="2"/>
                            /
                            <fmt:formatNumber value="${summary.expectedWorkHours}" maxFractionDigits="0"/>
                        </strong>
                    </div>
                </div>

                <div class="attendance-summary-list">

                    <div class="attendance-summary-row">
                        <span>Number of leaves this month</span>
                        <strong><fmt:formatNumber value="${summary.leaveDaysInMonth}" maxFractionDigits="2"/></strong>
                    </div>
                    <div class="attendance-summary-row">
                        <span>Number of Late Arrivals</span>
                        <strong>${summary.lateCount}</strong>
                    </div>
                    <div class="attendance-summary-row">
                        <span>Number of Early Arrivals</span>
                        <strong>${summary.earlyLeaveCount}</strong>
                    </div>
                    <div class="attendance-summary-row">
                        <span>Missing Check-in/out</span>
                        <strong>${summary.forgotCheckCount}</strong>
                    </div>

                    <div class="attendance-summary-row">
                        <span>Total Late/Early Hours (hh:mm)</span>
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
                        <strong><fmt:formatNumber value="${summary.overtimeHours}" maxFractionDigits="2"/>h</strong>
                    </div>
                </div>
            </section>
        </div>
    </main>
</div>

<script>
    (function () {
        const form = document.getElementById('attendancePeriodForm');
        const yearSelect = document.getElementById('attendanceYear');
        const monthSelect = document.getElementById('attendanceMonth');

        function submitPeriod() {
            form.submit();
        }

        yearSelect.addEventListener('change', submitPeriod);
        monthSelect.addEventListener('change', submitPeriod);

        const params = new URLSearchParams(window.location.search);
        if (!params.has('year') && !params.has('month')) {
            const deviceDate = new Date();
            const deviceYear = deviceDate.getFullYear();
            const deviceMonth = deviceDate.getMonth() + 1;

            if (Number(yearSelect.value) !== deviceYear || Number(monthSelect.value) !== deviceMonth) {
                const hasDeviceYear = Array.from(yearSelect.options)
                    .some(function (option) { return Number(option.value) === deviceYear; });
                if (hasDeviceYear) {
                    yearSelect.value = String(deviceYear);
                    monthSelect.value = String(deviceMonth);
                    submitPeriod();
                }
            }
        }
    })();
</script>
</body>
</html>
