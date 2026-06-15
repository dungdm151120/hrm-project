<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Request Detail - #${request.id} | HRM</title>
</head>

<body>
<div class="detail-card">
    <div class="detail-info">
        <div class="detail-row">
            <span class="detail-label">Request ID</span>
            <span class="detail-value">#${request.id}</span>
        </div>
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
            <span class="detail-label">Reason:</span>
            <span class="detail-value">${request.reason}</span>
        </div>
        <hr>
        <div class="detail-row">
            <span class="detail-label">Approver:</span>
            <span class="detail-value">${request.approverName}</span>
        </div>
        <div class="detail-row">
            <span class="detail-label">Observer(s):</span>
            <span class="detail-value">
        <c:choose>
            <c:when test="${not empty request.observer}">
                <ul>
                    <c:forEach items="${request.observer}" var="obs">
                        <li>${obs.fullName}</li>
                    </c:forEach>
                </ul>
            </c:when>
            <c:otherwise>
                N/A
            </c:otherwise>
        </c:choose>
    </span>
        </div>
    </div>
</div>

<div class="detail-row">
    <span class="detail-label">Approver Comment:</span>
    <span class="detail-value">
        <c:choose>
            <c:when test="${request.status == 'PENDING' && sessionScope.userId == request.approverId}">
                <form action="process_request" method="POST">
                    <input type="hidden" name="requestId" value="${request.id}">
                    <textarea name="comment" required placeholder="Enter approver comment..."></textarea>
                    <div>
                        <button type="submit" name="action" value="APPROVE">Approve</button>
                        <button type="submit" name="action" value="REJECT">Reject</button>
                    </div>
                </form>
            </c:when>

            <c:otherwise>
                <div>
                        ${not empty request.approverComment ? request.approverComment : 'No comment provided.'}
                </div>
            </c:otherwise>
        </c:choose>
    </span>
</div>

<div class="form-actions">
    <c:choose>
        <c:when test="${from == 'all'}">
            <a href="view_all_request">Back to list</a>
        </c:when>
        <c:otherwise>
            <a href="view_my_request">Back to list</a>
        </c:otherwise>
    </c:choose>
</div>

</body>
</html>