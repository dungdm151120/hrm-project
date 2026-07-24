<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%
    java.util.Set<String> userPermissions = (java.util.Set<String>) session.getAttribute("userPermissions");
    pageContext.setAttribute("userPermissions", userPermissions);
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Payroll Statements | HRM</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">

    <style>
        .search-filter form {
            display: flex;
            align-items: center;
            flex-wrap: nowrap;
            gap: 8px;
            overflow-x: auto;
            padding-bottom: 4px;
        }
        .search-filter input[type="text"],
        .search-filter select,
        .search-filter .search-btn,
        .search-filter .btn-reset {
            padding: 4px 10px !important;
            height: 34px !important;
            font-size: 0.82rem !important;
            white-space: nowrap;
        }
        .search-filter input[type="text"] {
            min-width: 140px;
        }

        /* Tăng padding giúp các cột không bị dính ríu vào nhau */
        .table-wrapper table th,
        .table-wrapper table td {
            padding: 8px 12px !important;
            font-size: 0.82rem !important;
            white-space: nowrap;
        }

        .table-wrapper table th:first-child,
        .table-wrapper table td:first-child {
            white-space: normal;
            min-width: 160px;
        }

        .table-wrapper table tfoot tr {
            background-color: #f1f5f9;
            font-weight: bold;
            border-top: 2px solid #cbd5e1;
            border-bottom: 2px solid #cbd5e1;
        }
        .table-wrapper table tfoot td {
            color: #0f172a;
            padding: 10px 12px !important;
        }
        .table-wrapper table .badge {
            font-size: 0.68rem !important;
            padding: 3px 8px !important;
            line-height: 1 !important;
            display: inline-block !important;
            border-radius: 4px !important;
            font-weight: 600 !important;
            letter-spacing: 0.3px;
        }

        /* Style sắp xếp nút View và Confirm nằm ngang */
        .action-group {
            display: flex !important;
            flex-direction: row !important;
            align-items: center !important;
            justify-content: center !important;
            gap: 6px !important;
        }
        .action-group form {
            margin: 0 !important;
            padding: 0 !important;
            display: inline-flex !important;
        }
        .btn-action {
            padding: 3px 8px !important;
            font-size: 0.75rem !important;
            line-height: 1.2 !important;
            border-radius: 4px !important;
            text-decoration: none !important;
            cursor: pointer;
            white-space: nowrap;
        }
        .text-plus, .text-success {
            color: #059669 !important;
            font-weight: 600;
        }

        .text-minus, .text-danger {
            color: #dc2626 !important;
            font-weight: 600;
        }
    </style>
</head>
<body class="dashboard-body">

