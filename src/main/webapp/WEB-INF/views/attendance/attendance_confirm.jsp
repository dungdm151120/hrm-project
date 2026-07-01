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

                <div style="margin-top: 20px; padding: 20px; border-radius: 8px; background-color: #f8f9fa; border: 1px solid #e9ecef; margin-bottom: 20px;">
                    <h3 style="margin-top:0; font-size: 16px; color: #495057;">Overall Month Status: 
                        <c:choose>
                            <c:when test="${overallStatus == 'APPROVED'}">
                                <span style="color: #28a745; font-weight: bold;">APPROVED & SNAPSHOT CREATED</span>
                            </c:when>
                            <c:when test="${overallStatus == 'HR_SENT'}">
                                <span style="color: #ffc107; font-weight: bold;">HR SENT (WAITING FOR BUSINESS ADMIN)</span>
                            </c:when>
                            <c:otherwise>
                                <span style="color: #6c757d; font-weight: bold;">PENDING DEPARTMENT CONFIRMATIONS</span>
                            </c:otherwise>
                        </c:choose>
                    </h3>
                    
                    <c:if test="${overallStatus == 'PENDING'}">
                        <c:if test="${isHR}">
                            <p style="margin-bottom:0; color:#6c757d; font-size:14px;">You are HR. You can send the final request to Business Admin once all departments are CONFIRMED.</p>
                            <form action="${pageContext.request.contextPath}/attendance/confirm" method="post" style="margin-top:10px;">
                                <input type="hidden" name="month" value="${selectedMonth}">
                                <input type="hidden" name="year" value="${selectedYear}">
                                <input type="hidden" name="action" value="hr_send">
                                <button type="submit" class="matrix-btn matrix-search-btn" ${!allConfirmed ? 'disabled' : ''} style="${!allConfirmed ? 'opacity:0.5; cursor:not-allowed;' : ''}">Send to Business Admin</button>
                            </form>
                        </c:if>
                    </c:if>
                    
                    <c:if test="${overallStatus == 'HR_SENT'}">
                        <c:if test="${isBusinessAdmin}">
                            <p style="margin-bottom:0; color:#6c757d; font-size:14px;">HR has sent the attendance data. You can now approve and lock it.</p>
                            <form action="${pageContext.request.contextPath}/attendance/confirm" method="post" style="margin-top:10px;">
                                <input type="hidden" name="month" value="${selectedMonth}">
                                <input type="hidden" name="year" value="${selectedYear}">
                                <input type="hidden" name="action" value="business_approve">
                                <button type="submit" class="matrix-btn" style="background-color: #dc3545; color:white; border:none; padding:8px 16px; border-radius:4px; font-weight:600; cursor:pointer;" onclick="return confirm('Are you sure? This will create a permanent snapshot.');">Approve & Create Snapshot</button>
                            </form>
                        </c:if>
                    </c:if>
                </div>

                <div class="attendance-matrix-wrapper">
                    <table class="attendance-matrix-table">
                        <thead>
                        <tr>
                            <th style="min-width:50px;">ID</th>
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
                                <td style="text-align:center;">${dept.departmentId}</td>
                                <td><strong>${dept.departmentName}</strong></td>
                                <td>${dept.managerName != null ? dept.managerName : 'No Manager'}</td>
                                <td>
                                    <c:choose>
                                        <c:when test="${dept.status == 'CONFIRMED'}">
                                            <span style="color:#28a745; font-weight:600;">CONFIRMED</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span style="color:#dc3545; font-weight:600;">PENDING</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <c:if test="${not empty dept.confirmedAt}">
                                        <fmt:formatDate value="${dept.confirmedAt}" pattern="dd/MM/yyyy HH:mm:ss"/>
                                    </c:if>
                                </td>
                                <td>
                                    <c:if test="${dept.status == 'PENDING' && overallStatus == 'PENDING'}">
                                        <c:if test="${dept.managerUserId == currentUser.id}">
                                            <form action="${pageContext.request.contextPath}/attendance/confirm" method="post" style="display:inline;">
                                                <input type="hidden" name="month" value="${selectedMonth}">
                                                <input type="hidden" name="year" value="${selectedYear}">
                                                <input type="hidden" name="departmentId" value="${dept.departmentId}">
                                                <input type="hidden" name="action" value="dept_confirm">
                                                <button type="submit" class="matrix-btn matrix-search-btn" style="padding: 4px 12px; font-size: 13px;" onclick="return confirm('Confirm attendance for ${dept.departmentName}?');">Confirm</button>
                                            </form>
                                        </c:if>
                                    </c:if>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty deptStatuses}">
                            <tr>
                                <td colspan="6" class="matrix-empty-state">No departments found.</td>
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
