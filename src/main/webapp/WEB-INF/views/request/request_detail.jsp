<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Request Detail - #${request.id} | HRM</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body class="dashboard-body">

<div class="dashboard-wrapper">
    <jsp:include page="/WEB-INF/views/common/sidebar.jsp"/>

    <div class="dashboard-main">
        <div class="dashboard-header">
            <h1 class="header-title">Request Detail #${request.id}</h1>
        </div>

        <div class="dashboard-content">
            <div class="detail-card">
                <div class="detail-info">
                    <div class="detail-row">
                        <span class="detail-label">Proposer:</span>
                        <span class="detail-value">${request.proposerName}</span>
                    </div>
                    <div class="detail-row">
                        <span class="detail-label">Department:</span>
                        <span class="detail-value">${not empty request.departmentName ? request.departmentName : 'N/A'}</span>
                    </div>
                    <div class="detail-row">
                        <span class="detail-label">Type:</span>
                        <span class="detail-value"><strong>${request.readableType}</strong></span>
                    </div>
                    <div class="detail-row">
                        <span class="detail-label">Status:</span>
                        <span class="detail-value">
                            <span class="badge badge-${fn:toLowerCase(request.status)}">${request.status}</span>
                        </span>
                    </div>
                    <div class="detail-row">
                        <span class="detail-label">Created At:</span>
                        <span class="detail-value"><fmt:formatDate value="${request.createdAt}" pattern="dd/MM/yyyy HH:mm"/></span>
                    </div>
                    <div class="detail-row">
                        <span class="detail-label">Handler:</span>
                        <span class="detail-value"><strong>${request.handlerName}</strong></span>
                    </div>
                    <div class="detail-row">
                        <span class="detail-label">Reason:</span>
                        <span class="detail-value">${request.reason}</span>
                    </div>
                    <c:if test="${request.type == 'LEAVE_REQUEST' && not empty leaveRequest}">
                        <div class="detail-row">
                            <span class="detail-label">Leave Type:</span>
                            <span class="detail-value">
                                <c:choose>
                                    <c:when test="${leaveRequest.leaveType == 'ON_LEAVE'}">On Leave (Paid)</c:when>
                                    <c:when test="${leaveRequest.leaveType == 'LEAVE'}">Leave (Unpaid)</c:when>
                                    <c:otherwise>${leaveRequest.leaveType}</c:otherwise>
                                </c:choose>
                            </span>
                        </div>
                        <div class="detail-row">
                            <span class="detail-label">Leave Date:</span>
                            <span class="detail-value"><strong>${leaveRequest.leaveDate}</strong></span>
                        </div>
                    </c:if>
                    <c:if test="${request.type == 'OVERTIME' && not empty overtimeRequest}">
                        <div class="detail-row">
                            <span class="detail-label">OT Date:</span>
                            <span class="detail-value"><strong>${overtimeRequest.overtimeDate}</strong></span>
                        </div>
                        <div class="detail-row">
                            <span class="detail-label">Shift:</span>
                            <span class="detail-value">${overtimeRequest.shiftStart} - ${overtimeRequest.shiftEnd}</span>
                        </div>
                        <div class="detail-row" style="grid-column: 1 / -1; margin-top: 15px;">
                            <span class="detail-label">OT Participants:</span>
                            <div class="detail-value" style="width: 100%;">
                                <table class="table" style="width: 100%; border-collapse: collapse; margin-top: 10px;">
                                    <thead>
                                        <tr style="background-color: #f5f5f5;">
                                            <th style="padding: 8px; border: 1px solid #ddd; text-align: left;">Employee</th>
                                            <th style="padding: 8px; border: 1px solid #ddd; text-align: center;">Status</th>
                                            <th style="padding: 8px; border: 1px solid #ddd; text-align: right;">Actual Hours</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach items="${overtimeParticipants}" var="p">
                                            <tr>
                                                <td style="padding: 8px; border: 1px solid #ddd;">${p.userFullName} - ${p.employeeCode}</td>
                                                <td style="padding: 8px; border: 1px solid #ddd; text-align: center;">
                                                    <span class="badge badge-${fn:toLowerCase(p.status)}">${p.status}</span>
                                                </td>
                                                <td style="padding: 8px; border: 1px solid #ddd; text-align: right;">${p.hoursActual}</td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                                <c:if test="${request.status == 'APPROVED' && (sessionScope.currentUser.id eq request.approverId || fn:contains(sessionScope.currentUser.roleName, 'HR'))}">
                                    <form action="confirm_overtime" method="POST" style="margin-top: 15px;">
                                        <input type="hidden" name="requestId" value="${request.id}">
                                        <button type="submit" class="btn btn-success" onclick="return confirm('Bạn có chắc chắn muốn xác nhận tính giờ OT cho request này?');">Xác nhận OT (Confirm Overtime)</button>
                                    </form>
                                </c:if>
                                <c:if test="${request.status == 'CONFIRMED'}">
                                    <div style="margin-top: 15px; padding: 10px; background-color: #f3e8ff; color: #6b21a8; border-radius: 8px; border: 1px solid #d8b4fe;">
                                        <strong><svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="vertical-align: text-bottom; margin-right: 4px;"><path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"></path><polyline points="22 4 12 14.01 9 11.01"></polyline></svg> OT Confirmed!</strong><br/>
                                        The OT hours for this request have been successfully calculated and synced with the attendance records.
                                    </div>
                                </c:if>
                            </div>
                        </div>
                    </c:if>
                    <div class="detail-row">
                        <span class="detail-label">Approver:</span>
                        <span class="detail-value">${request.approverName}</span>
                    </div>
                    <div class="detail-row">
                        <span class="detail-label">Observer(s):</span>
                        <span class="detail-value">
                            <c:choose>
                                <c:when test="${not empty request.observer}">
                                    <ul style="margin: 0; padding-left: 20px;">
                                        <c:forEach items="${request.observer}" var="obs">
                                            <li>${obs.fullName} - ${obs.positionName}</li>
                                        </c:forEach>
                                    </ul>
                                </c:when>
                                <c:otherwise>N/A</c:otherwise>
                            </c:choose>
                        </span>
                    </div>
                    <div class="detail-row">
                        <span class="detail-label">Processed At:</span>
                        <span class="detail-value">
                            <c:choose>
                                <c:when test="${not empty request.processedAt}">
                                    <fmt:formatDate value="${request.processedAt}" pattern="dd/MM/yyyy HH:mm"/>
                                </c:when>
                                <c:otherwise>
                                    <span>-</span>
                                </c:otherwise>
                            </c:choose>
                        </span>
                    </div>
                    <div class="detail-row" style="margin-top: 20px;">
                        <span class="detail-label">Approver Comment *:</span>
                        <span class="detail-value">
                        <c:choose>
                            <c:when test="${request.status == 'PENDING' && sessionScope.currentUser.id eq request.approverId}">
                                <form action="process_request" method="POST">
                                    <input type="hidden" name="requestId" value="${request.id}">
                                    <textarea name="comment" class="form-control" required placeholder="Enter approver comment..."></textarea>
                                    <div style="margin-top: 10px;">
                                        <button type="submit" name="action" value="APPROVE" class="btn btn-primary">Approve</button>
                                        <button type="submit" name="action" value="REJECT" class="btn btn-danger">Reject</button>
                                    </div>
                                </form>
                            </c:when>
                            <c:otherwise>
                                <p>${not empty request.approverComment ? request.approverComment : 'No comment provided.'}</p>
                            </c:otherwise>
                        </c:choose>
                        </span>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>