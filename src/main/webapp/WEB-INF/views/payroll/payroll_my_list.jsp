<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Payroll History | HRM</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">

    <style>
        .filter-card {
            background: #ffffff;
            border: 1px solid #e2e8f0;
            border-radius: 8px;
            padding: 12px 16px;
            margin-bottom: 16px;
        }

        .filter-form {
            display: flex;
            align-items: center;
            gap: 12px;
            flex-wrap: wrap;
        }

        .filter-group {
            display: flex;
            align-items: center;
            gap: 6px;
        }

        .filter-label {
            font-size: 0.82rem;
            font-weight: 600;
            color: #475569;
        }

        .filter-select {
            padding: 6px 12px;
            font-size: 0.85rem;
            border: 1px solid #cbd5e1;
            border-radius: 6px;
            background-color: #ffffff;
            color: #0f172a;
            outline: none;
        }
        .filter-select:focus {
            border-color: #2563eb;
        }

        /* Status Badges */
        .badge-status {
            padding: 4px 10px;
            font-size: 0.72rem;
            font-weight: 700;
            border-radius: 12px;
            display: inline-block;
            letter-spacing: 0.5px;
            text-transform: uppercase;
        }
        .badge-active-status {
            background-color: #dcfce7;
            color: #15803d;
            border: 1px solid #bbf7d0;
        }
        .badge-upcoming-status {
            background-color: #fef3c7;
            color: #b45309;
            border: 1px solid #fde68a;
        }
        .badge-history-status {
            background-color: #f1f5f9;
            color: #64748b;
            border: 1px solid #e2e8f0;
        }

        /* Pagination bar */
        .pagination-wrapper {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-top: 16px;
            padding-top: 12px;
            border-top: 1px solid #e2e8f0;
        }

        .pagination-info {
            font-size: 0.82rem;
            color: #64748b;
        }

        .pagination-nav {
            display: flex;
            gap: 4px;
        }

        .page-link {
            padding: 5px 11px;
            font-size: 0.82rem;
            border: 1px solid #cbd5e1;
            background: #ffffff;
            color: #334155;
            border-radius: 4px;
            text-decoration: none;
            transition: all 0.15s ease;
        }

        .page-link:hover {
            background: #f1f5f9;
            border-color: #94a3b8;
        }

        .page-link.active {
            background: #2563eb;
            color: #ffffff;
            border-color: #2563eb;
            font-weight: 600;
        }

        .page-link.disabled {
            color: #cbd5e1;
            pointer-events: none;
            background: #f8fafc;
        }
    </style>
</head>
<body class="dashboard-body">

<c:set var="monthNames" value="${fn:split('January,February,March,April,May,June,July,August,September,October,November,December', ',')}" />

