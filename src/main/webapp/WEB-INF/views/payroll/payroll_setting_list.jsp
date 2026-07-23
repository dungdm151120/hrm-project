<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Payroll Setting List | HRM</title>
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

        .btn-delete-sm {
            padding: 4px 10px;
            font-size: 0.78rem;
            background-color: #ef4444;
            color: #ffffff;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            transition: background 0.2s;
        }
        .btn-delete-sm:hover {
            background-color: #dc2626;
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

<div class="dashboard-wrapper">
    <jsp:include page="/WEB-INF/views/common/sidebar.jsp"/>

    <div class="dashboard-main">
        <div class="dashboard-header">
            <div class="header-left">
                <h1 class="header-title">Payroll Effective Settings</h1>
            </div>
            <div class="header-right">
                <a href="${pageContext.request.contextPath}/payroll/setting/update" class="btn-save">
                    + Create New Setting
                </a>
            </div>
        </div>

        <div class="dashboard-content">
            <div style="margin-bottom: 12px;">
                <a class="back-link" href="${pageContext.request.contextPath}/payroll/update_component" style="font-size: 0.82rem;">
                    Return to payroll components
                </a>
            </div>

            <c:if test="${not empty sessionScope.message}">
                <div class="alert alert-success" style="padding: 8px 12px; font-size: 0.85rem;">${sessionScope.message}</div>
                <% session.removeAttribute("message"); %>
            </c:if>
            <c:if test="${not empty sessionScope.error}">
                <div class="alert alert-error" style="padding: 8px 12px; font-size: 0.85rem;">${sessionScope.error}</div>
                <% session.removeAttribute("error"); %>
            </c:if>

            <div class="filter-card">
                <form action="${pageContext.request.contextPath}/payroll/setting/list" method="GET" class="filter-form">
                    <div class="filter-group">
                        <label class="filter-label">Month:</label>
                        <select name="month" class="filter-select" onchange="this.form.submit()">
                            <option value="">All months</option>
                            <c:forEach var="m" begin="1" end="12">
                                <option value="${m}" ${month == m ? 'selected' : ''}>Month ${m}</option>
                            </c:forEach>
                        </select>
                    </div>

                    <div class="filter-group">
                        <label class="filter-label">Year:</label>
                        <select name="year" class="filter-select" onchange="this.form.submit()">
                            <option value="">All years</option>
                            <option value="2025" ${year == 2025 ? 'selected' : ''}>2025</option>
                            <option value="2026" ${year == 2026 ? 'selected' : ''}>2026</option>
                            <option value="2027" ${year == 2027 ? 'selected' : ''}>2027</option>
                        </select>
                    </div>

                    <button type="submit" class="search-btn" style="padding: 6px 14px; font-size: 0.82rem;">Filter</button>

                    <c:if test="${not empty month || not empty year}">
                        <a href="${pageContext.request.contextPath}/payroll/setting/list" class="btn-reset" style="font-size: 0.82rem;">
                            Clear Filters
                        </a>
                    </c:if>
                </form>
            </div>

            <div class="table-wrapper" style="background: #ffffff; border-radius: 8px; padding: 8px 12px; border: 1px solid #e2e8f0;">
                <table>
                    <thead>
                        <tr style="background: #f8fafc;">
                            <th style="text-align: center; width: 60px;">No.</th>
                            <th>Effective Date</th>
                            <th style="text-align: center; width: 160px;">Status</th>
                            <th style="text-align: center; width: 140px;">Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="st" items="${settings}" varStatus="loop">
                            <tr>
                                <td style="text-align: center; font-size: 0.85rem;">
                                    ${(currentPage - 1) * 10 + loop.index + 1}
                                </td>
                                <td>
                                    <strong style="color: #1e293b; font-size: 0.88rem;">${st.effectiveDate}</strong>
                                </td>
                                <td style="text-align: center;">
                                    <c:choose>
                                        <c:when test="${st.effectiveDate > today}">
                                            <span class="badge-status badge-upcoming-status">⏱ UPCOMING</span>
                                        </c:when>

                                        <c:when test="${st.id == activeId}">
                                            <span class="badge-status badge-active-status">● ACTIVE</span>
                                        </c:when>

                                        <c:otherwise>
                                            <span class="badge-status badge-history-status">HISTORY</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td style="text-align: center;">
                                    <a href="${pageContext.request.contextPath}/payroll/setting?id=${st.id}"
                                       class="btn-save"
                                       style="padding: 4px 10px; font-size: 0.78rem;">
                                        View Details
                                    </a>
                                    <form action="${pageContext.request.contextPath}/payroll/setting/delete" method="POST" style="display: inline;" onsubmit="return confirm('Are you sure you want to delete this payroll setting?');">
                                        <input type="hidden" name="id" value="${st.id}">
                                        <button type="submit" class="btn-delete-sm">
                                            Delete
                                        </button>
                                    </form>
                                </td>
                            </tr>
                        </c:forEach>

                        <c:if test="${empty settings}">
                            <tr>
                                <td colspan="4" class="empty-state" style="text-align: center; padding: 32px; color: #64748b;">
                                    No payroll setting records found for the selected criteria.
                                </td>
                            </tr>
                        </c:if>
                    </tbody>
                </table>

                <c:if test="${not empty settings}">
                    <div class="pagination-wrapper">
                        <div class="pagination-info">
                            Showing page <strong>${currentPage}</strong> of <strong>${totalPages}</strong> (${totalRecords} records)
                        </div>

                        <div class="pagination-nav">
                            <a href="${pageContext.request.contextPath}/payroll/setting/list?page=${currentPage - 1}&month=${month}&year=${year}"
                               class="page-link ${currentPage == 1 ? 'disabled' : ''}">
                                ‹ Prev
                            </a>

                            <c:forEach var="p" begin="1" end="${totalPages}">
                                <a href="${pageContext.request.contextPath}/payroll/setting/list?page=${p}&month=${month}&year=${year}"
                                   class="page-link ${currentPage == p ? 'active' : ''}">
                                    ${p}
                                </a>
                            </c:forEach>

                            <a href="${pageContext.request.contextPath}/payroll/setting/list?page=${currentPage + 1}&month=${month}&year=${year}"
                               class="page-link ${currentPage == totalPages ? 'disabled' : ''}">
                                Next ›
                            </a>
                        </div>
                    </div>
                </c:if>

            </div>
        </div>
    </div>
</div>

</body>
</html>