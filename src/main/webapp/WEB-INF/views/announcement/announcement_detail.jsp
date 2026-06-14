<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Announcement Detail | HRM</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    <style>
        .announcement-content {
            white-space: pre-wrap;
            line-height: 1.7;
        }
    </style>
</head>
<body class="dashboard-body">

<div class="dashboard-wrapper">
    <jsp:include page="/WEB-INF/views/common/sidebar.jsp"/>

    <div class="dashboard-main">
        <div class="dashboard-header">
            <div class="header-left">
                <h1 class="header-title">Announcement Detail</h1>
            </div>
        </div>

        <div class="dashboard-content">
            <a class="back-link" href="${pageContext.request.contextPath}/announcements">Back to announcements</a>

            <div class="role-detail">
                <div class="role-meta" style="width: 100%;">
                    <span class="role-meta-label">Title</span>
                    <span class="role-meta-value"><c:out value="${announcement.title}"/></span>
                </div>
                <div class="role-meta">
                    <span class="role-meta-label">Target</span>
                    <span class="role-meta-value"><c:out value="${announcement.targetDisplay}"/></span>
                </div>
                <div class="role-meta">
                    <span class="role-meta-label">Publish Date</span>
                    <span class="role-meta-value">${announcement.publishDateDisplay}</span>
                </div>
                <div class="role-meta">
                    <span class="role-meta-label">Read At</span>
                    <span class="role-meta-value">${announcement.readAtDisplay}</span>
                </div>
                <div class="role-meta">
                    <span class="role-meta-label">Created By</span>
                    <span class="role-meta-value"><c:out value="${announcement.creatorName}"/></span>
                </div>
                <div class="role-meta">
                    <span class="role-meta-label">Created At</span>
                    <span class="role-meta-value">${announcement.createdAtDisplay}</span>
                </div>
            </div>

            <div class="role-detail">
                <div class="role-meta" style="width: 100%;">
                    <span class="role-meta-label">Content</span>
                    <span class="role-meta-value announcement-content"><c:out value="${announcement.content}"/></span>
                </div>
            </div>
        </div>
    </div>
</div>

</body>
</html>
