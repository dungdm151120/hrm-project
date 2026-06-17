<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.Request" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Department Requests | HRM</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body class="dashboard-body">

<div class="dashboard-wrapper">
    <jsp:include page="/WEB-INF/views/common/sidebar.jsp"/>

    <div class="dashboard-main">
        <div class="dashboard-header">
            <div class="header-left">
                <h1 class="header-title">Department Requests</h1>
            </div>
            <div class="header-right">
            </div>
        </div>

        <div class="dashboard-content">
            <div class="search-filter">
                <form action="${pageContext.request.contextPath}/view_department_request" method="GET">
                    <c:if test="${not empty selectedDeptId}">
                        <input type="hidden" name="deptId" value="${selectedDeptId}">
                    </c:if>

                    <select name="status" onchange="this.form.submit()">
                        <option value="" ${empty selectedStatus ? 'selected' : ''}>All Status</option>
                        <option value="PENDING" ${selectedStatus == 'PENDING' ? 'selected' : ''}>Pending</option>
                        <option value="APPROVED" ${selectedStatus == 'APPROVED' ? 'selected' : ''}>Approved</option>
                        <option value="REJECTED" ${selectedStatus == 'REJECTED' ? 'selected' : ''}>Rejected</option>
                        <option value="CANCELLED" ${selectedStatus == 'CANCELLED' ? 'selected' : ''}>Cancelled</option>
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

                    <a href="${pageContext.request.contextPath}/view_department_request${not empty selectedDeptId ? '?deptId='.concat(selectedDeptId) : ''}" class="btn-reset">Clear</a>
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
                        <th>Action</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach items="${requestList}" var="req" varStatus="r">
                        <tr>
                            <td>${req.readableType}</td>
                            <td>${req.proposerName}</td>
                            <td><span class="badge badge-${fn:toLowerCase(req.status)}">${req.status}</span></td>
                            <td><fmt:formatDate value="${req.createdAt}" pattern="dd/MM/yyyy HH:mm"/></td>
                            <td>${req.handlerName}</td>
                            <td>
                                <div class="actions">
                                    <a href="request_detail?id=${req.id}&from=dept&deptId=${selectedDeptId}" class="btn-secondary">View Detail</a>
                                </div>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty requestList}">
                        <tr>
                            <td colspan="6" style="text-align: center; color: #666; font-style: italic; padding: 15px;">No requests found for this department.</td>
                        </tr>
                    </c:if>
                    </tbody>
                </table>
            </div>

            <div class="pagination employee-pagination">
                <c:forEach begin="1" end="${totalPages}" var="i">
                    <a href="view_department_request?deptId=${selectedDeptId}&page=${i}&status=${selectedStatus}&type=${selectedType}&sort=${selectedSort}"
                       class="page-link ${currentPage == i ? 'active' : ''}">${i}</a>
                </c:forEach>
            </div>
        </div>
    </div>
</div>
</body>
</html>
