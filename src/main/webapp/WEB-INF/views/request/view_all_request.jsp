<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.Request" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<html>
<body>
<h2>All Requests</h2>

<div style="margin-bottom: 15px;">
    <form id="allFilterForm" action="view_all_requests" method="GET">
        <label for="status">Status:</label>
        <select name="status" id="status">
            <option value="" ${empty selectedStatus ? 'selected' : ''}>All Status</option>
            <option value="PENDING" ${selectedStatus == 'PENDING' ? 'selected' : ''}>Pending</option>
            <option value="APPROVED" ${selectedStatus == 'APPROVED' ? 'selected' : ''}>Approved</option>
            <option value="REJECTED" ${selectedStatus == 'REJECTED' ? 'selected' : ''}>Rejected</option>
            <option value="CLOSED" ${selectedStatus == 'CLOSED' ? 'selected' : ''}>Closed</option>
        </select>

        <label for="type" style="margin-left: 10px;">Type:</label>
        <select name="type" id="type">
            <option value="" ${empty selectedType ? 'selected' : ''}>All Types</option>
            <c:forEach items="<%= Request.getAllType() %>" var="entry">
                <option value="${entry.key}" ${selectedType == entry.key ? 'selected' : ''}>
                        ${entry.value}
                </option>
            </c:forEach>
        </select>

        <label for="sort" style="margin-left: 10px;">Sort By:</label>
        <select name="sort" id="sort" onchange="document.getElementById('allFilterForm').submit();">
            <option value="newest" ${selectedSort == 'newest' ? 'selected' : ''}>Newest</option>
            <option value="oldest" ${selectedSort == 'oldest' ? 'selected' : ''}>Oldest</option>
        </select>

        <button type="submit" style="margin-left: 10px;">Filter</button>
        <a href="view_all_request" style="margin-left: 5px;">Clear Filters</a>
    </form>
</div>

<table border="1" cellpadding="5" cellspacing="0">
    <thead>
    <tr>
        <th>ID</th>
        <th>Proposer</th>
        <th>Type</th>
        <th>Status</th>
        <th>Created At</th>
        <th>Actions</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach var="req" items="${requestList}">
        <tr>
            <td>${req.id}</td>
            <td>${req.proposerName}</td>
            <td>${req.readableType}</td>
            <td>${req.status}</td>
            <td><fmt:formatDate value="${req.createdAt}" pattern="dd/MM/yyyy HH:mm"/></td>
            <td>
                <a href="request_detail?id=${req.id}&from=all&status=${selectedStatus}&type=${selectedType}&sort=${selectedSort}&page=${currentPage}">View Detail</a>
                <c:if test="${req.status == 'APPROVED' || req.status == 'REJECTED'}">
                    <form action="process_request" method="POST" style="display:inline;" onsubmit="return confirm('Close this request?');">
                        <input type="hidden" name="requestId" value="${req.id}">
                        <input type="hidden" name="action" value="CLOSE">
                        <button type="submit">Close</button>
                    </form>
                </c:if>
            </td>
        </tr>
    </c:forEach>

    <c:if test="${empty requestList}">
        <tr>
            <td colspan="6" style="text-align: center; color: gray; font-style: italic;">No requests found.</td>
        </tr>
    </c:if>
    </tbody>
</table>

<div class="pagination" style="margin-top: 15px;">
    <c:choose>
        <c:when test="${currentPage <= 1}">
            <span style="color: gray; margin-right: 5px;">Previous</span>
        </c:when>
        <c:otherwise>
            <c:url var="prevUrl" value="view_all_requests">
                <c:param name="status" value="${selectedStatus}"/>
                <c:param name="type" value="${selectedType}"/>
                <c:param name="sort" value="${selectedSort}"/>
                <c:param name="page" value="${currentPage - 1}"/>
            </c:url>
            <a href="${prevUrl}" style="margin-right: 5px;">Previous</a>
        </c:otherwise>
    </c:choose>

    <c:forEach begin="1" end="${totalPages}" var="i">
        <c:url var="pageUrl" value="view_all_requests">
            <c:param name="status" value="${selectedStatus}"/>
            <c:param name="type" value="${selectedType}"/>
            <c:param name="sort" value="${selectedSort}"/>
            <c:param name="page" value="${i}"/>
        </c:url>
        <a href="${pageUrl}" style="margin-right: 5px; ${currentPage == i ? 'font-weight: bold; text-decoration: underline;' : ''}">
                ${i}
        </a>
    </c:forEach>

    <c:choose>
        <c:when test="${currentPage >= totalPages}">
            <span style="color: gray;">Next</span>
        </c:when>
        <c:otherwise>
            <c:url var="nextUrl" value="view_all_request">
                <c:param name="status" value="${selectedStatus}"/>
                <c:param name="type" value="${selectedType}"/>
                <c:param name="sort" value="${selectedSort}"/>
                <c:param name="page" value="${currentPage + 1}"/>
            </c:url>
            <a href="${nextUrl}">Next</a>
        </c:otherwise>
    </c:choose>
</div>
</body>
</html>