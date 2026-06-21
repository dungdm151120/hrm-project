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
    <title>${isMyPayroll ? 'My Payroll' : 'Payroll Statements'} | HRM</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body class="dashboard-body">

<div class="dashboard-wrapper">

    <jsp:include page="/WEB-INF/views/common/sidebar.jsp"/>

    <div class="dashboard-main">
        <div class="dashboard-header">
            <div class="header-left">
                <h1 class="header-title">${isMyPayroll ? 'My Payroll' : 'Payroll List'}</h1>
            </div>
            <div class="header-right">
                <c:if test="${userPermissions.contains('PAYROLL_EXPORT_REPORT')}">
                    <a href="${pageContext.request.contextPath}/payroll/export" class="btn-save">Export Report</a>
                </c:if>
            </div>
        </div>

        <div class="dashboard-content">
            <c:if test="${not empty sessionScope.message}">
                <div class="alert alert-success">
                    ${sessionScope.message}
                </div>
                <% session.removeAttribute("message"); %>
            </c:if>
            <c:if test="${not empty sessionScope.error}">
                <div class="alert alert-error">
                    ${sessionScope.error}
                </div>
                <% session.removeAttribute("error"); %>
            </c:if>

            <div class="search-filter">
                <form action="${pageContext.request.contextPath}${isMyPayroll ? '/payroll/my' : '/payroll/list'}" method="GET">

                    <c:choose>
                        <c:when test="${isMyPayroll}">
                            <input type="hidden" name="status" value="confirmed">
                        </c:when>
                        <c:otherwise>
                            <input type="text" name="search" placeholder="Search name" value="${keyword}">

                            <select name="status" onchange="this.form.submit()">
                                <option value="all" ${status == 'all' || empty status ? 'selected' : ''}>All status</option>
                                <option value="draft" ${status == 'draft' ? 'selected' : ''}>Draft</option>
                                <option value="confirmed" ${status == 'confirmed' ? 'selected' : ''}>Confirmed</option>
                            </select>
                        </c:otherwise>
                    </c:choose>

                    <c:set var="monthNames" value="${fn:split('January,February,March,April,May,June,July,August,September,October,November,December', ',')}" />

                    <select name="month" onchange="this.form.submit()">
                        <option value="" ${empty month ? 'selected' : ''}>All months</option>
                        <c:forEach var="m" begin="1" end="12">
                            <option value="${m}" ${month == m ? 'selected' : ''}>${monthNames[m - 1]}</option>
                        </c:forEach>
                    </select>

                    <select name="year" onchange="this.form.submit()">
                        <option value="" ${empty year ? 'selected' : ''}>All years</option>
                        <c:forEach var="y" begin="${currentYear - 5}" end="${currentYear + 1}">
                            <option value="${y}" ${year == y ? 'selected' : ''}>${y}</option>
                        </c:forEach>
                    </select>

                    <select name="sort" onchange="this.form.submit()">
                        <option value="name_asc" ${sort == 'name_asc' ? 'selected' : ''}>Name A-Z</option>
                        <option value="name_desc" ${sort == 'name_desc' ? 'selected' : ''}>Name Z-A</option>
                    </select>

                    <button type="submit" class="search-btn">Search</button>

                    <c:if test="${not empty keyword || (!isMyPayroll && not empty status && status != 'all') || not empty month || not empty year || not empty sort}">
                        <a href="${pageContext.request.contextPath}${isMyPayroll ? '/payroll/my' : '/payroll/list'}" class="btn-reset" style="text-decoration: none; padding: 8px 12px; margin-left: 5px;">Clear</a>
                    </c:if>
                </form>
            </div>

            <div class="table-wrapper">
                <table>
                    <thead>
                        <tr>
                            <th>Pay Period</th>
                            <th>Full Name</th>
                            <th>Department</th>
                            <th>Position</th>
                            <th style="text-align: right;">Basic Salary</th>
                            <th style="text-align: right;">Gross Income</th>
                            <th style="text-align: right;">Net Pay Amount</th>
                            <th style="text-align: center;">Status</th>
                            <th style="text-align: center;">Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="p" items="${payrollList}" varStatus="s">
                            <tr>
                                <td>
                                    <strong>${p.monthName} / ${p.year}</strong>
                                </td>
                                <td><c:out value="${p.employeeName}" default="N/A" /></td>
                                <td>
                                    <c:choose>
                                        <c:when test="${not empty p.departmentName}">
                                            <span class="badge-dept">${p.departmentName}</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="text-muted">N/A</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td><c:out value="${p.positionName}" default="N/A" /></td>
                                <td style="text-align: right;">
                                    <fmt:formatNumber value="${p.basicSalary}" type="number" maxFractionDigits="0"/> VND
                                </td>
                                <td style="text-align: right;">
                                    <fmt:formatNumber value="${p.totalIncome}" type="number" maxFractionDigits="0"/> VND
                                </td>
                                <td style="text-align: right; font-weight: bold; color: #2563EB;">
                                    <fmt:formatNumber value="${p.netPay}" type="number" maxFractionDigits="0"/> VND
                                </td>
                                <td style="text-align: center;">
                                    <c:set var="statusLower" value="${fn:toLowerCase(not empty p.status ? p.status : 'draft')}" />
                                    <span class="badge ${statusLower == 'confirmed' ? 'badge-active' : 'badge-inactive'}">
                                        <c:out value="${statusLower == 'confirmed' ? 'CONFIRMED' : 'DRAFT'}" />
                                    </span>
                                </td>
                                <td style="text-align: center;">
                                    <div class="actions">
                                        <c:url var="detailUrl" value="/payroll/detail">
                                            <c:param name="id" value="${p.id}" />
                                            <c:if test="${isMyPayroll}">
                                                <c:param name="from" value="my" />
                                            </c:if>
                                        </c:url>
                                        <a href="${detailUrl}" class="btn-save" style="padding: 5px 12px; font-size: 0.85rem; text-decoration: none;">
                                            View Details
                                        </a>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty payrollList}">
                            <tr>
                                <td colspan="11" class="empty-state" style="text-align: center; padding: 30px;">
                                    No payroll records found.
                                </td>
                            </tr>
                        </c:if>
                    </tbody>
                </table>
            </div>

            <c:if test="${totalPage > 1}">
                <div class="pagination" style="margin-top: 20px; display: flex; gap: 5px; justify-content: center;">

                    <c:url var="firstPageUrl" value="${isMyPayroll ? '/payroll/my' : '/payroll/list'}">
                        <c:param name="page" value="1" />
                        <c:if test="${not empty keyword}"><c:param name="search" value="${keyword}" /></c:if>
                        <c:if test="${not empty status}"><c:param name="status" value="${status}" /></c:if>
                        <c:if test="${not empty month}"><c:param name="month" value="${month}" /></c:if>
                        <c:if test="${not empty year}"><c:param name="year" value="${year}" /></c:if>
                        <c:if test="${not empty sort}"><c:param name="sort" value="${sort}" /></c:if>
                    </c:url>
                    <a href="${firstPageUrl}" class="page-link ${currentPage == 1 ? 'disabled' : ''}">First</a>

                    <c:url var="prevPageUrl" value="${isMyPayroll ? '/payroll/my' : '/payroll/list'}">
                        <c:param name="page" value="${currentPage - 1}" />
                        <c:if test="${not empty keyword}"><c:param name="search" value="${keyword}" /></c:if>
                        <c:if test="${not empty status}"><c:param name="status" value="${status}" /></c:if>
                        <c:if test="${not empty month}"><c:param name="month" value="${month}" /></c:if>
                        <c:if test="${not empty year}"><c:param name="year" value="${year}" /></c:if>
                        <c:if test="${not empty sort}"><c:param name="sort" value="${sort}" /></c:if>
                    </c:url>
                    <a href="${prevPageUrl}" class="page-link ${currentPage <= 1 ? 'disabled' : ''}">Previous</a>

                    <span class="page-link active" style="padding: 5px 10px; background-color: #2563EB; color: #fff; border-radius: 4px;">
                        ${currentPage} / ${totalPage}
                    </span>

                    <c:url var="nextPageUrl" value="${isMyPayroll ? '/payroll/my' : '/payroll/list'}">
                        <c:param name="page" value="${currentPage + 1}" />
                        <c:if test="${not empty keyword}"><c:param name="search" value="${keyword}" /></c:if>
                        <c:if test="${not empty status}"><c:param name="status" value="${status}" /></c:if>
                        <c:if test="${not empty month}"><c:param name="month" value="${month}" /></c:if>
                        <c:if test="${not empty year}"><c:param name="year" value="${year}" /></c:if>
                        <c:if test="${not empty sort}"><c:param name="sort" value="${sort}" /></c:if>
                    </c:url>
                    <a href="${nextPageUrl}" class="page-link ${currentPage >= totalPage ? 'disabled' : ''}">Next</a>

                    <c:url var="lastPageUrl" value="${isMyPayroll ? '/payroll/my' : '/payroll/list'}">
                        <c:param name="page" value="${totalPage}" />
                        <c:if test="${not empty keyword}"><c:param name="search" value="${keyword}" /></c:if>
                        <c:if test="${not empty status}"><c:param name="status" value="${status}" /></c:if>
                        <c:if test="${not empty month}"><c:param name="month" value="${month}" /></c:if>
                        <c:if test="${not empty year}"><c:param name="year" value="${year}" /></c:if>
                        <c:if test="${not empty sort}"><c:param name="sort" value="${sort}" /></c:if>
                    </c:url>
                    <a href="${lastPageUrl}" class="page-link ${currentPage == totalPage ? 'disabled' : ''}">Last</a>
                </div>
            </c:if>
        </div>
    </div>
</div>
