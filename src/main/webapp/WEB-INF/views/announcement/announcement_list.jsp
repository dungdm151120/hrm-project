<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Announcements | HRM</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body class="dashboard-body">

<div class="dashboard-wrapper">
    <jsp:include page="/WEB-INF/views/common/sidebar.jsp"/>

    <div class="dashboard-main">
        <div class="dashboard-header">
            <div class="header-left">
                <h1 class="header-title">Announcements</h1>
            </div>
            <div class="header-right">
                <c:if test="${canCreateAnnouncement}">
                    <a href="${pageContext.request.contextPath}/announcements/add" class="btn-primary">Create Announcement</a>
                </c:if>
            </div>
        </div>

        <div class="dashboard-content">
            <div class="search-filter">
                <form action="${pageContext.request.contextPath}/announcements" method="get">
                    <input type="text" name="search" placeholder="Search by title..." value="${fn:escapeXml(search)}">

                    <select name="scope">
                        <option value="ALL" ${scope == 'ALL' ? 'selected' : ''}>All announcements</option>
                        <option value="MY_DEPARTMENT" ${scope == 'MY_DEPARTMENT' ? 'selected' : ''}>My department</option>
                    </select>

                    <select name="readStatus">
                        <option value="ALL" ${readStatus == 'ALL' ? 'selected' : ''}>All status</option>
                        <option value="UNREAD" ${readStatus == 'UNREAD' ? 'selected' : ''}>Unread</option>
                        <option value="READ" ${readStatus == 'READ' ? 'selected' : ''}>Read</option>
                    </select>

                    <button type="submit" class="search-btn">Search</button>
                    <a href="${pageContext.request.contextPath}/announcements" class="btn-reset">Clear</a>
                </form>
            </div>

            <div class="table-wrapper">
                <table>
                    <thead>
                    <tr>
                        <th>Title</th>
                        <th>Target</th>
                        <th>Status</th>
                        <th>Publish Date</th>
                        <th>Created By</th>
                        <th>Actions</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach items="${announcements}" var="announcement">
                        <tr>
                            <td><strong><c:out value="${announcement.title}"/></strong></td>
                            <td>
                                <c:choose>
                                    <c:when test="${announcement.targetScope == 'ALL'}">
                                        <span class="badge badge-active">All employees</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="badge badge-pending"><c:out value="${announcement.targetDisplay}"/></span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                <c:choose>
                                    <c:when test="${announcement.read}">
                                        <span class="badge badge-active">Read</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="badge badge-pending">Unread</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td>${announcement.publishDateDisplay}</td>
                            <td><c:out value="${announcement.creatorName}"/></td>
                            <td>
                                <div class="actions">
                                    <a href="${pageContext.request.contextPath}/announcements/detail?id=${announcement.id}">View Detail</a>
                                </div>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty announcements}">
                        <tr>
                            <td colspan="6" class="empty-state">No announcements found.</td>
                        </tr>
                    </c:if>
                    </tbody>
                </table>
            </div>

            <div class="pagination-wrapper">
                <div class="pagination-summary">
                    Showing page ${currentPage} of ${totalPages} (${totalRecords} announcements)
                </div>

                <c:if test="${totalPages > 1}">
                    <div class="pagination">
                        <c:url var="previousPageUrl" value="/announcements">
                            <c:param name="search" value="${search}" />
                            <c:param name="scope" value="${scope}" />
                            <c:param name="readStatus" value="${readStatus}" />
                            <c:param name="page" value="${currentPage - 1}" />
                        </c:url>
                        <a class="page-link ${currentPage == 1 ? 'disabled' : ''}"
                           href="${currentPage == 1 ? '#' : previousPageUrl}">Previous</a>

                        <c:forEach begin="1" end="${totalPages}" var="pageNumber">
                            <c:url var="pageUrl" value="/announcements">
                                <c:param name="search" value="${search}" />
                                <c:param name="scope" value="${scope}" />
                                <c:param name="readStatus" value="${readStatus}" />
                                <c:param name="page" value="${pageNumber}" />
                            </c:url>
                            <a class="page-link ${pageNumber == currentPage ? 'active' : ''}" href="${pageUrl}">${pageNumber}</a>
                        </c:forEach>

                        <c:url var="nextPageUrl" value="/announcements">
                            <c:param name="search" value="${search}" />
                            <c:param name="scope" value="${scope}" />
                            <c:param name="readStatus" value="${readStatus}" />
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
