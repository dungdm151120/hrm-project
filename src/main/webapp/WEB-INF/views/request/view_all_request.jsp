<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.Request" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>All Requests | HRM</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body class="dashboard-body">

<div class="dashboard-wrapper">
    <jsp:include page="/WEB-INF/views/common/sidebar.jsp"/>

    <div class="dashboard-main">
        <div class="dashboard-header">
            <div class="header-left">
                <h1 class="header-title">All Requests</h1>
            </div>
        </div>

        <div class="dashboard-content">
            <div class="search-filter">
                <form action="${pageContext.request.contextPath}/view_all_request" method="GET">
                    <select name="status" onchange="this.form.submit()">
                        <option value="" ${empty selectedStatus ? 'selected' : ''}>All Status</option>
                        <option value="PENDING" ${selectedStatus == 'PENDING' ? 'selected' : ''}>Pending</option>
                        <option value="APPROVED" ${selectedStatus == 'APPROVED' ? 'selected' : ''}>Approved</option>
                        <option value="REJECTED" ${selectedStatus == 'REJECTED' ? 'selected' : ''}>Rejected</option>
                        <option value="CLOSED" ${selectedStatus == 'CLOSED' ? 'selected' : ''}>Closed</option>
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

                    <button type="submit" class="btn-primary">Filter</button>
                    <a href="${pageContext.request.contextPath}/view_all_request" class="btn-reset">Clear</a>
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
                    <c:forEach var="req" items="${requestList}">
                        <tr>
                            <td>${req.readableType}</td>
                            <td>${req.proposerName}</td>
                            <td><span class="badge badge-${fn:toLowerCase(req.status)}">${req.status}</span></td>
                            <td><fmt:formatDate value="${req.createdAt}" pattern="dd/MM/yyyy HH:mm"/></td>
                            <td>${req.handlerName}</td>
                            <td>
                                <c:choose>
                                    <c:when test="${req.status != 'PENDING' and not empty req.processedAt}">
                                        <fmt:formatDate value="${req.processedAt}" pattern="dd/MM/yyyy HH:mm"/>
                                    </c:when>
                                    <c:otherwise>
                                        <span>-</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                <div class="actions">
                                    <a href="request_detail?id=${req.id}" class="btn-secondary">View Detail</a>
                                </div>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty requestList}">
                        <tr>
                            <td colspan="6" style="text-align: center; color: #666; font-style: italic; padding: 15px;">No requests found.</td>
                        </tr>
                    </c:if>
                    </tbody>
                </table>
            </div>

            <div class="pagination employee-pagination">
                <c:forEach begin="1" end="${totalPages}" var="i">
                    <a href="view_all_request?page=${i}&status=${selectedStatus}&type=${selectedType}&sort=${selectedSort}"
                       class="page-link ${currentPage == i ? 'active' : ''}">${i}</a>
                </c:forEach>
            </div>
        </div>
    </div>
</div>
</body>
</html>
