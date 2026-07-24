<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Labor Contracts | HRM</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body class="dashboard-body">

<div class="dashboard-wrapper">

    <jsp:include page="/WEB-INF/views/common/sidebar.jsp"/>

    <div class="dashboard-main">
        <div class="dashboard-header">
            <div class="header-left">
                <h1 class="header-title">Labor Contracts</h1>
            </div>
            <div class="header-right">
                <a href="${pageContext.request.contextPath}/home" class="btn-secondary">Back to Home</a>
                <c:if test="${canCreateContract}">
                    <a href="${pageContext.request.contextPath}/contracts/add" class="btn-primary">Add Contract</a>
                </c:if>
            </div>
        </div>

        <div class="dashboard-content">
            <div class="search-filter">
                <form action="${pageContext.request.contextPath}/contracts" method="get">
                    <input type="text" name="search" placeholder="Search code, employee, or email..." value="${search}">

                    <select name="contractType">
                        <option value="all" ${empty contractType ? 'selected' : ''}>All Types</option>
                        <option value="FIXED_TERM" ${contractType == 'FIXED_TERM' ? 'selected' : ''}>FIXED_TERM</option>
                        <option value="INDEFINITE_TERM" ${contractType == 'INDEFINITE_TERM' ? 'selected' : ''}>INDEFINITE_TERM</option>
                        <option value="PROBATION" ${contractType == 'PROBATION' ? 'selected' : ''}>PROBATION</option>
                    </select>

                    <select name="status">
                        <option value="all" ${empty status ? 'selected' : ''}>All Status</option>
                        <option value="ACTIVE" ${status == 'ACTIVE' ? 'selected' : ''}>ACTIVE</option>
                        <option value="EXPIRED" ${status == 'EXPIRED' ? 'selected' : ''}>EXPIRED</option>
                        <option value="TERMINATED" ${status == 'TERMINATED' ? 'selected' : ''}>TERMINATED</option>
                    </select>

                    <button type="submit" class="search-btn">Search</button>
                    <a href="${pageContext.request.contextPath}/contracts" class="btn-reset">Clear</a>
                </form>
            </div>

            <div class="table-wrapper">
                <table>
                    <thead>
                    <tr>
                        <th>Code</th>
                        <th>Employee</th>
                        <th>Type</th>
                        <th>Start Date</th>
                        <th>End Date</th>
                        <th>Status</th>
                        <th>Actions</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach items="${contracts}" var="contract">
                        <tr>
                            <td><strong>${contract.contractCode}</strong></td>
                            <td>
                                ${contract.employeeName}
                                <c:if test="${not empty contract.employeeCode}">
                                    (${contract.employeeCode})
                                </c:if>
                            </td>
                            <td>${contract.contractType}</td>
                            <td>${contract.startDateDisplay}</td>
                            <td>
                                <c:choose>
                                    <c:when test="${not empty contract.endDate}">${contract.endDateDisplay}</c:when>
                                    <c:otherwise>Open-ended</c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                <c:choose>
                                    <c:when test="${contract.status == 'ACTIVE'}">
                                        <span class="badge badge-active">ACTIVE</span>
                                    </c:when>
                                    <c:when test="${contract.status == 'TERMINATED'}">
                                        <span class="badge badge-inactive">TERMINATED</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="badge badge-pending">${contract.status}</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                <div class="actions">
                                    <a href="${pageContext.request.contextPath}/contracts/detail?id=${contract.id}">View Detail</a>
                                    <c:if test="${canUpdateContract && contract.status == 'ACTIVE'}">
                                        <a href="${pageContext.request.contextPath}/contracts/update?id=${contract.id}">Update</a>
                                    </c:if>
                                </div>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty contracts}">
                        <tr>
                            <td colspan="7" class="empty-state">No contracts found.</td>
                        </tr>
                    </c:if>
                    </tbody>
                </table>
            </div>

            <div class="pagination-wrapper">
                <div class="pagination-summary">
                    Showing page ${currentPage} of ${totalPages} (${totalRecords} contracts)
                </div>

                <c:if test="${totalPages > 1}">
                    <div class="pagination">
                        <c:url var="previousPageUrl" value="/contracts">
                            <c:param name="search" value="${search}" />
                            <c:param name="contractType" value="${contractType}" />
                            <c:param name="status" value="${status}" />
                            <c:param name="page" value="${currentPage - 1}" />
                        </c:url>
                        <a class="page-link ${currentPage == 1 ? 'disabled' : ''}"
                           href="${currentPage == 1 ? '#' : previousPageUrl}">Previous</a>

                        <c:forEach begin="1" end="${totalPages}" var="pageNumber">
                            <c:url var="pageUrl" value="/contracts">
                                <c:param name="search" value="${search}" />
                                <c:param name="contractType" value="${contractType}" />
                                <c:param name="status" value="${status}" />
                                <c:param name="page" value="${pageNumber}" />
                            </c:url>
                            <a class="page-link ${pageNumber == currentPage ? 'active' : ''}" href="${pageUrl}">${pageNumber}</a>
                        </c:forEach>

                        <c:url var="nextPageUrl" value="/contracts">
                            <c:param name="search" value="${search}" />
                            <c:param name="contractType" value="${contractType}" />
                            <c:param name="status" value="${status}" />
                            <c:param name="page" value="${currentPage + 1}" />
                        </c:url>
                        <a class="page-link ${currentPage == totalPages ? 'disabled' : ''}"
                           href="${currentPage == totalPages ? '#' : nextPageUrl}">Next</a>
                    </div>
                </c:if>
            </div>
        </div>
    </div>
</div>

</body>
</html>
