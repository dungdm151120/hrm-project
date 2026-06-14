<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Update Attendance Record | HRM</title>
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
                <h1 class="header-title">Update Attendance Record</h1>
            </div>
        </header>

        <div class="dashboard-content attendance-update-page">
            <section class="attendance-update-card">
                <div class="attendance-update-heading">
                    <h2>Update Attendance Record</h2>
                    <p>Correct attendance data and provide a reason for the change.</p>
                </div>

                <c:if test="${not empty error}">
                    <div class="attendance-update-error" role="alert">
                        <c:out value="${error}"/>
                    </div>
                </c:if>

                <form method="post"
                      action="${pageContext.request.contextPath}/attendance/update"
                      class="attendance-update-form">
                    <input type="hidden" name="id" value="${record.id}">

                    <div class="attendance-update-row">
                        <label for="employeeName">Employee Name</label>
                        <input id="employeeName"
                               type="text"
                               value="${fn:escapeXml(record.employeeName)}"
                               readonly>
                    </div>

                    <div class="attendance-update-row">
                        <label for="employeeCode">Employee ID / Code</label>
                        <input id="employeeCode"
                               type="text"
                               value="${fn:escapeXml(record.employeeCode)}"
                               readonly>
                    </div>

                    <div class="attendance-update-row">
                        <label for="workDate">Work Date</label>
                        <input id="workDate"
                               type="text"
                               value="${record.workDate}"
                               readonly>
                    </div>

                    <div class="attendance-update-row">
                        <label for="checkIn">Check in</label>
                        <input id="checkIn"
                               type="time"
                               name="checkIn"
                               value="${record.checkInText}">
                    </div>

                    <div class="attendance-update-row">
                        <label for="checkOut">Check out</label>
                        <input id="checkOut"
                               type="time"
                               name="checkOut"
                               value="${record.checkOutText}">
                    </div>

                    <div class="attendance-update-row">
                        <label for="workingTime">Working time</label>
                        <input id="workingTime"
                               type="text"
                               value="${record.totalWorkHours} hours"
                               readonly>
                    </div>

                    <div class="attendance-update-row">
                        <label for="status">Status</label>
                        <input id="status"
                               type="text"
                               value="${record.status}"
                               readonly>
                    </div>

                    <div class="attendance-update-row attendance-note-row">
                        <label for="note">Reason / Note</label>
                        <textarea id="note"
                                  name="note"
                                  maxlength="1000"
                                  required
                                  placeholder="Enter the reason for this attendance correction"><c:out value="${record.note}"/></textarea>
                    </div>

                    <div class="attendance-update-actions">
                        <button type="submit" class="attendance-update-submit">Update</button>

                        <c:url var="cancelUrl" value="/attendance/records">
                            <c:param name="month" value="${record.workDate.monthValue}"/>
                            <c:param name="year" value="${record.workDate.year}"/>
                        </c:url>
                        <a href="${cancelUrl}" class="attendance-update-cancel">Cancel</a>
                    </div>
                </form>
            </section>
        </div>
    </main>
</div>
</body>
</html>
