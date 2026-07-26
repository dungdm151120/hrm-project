<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Attendance Confirmation & Snapshot | HRM</title>
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
                <h1 class="header-title">Attendance Confirmation & Snapshot</h1>
            </div>
        </header>

        <div class="dashboard-content attendance-records-page">
            <section class="attendance-records-card">
                <div class="attendance-records-heading">
                    <div>
                        <h2>Attendance Confirmation Status</h2>
                        <p>View and manage the attendance confirmation progress for ${selectedMonth}/${selectedYear}.</p>
                    </div>
                </div>

                <c:if test="${not empty sessionScope.successMsg}">
                    <div class="attendance-matrix-message success" role="status">
                        ${sessionScope.successMsg}
                        <c:remove var="successMsg" scope="session"/>
                    </div>
                </c:if>
                <c:if test="${not empty sessionScope.errorMsg}">
                    <div class="attendance-matrix-message error" role="alert">
                        ${sessionScope.errorMsg}
                        <c:remove var="errorMsg" scope="session"/>
                    </div>
                </c:if>

                <form class="attendance-matrix-filters" action="${pageContext.request.contextPath}/attendance/confirm" method="get">
                    <div class="matrix-filter-field">
                        <label for="matrixMonth">Month</label>
                        <select name="month" id="matrixMonth" class="matrix-filter-select">
                            <c:forEach var="m" begin="1" end="12">
                                <option value="${m}" ${m == selectedMonth ? 'selected' : ''}>${m}</option>
                            </c:forEach>
                        </select>
                    </div>

                    <div class="matrix-filter-field">
                        <label for="matrixYear">Year</label>
                        <select name="year" id="matrixYear" class="matrix-filter-select">
                            <c:forEach var="y" begin="${selectedYear - 5}" end="${selectedYear + 1}">
                                <option value="${y}" ${y == selectedYear ? 'selected' : ''}>${y}</option>
                            </c:forEach>
                        </select>
                    </div>

                    <button type="submit" class="matrix-btn matrix-search-btn">Check Status</button>
                </form>

                <div class="attendance-confirm-summary">
                    <h3 class="attendance-confirm-summary-title">Overall Month Status:
                        <c:choose>
                            <c:when test="${overallStatus == 'FINALIZED'}">
                                <span class="attendance-confirm-status finalized">Finalized</span>
                            </c:when>
                            <c:otherwise>
                                <span class="attendance-confirm-status pending">PENDING DEPARTMENT CONFIRMATIONS</span>
                            </c:otherwise>
                        </c:choose>
                    </h3>
                    
                    <c:if test="${overallStatus == 'PENDING' && confirmationAllowed}">
                        <c:if test="${isHRManager}">
                            <p class="attendance-confirm-note">All department confirmations are required before HR can finalize attendance.</p>
                            <form action="${pageContext.request.contextPath}/attendance/confirm" method="post" class="attendance-confirm-finalize-form">
                                <input type="hidden" name="month" value="${selectedMonth}">
                                <input type="hidden" name="year" value="${selectedYear}">
                                <input type="hidden" name="action" value="hr_finalize">
                                <button type="submit" class="matrix-btn matrix-search-btn attendance-confirm-finalize-btn" ${!allConfirmed ? 'disabled' : ''} onclick="return confirm('Are you sure? This will create a permanent snapshot.');">Finalize Attendance</button>
                            </form>
                        </c:if>
                    </c:if>
                    <c:if test="${overallStatus == 'PENDING' && !confirmationAllowed}">
                        <p class="attendance-confirm-note">Attendance can only be confirmed for the previous month from day 5 to day 10.</p>
                    </c:if>
                    
                </div>

                <div class="attendance-matrix-wrapper">
                    <table class="attendance-matrix-table">
                        <thead>
                        <tr>
                            <th>Department Name</th>
                            <th>Manager</th>
                            <th>Status</th>
                            <th>Confirmed At</th>
                            <th>Action</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach var="dept" items="${deptStatuses}">
                            <tr>
                                <td><strong>${dept.departmentName}</strong></td>
                                <td>${dept.managerName != null ? dept.managerName : 'No Manager'}</td>
                                <td>
                                    <c:choose>
                                        <c:when test="${overallStatus == 'FINALIZED'}">
                                            <span class="attendance-confirm-status finalized">FINALIZED</span>
                                        </c:when>
                                        <c:when test="${dept.status == 'CONFIRMED'}">
                                            <span class="attendance-confirm-status confirmed">CONFIRMED</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="attendance-confirm-status pending">PENDING</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <c:if test="${not empty dept.confirmedAt}">
                                        <fmt:formatDate value="${dept.confirmedAt}" pattern="dd/MM/yyyy HH:mm:ss"/>
                                    </c:if>
                                </td>
                                <td>
                                    <c:if test="${dept.status == 'PENDING' && overallStatus == 'PENDING' && confirmationAllowed}">
                                        <c:if test="${dept.managerUserId == currentUser.id}">
                                            <form action="${pageContext.request.contextPath}/attendance/confirm" method="post" class="attendance-confirm-action-form">
                                                <input type="hidden" name="month" value="${selectedMonth}">
                                                <input type="hidden" name="year" value="${selectedYear}">
                                                <input type="hidden" name="departmentId" value="${dept.departmentId}">
                                                <input type="hidden" name="action" value="dept_confirm">
                                                <button type="submit" class="matrix-btn matrix-search-btn attendance-confirm-action-btn" onclick="return confirm('Confirm attendance for ${dept.departmentName}?');">Confirm</button>
                                            </form>
                                        </c:if>
                                    </c:if>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty deptStatuses}">
                            <tr>
                                <td colspan="5" class="matrix-empty-state">No departments found.</td>
                            </tr>
                        </c:if>
                        </tbody>
                    </table>
                </div>

            </section>
        </div>
    </main>
</div>
</body>
</html>