<div class="dashboard-wrapper">

    <jsp:include page="/WEB-INF/views/common/sidebar.jsp"/>

    <div class="dashboard-main">
        <div class="dashboard-header">
            <div class="header-left">
                <h1 class="header-title">Payroll List</h1>
            </div>
            <div class="header-right">
                <c:if test="${userPermissions.contains('PAYROLL_CONFIRM')}">
                    <form action="${pageContext.request.contextPath}/payroll/confirm" method="POST" onsubmit="return confirm('Confirm all payrolls?')">
                        <input type="hidden" name="all" value="all">
                        <input type="hidden" name="redirectDepartmentId" value="${currentDeptParam}">
                        <input type="hidden" name="redirectMonth" value="${month}">
                        <input type="hidden" name="redirectYear" value="${year}">

                        <button type="submit" class="btn-save" style="padding: 6px 12px; font-size: 0.85rem;">Confirm All</button>
                    </form>
                </c:if>
            </div>
        </div>

        <div class="dashboard-content">

            <c:if test="${not empty sessionScope.message}">
                <div class="alert alert-success">${sessionScope.message}</div>
                <% session.removeAttribute("message"); %>
            </c:if>
            <c:if test="${not empty sessionScope.error}">
                <div class="alert alert-error">${sessionScope.error}</div>
                <% session.removeAttribute("error"); %>
            </c:if>

            <div class="search-filter">
                <form action="${pageContext.request.contextPath}/payroll/list" method="GET">
                    <c:set var="monthNames" value="${fn:split('January,February,March,April,May,June,July,August,September,October,November,December', ',')}" />

                    <input type="text" name="search" placeholder="Search name..." value="${keyword}">

                    <select name="departmentId" onchange="this.form.submit()">
                        <option value="all" ${empty requestScope.departmentId ? 'selected' : ''}>All Departments</option>

                        <c:forEach var="dept" items="${departmentList}">
                            <option value="${dept.id}" ${requestScope.departmentId == dept.id ? 'selected' : ''}>
                                <c:out value="${dept.name}" />
                            </option>
                        </c:forEach>
                    </select>

                    <select name="month" onchange="this.form.submit()">
                        <option value="">All Months</option>
                        <c:forEach var="m" begin="1" end="12">
                            <option value="${m}" ${month == m ? 'selected' : ''}>${monthNames[m - 1]}</option>
                        </c:forEach>
                    </select>

                    <select name="year" onchange="this.form.submit()">
                        <option value="">All Years</option>
                        <c:set var="cYear" value="${not empty currentYear ? currentYear : 2026}" />
                        <c:forEach var="y" begin="${cYear - 3}" end="${cYear + 1}">
                            <option value="${y}" ${year == y ? 'selected' : ''}>${y}</option>
                        </c:forEach>
                    </select>

                    <select name="status" onchange="this.form.submit()">
                        <option value="all" ${status == 'all' || empty status ? 'selected' : ''}>All status</option>
                        <option value="draft" ${status == 'draft' ? 'selected' : ''}>Draft</option>
                        <option value="confirmed" ${status == 'confirmed' ? 'selected' : ''}>Confirmed</option>
                    </select>

                    <select name="sort" onchange="this.form.submit()">
                        <option value="name_asc" ${sort == 'name_asc' ? 'selected' : ''}>Name A-Z</option>
                        <option value="name_desc" ${sort == 'name_desc' ? 'selected' : ''}>Name Z-A</option>
                    </select>

                    <button type="submit" class="search-btn">Search</button>

                    <c:if test="${not empty keyword || (not empty status && status != 'all') || not empty month || not empty year || not empty departmentId}">
                        <a href="${pageContext.request.contextPath}/payroll/list" class="btn-reset">Clear</a>
                    </c:if>
                </form>
            </div>

            <div class="table-wrapper">
                <table>
                    <thead>
                        <tr>
                            <th>Employee Details</th>
                            <th style="text-align: right;">Basic Salary</th>
                            <th style="text-align: center;">Hours (Act/Exp)</th>
                            <th style="text-align: right;">Act. Base Inc.</th>
                            <th style="text-align: right;" class="text-plus">+Allowance</th>
                            <th style="text-align: right;">Gross Income</th>
                            <th style="text-align: right;" class="text-minus">-Social Ins.</th>
                            <th style="text-align: right;" class="text-minus">-Health Ins.</th>
                            <th style="text-align: right;" class="text-minus">-Unemp. Ins.</th>
                            <th style="text-align: right;" class="text-minus">-Union Fee</th>
                            <th style="text-align: right;">Inc. Before Tax</th>
                            <th style="text-align: right;">Taxable Inc.</th>
                            <th style="text-align: right;" class="text-minus">-PIT</th>
                            <th style="text-align: right;" class="text-plus">+OT Pay</th>
                            <th style="text-align: right;" class="text-plus">+Sick Pay</th>
                            <th style="text-align: right;">Net Pay</th>
                            <th style="text-align: center;">Status</th>
                            <th style="text-align: center;">Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="p" items="${payrollList}">
                            <tr>
                                <td>
                                    <strong><c:out value="${p.employeeName}" default="N/A" /></strong>
                                    <div style="font-size: 0.8em;" class="text-muted">
                                        <c:out value="${p.departmentName}" default="N/A" /> - <c:out value="${p.positionName}" default="N/A" />
                                    </div>
                                </td>

                                <td style="text-align: right;">
                                    <fmt:formatNumber value="${p.basicSalary}" type="number" maxFractionDigits="0"/>
                                </td>

                                <td style="text-align: center;">
                                    <strong>${p.actualHours}</strong> / ${p.expectedHours}
                                </td>

                                <td style="text-align: right;">
                                    <fmt:formatNumber value="${p.actualBasicSalary}" type="number" maxFractionDigits="0"/>
                                </td>

                                <td style="text-align: right;">
                                    <fmt:formatNumber value="${p.bonus}" type="number" maxFractionDigits="0"/>
                                </td>

                                <td style="text-align: right;">
                                    <strong><fmt:formatNumber value="${p.totalIncome}" type="number" maxFractionDigits="0"/></strong>
                                </td>

                                <td style="text-align: right;">
                                    <fmt:formatNumber value="${p.socialInsurance}" type="number" maxFractionDigits="0"/>
                                </td>

                                <td style="text-align: right;">
                                    <fmt:formatNumber value="${p.healthInsurance}" type="number" maxFractionDigits="0"/>
                                </td>

                                <td style="text-align: right;">
                                    <fmt:formatNumber value="${p.unemploymentInsurance}" type="number" maxFractionDigits="0"/>
                                </td>

                                <td style="text-align: right;">
                                    <fmt:formatNumber value="${p.unionFee}" type="number" maxFractionDigits="0"/>
                                </td>

                                <td style="text-align: right;">
                                    <fmt:formatNumber value="${p.incomeBeforeTax}" type="number" maxFractionDigits="0"/>
                                </td>

                                <td style="text-align: right;">
                                    <fmt:formatNumber value="${p.taxableIncome}" type="number" maxFractionDigits="0"/>
                                </td>

                                <td style="text-align: right;">
                                    <fmt:formatNumber value="${p.incomeTax}" type="number" maxFractionDigits="0"/>
                                </td>

                                <td style="text-align: right;">
                                    <fmt:formatNumber value="${p.overtimePay}" type="number" maxFractionDigits="0"/>
                                </td>

                                <td style="text-align: right;">
                                    <fmt:formatNumber value="${p.sickLeavePay}" type="number" maxFractionDigits="0"/>
                                </td>

                                <td style="text-align: right; font-weight: bold; color: var(--primary-dark);">
                                    <fmt:formatNumber value="${p.netPay}" type="number" maxFractionDigits="0"/>
                                </td>

                                <td style="text-align: center; padding: 4px 6px;">
                                    <c:set var="statusLower" value="${fn:toLowerCase(not empty p.status ? p.status : 'draft')}" />
                                    <span class="badge ${statusLower == 'confirmed' ? 'badge-active' : 'badge-inactive'}">
                                        <c:out value="${statusLower == 'confirmed' ? 'CONFIRMED' : 'DRAFT'}" />
                                    </span>
                                </td>

                                <td style="text-align: center;">
                                    <div class="action-group">
                                        <a href="${pageContext.request.contextPath}/payroll/detail?id=${p.id}&departmentId=${departmentId}&month=${month}&year=${year}" class="btn-save btn-action">
                                            View
                                        </a>

                                        <c:if test="${statusLower == 'draft' && userPermissions.contains('PAYROLL_CONFIRM')}">
                                            <c:set var="currentDeptParam" value="${not empty requestScope.departmentId ? requestScope.departmentId : 'all'}" />

                                            <form action="${pageContext.request.contextPath}/payroll/confirm" method="POST" onsubmit="return confirm('Confirm this payroll?')">
                                                <input type="hidden" name="id" value="${p.id}">
                                                <input type="hidden" name="redirectDepartmentId" value="${currentDeptParam}">
                                                <input type="hidden" name="redirectMonth" value="${month}">
                                                <input type="hidden" name="redirectYear" value="${year}">

                                                <button type="submit" class="btn-save btn-action">
                                                    Confirm
                                                </button>
                                            </form>
                                        </c:if>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>

                        <c:if test="${empty payrollList}">
                            <tr>
                                <td colspan="18" class="empty-state" style="text-align: center; padding: 20px;">
                                    No payroll records found.
                                </td>
                            </tr>
                        </c:if>
                    </tbody>

                    <c:if test="${not empty payrollList && not empty totalSummary}">
                        <tfoot>
                            <tr>
                                <td><strong>TOTAL SUMMARY</strong></td>
                                <td style="text-align: right;">
                                    <fmt:formatNumber value="${totalSummary.basicSalary}" type="number" maxFractionDigits="0"/>
                                </td>
                                <td style="text-align: center;">
                                    <strong>${totalSummary.actualHours}</strong> / ${totalSummary.expectedHours}
                                </td>
                                <td style="text-align: right;">
                                    <fmt:formatNumber value="${actualBasicSalary}" type="number" maxFractionDigits="0"/>
                                </td>
                                <td style="text-align: right;">
                                    <fmt:formatNumber value="${totalSummary.bonus}" type="number" maxFractionDigits="0"/>
                                </td>
                                <td style="text-align: right;">
                                    <fmt:formatNumber value="${totalSummary.totalIncome}" type="number" maxFractionDigits="0"/>
                                </td>
                                <td style="text-align: right;">
                                    <fmt:formatNumber value="${totalSummary.socialInsurance}" type="number" maxFractionDigits="0"/>
                                </td>
                                <td style="text-align: right;">
                                    <fmt:formatNumber value="${totalSummary.healthInsurance}" type="number" maxFractionDigits="0"/>
                                </td>
                                <td style="text-align: right;">
                                    <fmt:formatNumber value="${totalSummary.unemploymentInsurance}" type="number" maxFractionDigits="0"/>
                                </td>
                                <td style="text-align: right;">
                                    <fmt:formatNumber value="${totalSummary.unionFee}" type="number" maxFractionDigits="0"/>
                                </td>
                                <td style="text-align: right;">
                                    <fmt:formatNumber value="${totalSummary.incomeBeforeTax}" type="number" maxFractionDigits="0"/>
                                </td>
                                <td style="text-align: right;">
                                    <fmt:formatNumber value="${totalSummary.taxableIncome}" type="number" maxFractionDigits="0"/>
                                </td>
                                <td style="text-align: right;">
                                    <fmt:formatNumber value="${totalSummary.incomeTax}" type="number" maxFractionDigits="0"/>
                                </td>
                                <td style="text-align: right;">
                                    <fmt:formatNumber value="${totalSummary.overtimePay}" type="number" maxFractionDigits="0"/>
                                </td>
                                <td style="text-align: right;">
                                    <fmt:formatNumber value="${totalSummary.sickLeavePay}" type="number" maxFractionDigits="0"/>
                                </td>
                                <td style="text-align: right; color: var(--primary-dark);">
                                    <fmt:formatNumber value="${totalSummary.netPay}" type="number" maxFractionDigits="0"/> VND
                                </td>
                                <td></td>
                                <td></td>
                            </tr>
                        </tfoot>
                    </c:if>
                </table>
            </div>

            <c:if test="${totalPage > 1}">
                <div class="pagination" style="margin-top: 15px; display:flex; justify-content:center">

                    <c:set var="currentDeptParam" value="${not empty requestScope.departmentId ? requestScope.departmentId : 'all'}" />

                    <c:url var="firstPageUrl" value="/payroll/list">
                        <c:param name="page" value="1" />
                        <c:if test="${not empty keyword}"><c:param name="search" value="${keyword}" /></c:if>
                        <c:if test="${not empty status}"><c:param name="status" value="${status}" /></c:if>
                        <c:if test="${not empty month}"><c:param name="month" value="${month}" /></c:if>
                        <c:if test="${not empty year}"><c:param name="year" value="${year}" /></c:if>
                        <c:param name="departmentId" value="${currentDeptParam}" />
                        <c:if test="${not empty sort}"><c:param name="sort" value="${sort}" /></c:if>
                    </c:url>
                    <a href="${firstPageUrl}" class="page-link ${currentPage == 1 ? 'disabled' : ''}">First</a>

                    <c:url var="prevPageUrl" value="/payroll/list">
                        <c:param name="page" value="${currentPage - 1}" />
                        <c:if test="${not empty keyword}"><c:param name="search" value="${keyword}" /></c:if>
                        <c:if test="${not empty status}"><c:param name="status" value="${status}" /></c:if>
                        <c:if test="${not empty month}"><c:param name="month" value="${month}" /></c:if>
                        <c:if test="${not empty year}"><c:param name="year" value="${year}" /></c:if>
                        <c:param name="departmentId" value="${currentDeptParam}" />
                        <c:if test="${not empty sort}"><c:param name="sort" value="${sort}" /></c:if>
                    </c:url>
                    <a href="${prevPageUrl}" class="page-link ${currentPage <= 1 ? 'disabled' : ''}">Previous</a>

                    <span class="page-link active">
                        ${currentPage} / ${totalPage}
                    </span>

                    <c:url var="nextPageUrl" value="/payroll/list">
                        <c:param name="page" value="${currentPage + 1}" />
                        <c:if test="${not empty keyword}"><c:param name="search" value="${keyword}" /></c:if>
                        <c:if test="${not empty status}"><c:param name="status" value="${status}" /></c:if>
                        <c:if test="${not empty month}"><c:param name="month" value="${month}" /></c:if>
                        <c:if test="${not empty year}"><c:param name="year" value="${year}" /></c:if>
                        <c:param name="departmentId" value="${currentDeptParam}" />
                        <c:if test="${not empty sort}"><c:param name="sort" value="${sort}" /></c:if>
                    </c:url>
                    <a href="${nextPageUrl}" class="page-link ${currentPage >= totalPage ? 'disabled' : ''}">Next</a>

                    <c:url var="lastPageUrl" value="/payroll/list">
                        <c:param name="page" value="${totalPage}" />
                        <c:if test="${not empty keyword}"><c:param name="search" value="${keyword}" /></c:if>
                        <c:if test="${not empty status}"><c:param name="status" value="${status}" /></c:if>
                        <c:if test="${not empty month}"><c:param name="month" value="${month}" /></c:if>
                        <c:if test="${not empty year}"><c:param name="year" value="${year}" /></c:if>
                        <c:param name="departmentId" value="${currentDeptParam}" />
                        <c:if test="${not empty sort}"><c:param name="sort" value="${sort}" /></c:if>
                    </c:url>
                    <a href="${lastPageUrl}" class="page-link ${currentPage == totalPage ? 'disabled' : ''}">Last</a>
                </div>
            </c:if>

        </div>
    </div>
</div>

</body>
</html>