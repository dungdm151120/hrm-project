<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Payroll PIT Versions | HRM</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body class="dashboard-body">

<div class="dashboard-wrapper">
    <jsp:include page="/WEB-INF/views/common/sidebar.jsp"/>

    <div class="dashboard-main">
        <div class="dashboard-header">
            <div class="header-left">
                <h1 class="header-title">Payroll PIT Versions</h1>
            </div>
            <div class="header-right">
                <a href="${pageContext.request.contextPath}/payroll/pit/update" class="btn-save">+ Create New Version</a>
            </div>
        </div>

        <div class="dashboard-content">
            <a class="back-link" href="${pageContext.request.contextPath}/payroll/update_component">Return to payroll component list</a>

            <c:if test="${not empty sessionScope.message}">
                <div class="alert alert-success">${sessionScope.message}</div>
                <% session.removeAttribute("message"); %>
            </c:if>
            <c:if test="${not empty sessionScope.error}">
                <div class="alert alert-error">${sessionScope.error}</div>
                <% session.removeAttribute("error"); %>
            </c:if>

            <div class="search-filter">
                <form action="${pageContext.request.contextPath}/payroll/pit/list" method="GET">
                    <select name="month" onchange="this.form.submit()">
                        <option value="">All months</option>
                        <c:forEach var="m" begin="1" end="12">
                            <option value="${m}" ${month == m ? 'selected' : ''}>Month ${m}</option>
                        </c:forEach>
                    </select>

                    <select name="year" onchange="this.form.submit()">
                        <option value="">All years</option>
                        <option value="2025" ${year == 2025 ? 'selected' : ''}>2025</option>
                        <option value="2026" ${year == 2026 ? 'selected' : ''}>2026</option>
                    </select>

                    <button type="submit" class="search-btn">Filter</button>
                    <c:if test="${not empty month || not empty year}">
                        <a href="${pageContext.request.contextPath}/payroll/pit/list" class="btn-reset">Clear Filters</a>
                    </c:if>
                </form>
            </div>

            <div class="table-wrapper">
                <table>
                    <thead>
                        <tr>
                            <th style="text-align: center; width: 60px;">No.</th>
                            <th style="text-align: center;">Version Name</th>
                            <th>Effective Date</th>
                            <th style="text-align: center;">Status</th>
                            <th style="text-align: center;">Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="version" items="${versions}" varStatus="loop">
                            <tr>
                                <td style="text-align: center;">${(currentPage - 1) * 10 + loop.index + 1}</td>
                                <td style="text-align: center;">${version.versionName}</td>
                                <td><strong>${version.effectiveDate}</strong></td>
                                <td style="text-align: center;">
                                    <c:choose>
                                        <c:when test="${version.id == latestId}">
                                            <span class="badge badge-active">ACTIVE</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="badge badge-inactive">HISTORY</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td style="text-align: center;">
                                    <div class="actions" style="justify-content: center;">
                                        <a href="${pageContext.request.contextPath}/payroll/pit?id=${version.id}&effectiveDate=${version.effectiveDate}" class="btn-save">
                                            View Details
                                        </a>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty versions}">
                            <tr>
                                <td colspan="5" class="empty-state" style="text-align: center; padding: 30px;">
                                    No PIT version records found.
                                </td>
                            </tr>
                        </c:if>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>

</body>
</html>