<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.Request" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>My Requests | HRM</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body class="dashboard-body">

<div class="dashboard-wrapper">
    <jsp:include page="/WEB-INF/views/common/sidebar.jsp"/>

    <div class="dashboard-main">
        <div class="dashboard-header">
            <div class="header-left">
                <h1 class="header-title">My Requests</h1>
            </div>
            <div class="header-right">
                <a href="${pageContext.request.contextPath}/view_observed_request" class="btn-primary">View Observed Requests</a>
                <a href="${pageContext.request.contextPath}/view_handled_request" class="btn-primary">View Handled Requests</a>
                <a href="${pageContext.request.contextPath}/create_request" class="btn-primary">Create New Request</a>
            </div>
        </div>

        <div class="dashboard-content">
            <div class="search-filter">
                <form action="${pageContext.request.contextPath}/view_my_request" method="GET">
                    <select name="status" onchange="this.form.submit()">
                        <option value="" ${empty selectedStatus ? 'selected' : ''}>All Status</option>
                        <option value="PENDING" ${selectedStatus == 'PENDING' ? 'selected' : ''}>Pending</option>
                        <option value="APPROVED" ${selectedStatus == 'APPROVED' ? 'selected' : ''}>Approved</option>
                        <option value="REJECTED" ${selectedStatus == 'REJECTED' ? 'selected' : ''}>Rejected</option>
                        <option value="CLOSED" ${selectedStatus == 'REJECTED' ? 'selected' : ''}>Closed</option>
                        <option value="CANCELLED" ${selectedStatus == 'REJECTED' ? 'selected' : ''}>Cancelled</option>
                    </select>

                    <select name="type" onchange="this.form.submit()">
                        <option value="" ${empty selectedType ? 'selected' : ''}>All Types</option>
                        <c:forEach items="<%= Request.getAllType() %>" var="entry">
                            <option value="${entry.key}" ${selectedType == entry.key ? 'selected' : ''}>${entry.value}</option>
                        </c:forEach>
                    </select>

                    <select name="sort" onchange="this.form.submit()">
                        <option value="newest" ${selectedSort == 'newest' ? 'selected' : ''}>Newest</option>
                        <option value="oldest" ${selectedSort == 'oldest' ? 'selected' : ''}>Oldest</option>
                    </select>
                    <a href="${pageContext.request.contextPath}/view_my_request" class="btn-reset">Clear</a>
                </form>
            </div>

            <div class="table-wrapper">
                <table>
                    <thead>
                    <tr>
                        <th>Type</th>
                        <th>Proposer</th>
                        <th>Status</th>
                        <th>Created At</th>
                        <th>Handler</th>
                        <th>Processed At</th>
                        <th>Actions</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach items="${myRequests}" var="req" varStatus="r">
                        <tr>
                            <td>${req.readableType}</td>
                            <td>${req.proposerName}</td>
                            <td><span class="badge badge-${fn:toLowerCase(req.status)}">${req.status}</span></td>
                            <td><fmt:formatDate value="${req.createdAt}" pattern="dd/MM/yyyy HH:mm"/></td>
                            <td>${req.handlerName}</td>
                            <td>
                                <c:if test="${not empty req.processedAt}">
                                    <fmt:formatDate value="${req.processedAt}" pattern="dd/MM/yyyy HH:mm"/>
                                </c:if>
                                <c:if test="${empty req.processedAt}">-</c:if>
                            </td>
                            <td>
                                <div class="actions">
                                    <a href="request_detail?id=${req.id}" class="btn-secondary">View Details</a>
                                    <c:if test="${req.status == 'PENDING'}">
                                        <form action="process_request" method="POST" onsubmit="return confirm('Cancel this request?');" style="display:inline;">
                                            <input type="hidden" name="requestId" value="${req.id}">
                                            <input type="hidden" name="action" value="CANCEL">
                                            <button type="submit" class="btn btn-danger">Cancel</button>
                                        </form>
                                    </c:if>
                                </div>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty myRequests}">
                    <tr>
                        <td colspan="6" style="text-align: center; color: #666; font-style: italic; padding: 15px;">No requests found.</td>
                    </tr>
                    </c:if>
                    </tbody>
                </table>
            </div>

            <div class="pagination employee-pagination">
                <c:forEach begin="1" end="${totalPages}" var="i">
                    <a href="view_my_request?page=${i}&status=${selectedStatus}&type=${selectedType}&sort=${selectedSort}"
                       class="page-link ${currentPage == i ? 'active' : ''}">${i}</a>
                </c:forEach>
            </div>
        </div>
    </div>
</div>
</body>
</html>
