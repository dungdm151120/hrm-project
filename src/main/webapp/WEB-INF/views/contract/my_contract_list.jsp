<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Contracts | HRM</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body>

<jsp:include page="/WEB-INF/views/common/navbar.jsp" />

<div class="container" style="margin-top: 2rem;">
    <div class="page-header">
        <h2>My Contracts</h2>
        <a href="${pageContext.request.contextPath}/home" class="btn btn-secondary">Back to Home</a>
    </div>

    <div class="search-filter">
        <form action="${pageContext.request.contextPath}/my-contract" method="get">
            <select name="status">
                <option value="all" ${empty status ? 'selected' : ''}>All Status</option>
                <option value="ACTIVE" ${status == 'ACTIVE' ? 'selected' : ''}>ACTIVE</option>
                <option value="EXPIRED" ${status == 'EXPIRED' ? 'selected' : ''}>EXPIRED</option>
                <option value="TERMINATED" ${status == 'TERMINATED' ? 'selected' : ''}>TERMINATED</option>
            </select>

            <button type="submit" class="btn btn-primary">Filter</button>
            <a href="${pageContext.request.contextPath}/my-contract" class="btn btn-reset">Clear</a>
        </form>
    </div>

    <div class="table-wrapper">
        <table>
            <thead>
            <tr>
                <th>Code</th>
                <th>Type</th>
                <th>Start Date</th>
                <th>End Date</th>
                <th>Base Salary</th>
                <th>Status</th>
                <th>Actions</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach items="${contracts}" var="contract">
                <tr>
                    <td><strong>${contract.contractCode}</strong></td>
                    <td>${contract.contractType}</td>
                    <td>${contract.startDate}</td>
                    <td>
                        <c:choose>
                            <c:when test="${not empty contract.endDate}">${contract.endDate}</c:when>
                            <c:otherwise>Open-ended</c:otherwise>
                        </c:choose>
                    </td>
                    <td>${contract.baseSalary}</td>
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
                        <a href="${pageContext.request.contextPath}/my-contract/detail?id=${contract.id}">View Detail</a>
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
                <c:url var="previousPageUrl" value="/my-contract">
                    <c:param name="status" value="${status}" />
                    <c:param name="page" value="${currentPage - 1}" />
                </c:url>
                <a class="page-link ${currentPage == 1 ? 'disabled' : ''}"
                   href="${currentPage == 1 ? '#' : previousPageUrl}">Previous</a>

                <c:forEach begin="1" end="${totalPages}" var="pageNumber">
                    <c:url var="pageUrl" value="/my-contract">
                        <c:param name="status" value="${status}" />
                        <c:param name="page" value="${pageNumber}" />
                    </c:url>
                    <a class="page-link ${pageNumber == currentPage ? 'active' : ''}" href="${pageUrl}">${pageNumber}</a>
                </c:forEach>

                <c:url var="nextPageUrl" value="/my-contract">
                    <c:param name="status" value="${status}" />
                    <c:param name="page" value="${currentPage + 1}" />
                </c:url>
                <a class="page-link ${currentPage == totalPages ? 'disabled' : ''}"
                   href="${currentPage == totalPages ? '#' : nextPageUrl}">Next</a>
            </div>
        </c:if>
    </div>
</div>

</body>
</html>
