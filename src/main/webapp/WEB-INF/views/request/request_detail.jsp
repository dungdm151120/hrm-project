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
                        <span class="detail-label">ID:</span>
                        <span class="detail-value">${request.id}</span>
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
                        <span class="detail-label">Handler:</span>
                        <span class="detail-value"><strong>${request.handlerName}</strong></span>
                    </div>
                    <div class="detail-row">
                        <span class="detail-label">Reason:</span>
                        <span class="detail-value">${request.reason}</span>
                    </div>
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
                            <c:when test="${request.status == 'PENDING' and sessionScope.currentUser.id eq request.approverId}">
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