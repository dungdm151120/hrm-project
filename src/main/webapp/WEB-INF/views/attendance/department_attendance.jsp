<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Department Attendance | HRM</title>
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
                <h1 class="header-title">Department attendance</h1>
            </div>
        </header>

        <div class="dashboard-content attendance-records-page">
            <section class="attendance-records-card">
                <div class="attendance-records-heading">
                    <div>
                        <h2>Department attendance</h2>
                        <p>Showing attendance records for members of ${departmentName}.</p>
                    </div>
                </div>

                <c:if test="${param.message == 'updated'}">
                    <div class="attendance-matrix-message success" role="status">
                        Attendance record updated successfully.
                    </div>
                </c:if>
                <c:if test="${noDepartmentAssigned}">
                    <div class="attendance-matrix-message error" role="alert">
                        Your account is not assigned to a department.
                    </div>
                </c:if>

                <form class="attendance-matrix-filters department-attendance-filters"
                      action="${pageContext.request.contextPath}/attendance/department"
                      method="get">
                    <div class="matrix-filter-field">
                        <label for="matrixMonth">Month</label>
                        <select id="matrixMonth" name="month">
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
                    </div>

                    <div class="matrix-filter-field">
                        <label for="matrixYear">Year</label>
                        <select id="matrixYear" name="year">
                            <c:forEach var="year" items="${years}">
                                <option value="${year}" ${year == selectedYear ? 'selected' : ''}>${year}</option>
                            </c:forEach>
                        </select>
                    </div>

                    <div class="matrix-filter-field matrix-keyword-filter">
                        <label for="matrixKeyword">Employee</label>
                        <input id="matrixKeyword"
                               type="search"
                               name="keyword"
                               value="${keyword}"
                               placeholder="Search name or employee code">
                    </div>

                    <button type="submit" class="matrix-btn matrix-search-btn">Search</button>
                    <a href="${pageContext.request.contextPath}/attendance/department"
                       class="matrix-btn matrix-clear-btn">Clear</a>
                </form>

                <div class="attendance-legend" aria-label="Attendance status legend">
                    <span><i class="legend-dot status-on-time"></i>On time</span>
                    <span><i class="legend-dot status-late"></i>Late or early leave</span>
                    <span><i class="legend-dot status-absent"></i>Absent</span>
                    <span><i class="legend-dot status-forgot"></i>Forgot Check In/Out</span>
                    <span><i class="legend-dot status-leave"></i>On leave</span>
                    <span><span class="matrix-ot-badge" style="margin-right:4px;">OT</span>Overtime</span>
                </div>

                <c:set var="canUpdateAttendance" value="${sessionScope.userPermissions.contains('ATTENDANCE_UPDATE')}"/>

                <div class="attendance-matrix-wrapper">
                    <table class="attendance-matrix-table">
                        <thead>
                        <tr>
                            <th class="matrix-employee-column">Employee</th>
                            <c:forEach var="day" items="${daysInMonth}" varStatus="dayLoop">
                                <th>${dayLabels[dayLoop.index]}</th>
                            </c:forEach>
                        </tr>
                        </thead>
                        <tbody>
                        <c:choose>
                            <c:when test="${empty employees}">
                                <tr>
                                    <td colspan="8" class="matrix-empty-state">
                                        No attendance records found for your department.
                                    </td>
                                </tr>
                            </c:when>
                            <c:otherwise>
                                <c:forEach var="employee" items="${employees}">
                                    <tr>
                                        <td class="matrix-employee-column">
                                            <c:url var="employeeDetailUrl" value="/attendance/employee">
                                                <c:param name="userId" value="${employee.userId}"/>
                                                <c:param name="month" value="${selectedMonth}"/>
                                                <c:param name="year" value="${selectedYear}"/>
                                            </c:url>
                                            <a class="matrix-employee-link" href="${employeeDetailUrl}">
                                                <strong>${employee.employeeName}</strong>
                                                <span>
                                                    <c:out value="${employee.employeeCode}" default="No code"/>
                                                    <c:if test="${not empty employee.departmentName}">
                                                        - ${employee.departmentName}
                                                    </c:if>
                                                </span>
                                            </a>
                                        </td>

                                        <c:forEach var="day" items="${daysInMonth}">
                                            <c:set var="recordKey" value="${employee.userId}_${day}"/>
                                            <c:set var="record" value="${attendanceMap[recordKey]}"/>
                                            <c:choose>
                                                <c:when test="${not empty record}">
                                                    <td class="matrix-attendance-cell ${record.cssClass}">
                                                        <c:choose>
                                                            <c:when test="${canUpdateAttendance}">
                                                                <c:url var="updateUrl" value="/attendance/update">
                                                                    <c:param name="id" value="${record.attendanceRecordId}"/>
                                                                </c:url>
                                                                <a href="${updateUrl}"
                                                                   class="matrix-cell-link"
                                                                   title="${record.status}">
                                                                    <span class="matrix-status-dot"></span>
                                                                    <span class="matrix-time">
                                                                        <c:choose>
                                                                            <c:when test="${record.status == 'ON_LEAVE'}">On leave</c:when>
                                                                            <c:otherwise>
                                                                                ${record.checkInText} <b>-</b> ${record.checkOutText}
                                                                            </c:otherwise>
                                                                        </c:choose>
                                                                    </span>
                                                                    <c:if test="${not empty record.otStatus and (record.otStatus == 'REGISTERED' or record.otStatus == 'COMPLETED' or record.otStatus == 'PARTIAL' or record.otStatus == 'ABSENT')}"><a href="${pageContext.request.contextPath}/get_overtime_detail?userId=${record.userId}&workDate=${record.workDate}" class="matrix-ot-badge" style="text-decoration:none;" title="View OT Detail">OT</a></c:if>
                                                                    <c:if test="${record.edited}">
                                                                        <span class="matrix-edited-badge">Edited</span>
                                                                    </c:if>
                                                                </a>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <div class="matrix-cell-link" title="${record.status}">
                                                                    <span class="matrix-status-dot"></span>
                                                                    <span class="matrix-time">
                                                                        <c:choose>
                                                                            <c:when test="${record.status == 'ON_LEAVE'}">On leave</c:when>
                                                                            <c:otherwise>
                                                                                ${record.checkInText} <b>-</b> ${record.checkOutText}
                                                                            </c:otherwise>
                                                                        </c:choose>
                                                                    </span>
                                                                    <c:if test="${not empty record.otStatus and (record.otStatus == 'REGISTERED' or record.otStatus == 'COMPLETED' or record.otStatus == 'PARTIAL' or record.otStatus == 'ABSENT')}"><a href="${pageContext.request.contextPath}/get_overtime_detail?userId=${record.userId}&workDate=${record.workDate}" class="matrix-ot-badge" style="text-decoration:none;" title="View OT Detail">OT</a></c:if>
                                                                    <c:if test="${record.edited}">
                                                                        <span class="matrix-edited-badge">Edited</span>
                                                                    </c:if>
                                                                </div>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </td>
                                                </c:when>
                                                <c:otherwise>
                                                    <td class="matrix-attendance-cell matrix-no-record">--</td>
                                                </c:otherwise>
                                            </c:choose>
                                        </c:forEach>
                                    </tr>
                                </c:forEach>
                            </c:otherwise>
                        </c:choose>
                        </tbody>
                    </table>
                </div>

                <div class="matrix-pagination-wrapper">
                    <p>
                        <c:choose>
                            <c:when test="${totalEmployees > 0}">
                                ${totalEmployees} employee(s)
                            </c:when>
                            <c:otherwise>No employees</c:otherwise>
                        </c:choose>
                    </p>

                    <c:if test="${totalPages > 1}">
                        <nav class="matrix-pagination" aria-label="Department attendance pagination">
                            <c:forEach var="pageNumber" begin="1" end="${totalPages}">
                                <c:url var="pageUrl" value="/attendance/department">
                                    <c:param name="month" value="${selectedMonth}"/>
                                    <c:param name="year" value="${selectedYear}"/>
                                    <c:if test="${not empty keyword}">
                                        <c:param name="keyword" value="${keyword}"/>
                                    </c:if>
                                    <c:param name="page" value="${pageNumber}"/>
                                </c:url>
                                <a href="${pageUrl}"
                                   class="matrix-page-link ${pageNumber == currentPage ? 'active' : ''}">
                                    ${pageNumber}
                                </a>
                            </c:forEach>
                        </nav>
                    </c:if>
                </div>
            </section>
        </div>
    </main>
</div>
</body>
</html>