<div class="dashboard-wrapper">
    <jsp:include page="/WEB-INF/views/common/sidebar.jsp"/>

    <div class="dashboard-main">
        <div class="dashboard-header">
            <div class="header-left">
                <h1 class="header-title">My Payslip History</h1>
            </div>
        </div>

        <div class="dashboard-content">
            <c:if test="${not empty sessionScope.message}">
                <div class="alert alert-success" style="padding: 8px 12px; font-size: 0.85rem;">
                    ${sessionScope.message}
                </div>
                <% session.removeAttribute("message"); %>
            </c:if>
            <c:if test="${not empty sessionScope.error}">
                <div class="alert alert-error" style="padding: 8px 12px; font-size: 0.85rem;">
                    ${sessionScope.error}
                </div>
                <% session.removeAttribute("error"); %>
            </c:if>

            <div class="filter-card">
                <form action="${pageContext.request.contextPath}/payroll/my" method="GET" class="filter-form">
                    <div class="filter-group">
                        <label class="filter-label">Month:</label>
                        <select name="month" class="filter-select" onchange="this.form.submit()">
                            <option value="">All months</option>
                            <c:forEach var="m" begin="1" end="12">
                                <option value="${m}" ${month == m ? 'selected' : ''}>${monthNames[m - 1]}</option>
                            </c:forEach>
                        </select>
                    </div>

                    <div class="filter-group">
                        <label class="filter-label">Year:</label>
                        <select name="year" class="filter-select" onchange="this.form.submit()">
                            <option value="">All years</option>
                            <c:forEach var="y" begin="${currentYear - 1}" end="${currentYear + 1}">
                                <option value="${y}" ${year == y ? 'selected' : ''}>Year ${y}</option>
                            </c:forEach>
                        </select>
                    </div>

                    <button type="submit" class="search-btn" style="padding: 6px 14px; font-size: 0.82rem;">Filter</button>

                    <c:if test="${not empty month || not empty year}">
                        <a href="${pageContext.request.contextPath}/payroll/my" class="btn-reset" style="font-size: 0.82rem;">
                            Clear Filters
                        </a>
                    </c:if>
                </form>
            </div>

            <div class="table-wrapper" style="background: #ffffff; border-radius: 8px; padding: 8px 12px; border: 1px solid #e2e8f0;">
                <table>
                    <thead>
                        <tr style="background: #f8fafc;">
                            <th>Pay Period</th>
                            <th style="text-align: right;">Basic Salary</th>
                            <th style="text-align: right;">Net Pay</th>
                            <th style="text-align: center; width: 140px;">Status</th>
                            <th style="text-align: center; width: 140px;">Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="p" items="${payrollList}" varStatus="s">
                            <tr>
                                <td>
                                    <strong style="color: #1e293b; font-size: 0.88rem;">${monthNames[p.month - 1]} / ${p.year}</strong>
                                </td>
                                <td style="text-align: right; font-size: 0.85rem;">
                                    <fmt:formatNumber value="${p.basicSalary}" type="number" maxFractionDigits="0"/> VND
                                </td>
                                <td style="text-align: right; font-weight: 700; color: #2563eb; font-size: 0.88rem;">
                                    <fmt:formatNumber value="${p.netPay}" type="number" maxFractionDigits="0"/> VND
                                </td>
                                <td style="text-align: center;">
                                    <c:choose>
                                        <c:when test="${p.status == 'PAID' || p.status == 'APPROVED' || p.status == 'FINALIZED'}">
                                            <span class="badge-status badge-active-status">● ${p.status}</span>
                                        </c:when>
                                        <c:when test="${p.status == 'PENDING' || p.status == 'PROCESSING'}">
                                            <span class="badge-status badge-upcoming-status">⏱ ${p.status}</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="badge-status badge-history-status">${p.status}</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td style="text-align: center;">
                                    <a href="${pageContext.request.contextPath}/payroll/my/detail?id=${p.id}&month=${p.month}&year=${p.year}"
                                       class="btn-save"
                                       style="padding: 4px 10px; font-size: 0.78rem;">
                                        View Details
                                    </a>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty payrollList}">
                            <tr>
                                <td colspan="5" class="empty-state" style="text-align: center; padding: 32px; color: #64748b;">
                                    No payroll records found for the selected criteria.
                                </td>
                            </tr>
                        </c:if>
                    </tbody>
                </table>

                <c:if test="${not empty payrollList && totalPage > 1}">
                    <div class="pagination-wrapper">
                        <div class="pagination-info">
                            Showing page <strong>${currentPage}</strong> of <strong>${totalPage}</strong>
                        </div>

                        <div class="pagination-nav">
                            <c:url var="firstUrl" value="/payroll/my">
                                <c:param name="page" value="1"/>
                                <c:if test="${not empty month}"><c:param name="month" value="${month}"/></c:if>
                                <c:if test="${not empty year}"><c:param name="year" value="${year}"/></c:if>
                            </c:url>
                            <a href="${firstUrl}" class="page-link ${currentPage == 1 ? 'disabled' : ''}">« First</a>

                            <c:url var="prevUrl" value="/payroll/my">
                                <c:param name="page" value="${currentPage - 1}"/>
                                <c:if test="${not empty month}"><c:param name="month" value="${month}"/></c:if>
                                <c:if test="${not empty year}"><c:param name="year" value="${year}"/></c:if>
                            </c:url>
                            <a href="${prevUrl}" class="page-link ${currentPage <= 1 ? 'disabled' : ''}">‹ Prev</a>

                            <c:forEach var="pNum" begin="1" end="${totalPage}">
                                <c:url var="pageUrl" value="/payroll/my">
                                    <c:param name="page" value="${pNum}"/>
                                    <c:if test="${not empty month}"><c:param name="month" value="${month}"/></c:if>
                                    <c:if test="${not empty year}"><c:param name="year" value="${year}"/></c:if>
                                </c:url>
                                <a href="${pageUrl}" class="page-link ${currentPage == pNum ? 'active' : ''}">${pNum}</a>
                            </c:forEach>

                            <c:url var="nextUrl" value="/payroll/my">
                                <c:param name="page" value="${currentPage + 1}"/>
                                <c:if test="${not empty month}"><c:param name="month" value="${month}"/></c:if>
                                <c:if test="${not empty year}"><c:param name="year" value="${year}"/></c:if>
                            </c:url>
                            <a href="${nextUrl}" class="page-link ${currentPage >= totalPage ? 'disabled' : ''}">Next ›</a>

                            <c:url var="lastUrl" value="/payroll/my">
                                <c:param name="page" value="${totalPage}"/>
                                <c:if test="${not empty month}"><c:param name="month" value="${month}"/></c:if>
                                <c:if test="${not empty year}"><c:param name="year" value="${year}"/></c:if>
                            </c:url>
                            <a href="${lastUrl}" class="page-link ${currentPage == totalPage ? 'disabled' : ''}">Last »</a>
                        </div>
                    </div>
                </c:if>
            </div>
        </div>
    </div>
</div>

</body>
</html>