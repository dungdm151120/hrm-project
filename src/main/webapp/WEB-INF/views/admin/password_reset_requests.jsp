<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Password Reset Requests | HRM</title>
    <!-- Font Inter -->
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
    <!-- CSS chung -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body>

<jsp:include page="/WEB-INF/views/common/navbar.jsp" />

<div class="container" style="margin-top: 2rem;">
    <div class="page-header">
        <h2>Password Reset Requests</h2>
        <a href="${pageContext.request.contextPath}/home" class="btn btn-secondary">Back to Home</a>
    </div>

    <c:if test="${not empty param.success}">
        <div class="alert alert-success"><span>✓</span> ${param.success}</div>
    </c:if>
    <c:if test="${not empty param.error}">
        <div class="alert alert-error"><span>⚠</span> ${param.error}</div>
    </c:if>

    <div class="search-filter">
        <form action="${pageContext.request.contextPath}/admin/password-reset-requests" method="get">
            <input type="text" name="search" placeholder="Search user, email, or reason..." value="${search}">

            <select name="status">
                <option value="all" ${empty status ? 'selected' : ''}>All Status</option>
                <option value="PENDING" ${status == 'PENDING' ? 'selected' : ''}>Pending</option>
                <option value="APPROVED" ${status == 'APPROVED' ? 'selected' : ''}>Approved</option>
                <option value="REJECTED" ${status == 'REJECTED' ? 'selected' : ''}>Rejected</option>
            </select>

            <button type="submit" class="btn btn-primary">Search</button>
            <a href="${pageContext.request.contextPath}/admin/password-reset-requests" class="btn btn-secondary">Clear</a>
        </form>
    </div>

    <div class="table-wrapper">
        <table>
            <thead>
                <tr>
                    <th>ID</th>
                    <th>User</th>
                    <th>Email</th>
                    <th>Reason</th>
                    <th>Status</th>
                    <th>Created At</th>
                    <th>Handled At</th>
                    <th>Admin Note</th>
                    <th>Action</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach items="${resetRequests}" var="resetRequest">
                    <tr>
                        <td>${resetRequest.id}</td>
                        <td><strong>${resetRequest.fullName}</strong></td>
                        <td>${resetRequest.email}</td>
                        <td>${resetRequest.reason}</td>
                        <td>
                            <c:choose>
                                <c:when test="${resetRequest.status == 'PENDING'}">
                                    <span class="badge badge-pending">Pending</span>
                                </c:when>
                                <c:when test="${resetRequest.status == 'APPROVED'}">
                                    <span class="badge badge-active">Approved</span>
                                </c:when>
                                <c:when test="${resetRequest.status == 'REJECTED'}">
                                    <span class="badge badge-inactive">Rejected</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="badge badge-inactive">${resetRequest.status}</span>
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td>${resetRequest.createdAt}</td>
                        <td>${resetRequest.handledAt}</td>
                        <td>${resetRequest.adminNote}</td>
                        <td>
                            <c:if test="${resetRequest.status == 'PENDING'}">
                                <div class="actions">
                                    <form action="${pageContext.request.contextPath}/admin/password-reset/approve" method="post" style="display:inline;">
                                        <input type="hidden" name="id" value="${resetRequest.id}">
                                        <button type="submit" class="btn btn-save" style="padding: 0.4rem 0.85rem; font-size: 0.8rem;">Reset password</button>
                                    </form>
                                    <form action="${pageContext.request.contextPath}/admin/password-reset/reject" method="post" style="display:inline;">
                                        <input type="hidden" name="id" value="${resetRequest.id}">
                                        <input type="hidden" name="adminNote" value="Rejected by admin">
                                        <button type="submit" class="btn btn-danger" style="padding: 0.4rem 0.85rem; font-size: 0.8rem;">Reject</button>
                                    </form>
                                </div>
                            </c:if>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty resetRequests}">
                    <tr>
                        <td colspan="9" class="empty-state">No password reset requests found.</td>
                    </tr>
                </c:if>
            </tbody>
        </table>
    </div>

    <div class="pagination-wrapper">
        <div class="pagination-summary">
            Showing page ${currentPage} of ${totalPages} (${totalRecords} requests)
        </div>

        <c:if test="${totalPages > 1}">
            <div class="pagination">
                <c:url var="previousPageUrl" value="/admin/password-reset-requests">
                    <c:param name="search" value="${search}" />
                    <c:param name="status" value="${status}" />
                    <c:param name="page" value="${currentPage - 1}" />
                </c:url>
                <a class="page-link ${currentPage == 1 ? 'disabled' : ''}"
                   href="${currentPage == 1 ? '#' : previousPageUrl}">Previous</a>

                <c:forEach begin="1" end="${totalPages}" var="pageNumber">
                    <c:url var="pageUrl" value="/admin/password-reset-requests">
                        <c:param name="search" value="${search}" />
                        <c:param name="status" value="${status}" />
                        <c:param name="page" value="${pageNumber}" />
                    </c:url>
                    <a class="page-link ${pageNumber == currentPage ? 'active' : ''}" href="${pageUrl}">${pageNumber}</a>
                </c:forEach>

                <c:url var="nextPageUrl" value="/admin/password-reset-requests">
                    <c:param name="search" value="${search}" />
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
