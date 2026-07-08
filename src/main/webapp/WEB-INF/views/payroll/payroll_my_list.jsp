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
                <form action="${pageContext.request.contextPath}/payroll/my" method="GET">

                    <label for="month">Month</label>
                    <select name="month" style="padding: 6px 12px;" onchange="this.form.submit()">
                        <option value="" ${empty month ? 'selected' : ''}>All months</option>
                        <c:forEach var="m" begin="1" end="12">
                            <option value="${m}" ${month == m ? 'selected' : ''}>${monthNames[m - 1]}</option>
                        </c:forEach>
                    </select>

                    <label>Year</label>
                    <select name="year" style="padding: 6px 12px;" onchange="this.form.submit()">
                        <option value="">All years</option>
                        <c:forEach var="y" begin="${currentYear - 1}" end="${currentYear + 1}">
                            <option value="${y}" ${year == y ? 'selected' : ''}>Year ${y}</option>
                        </c:forEach>
                    </select>

                    <button type="submit" class="search-btn">Search</button>
                </form>
            </div>

            <div class="table-wrapper">
                <table>
                    <thead>
                        <tr>
                            <th>Pay Period</th>
                            <th style="text-align: right;">Basic Salary</th>
                            <th style="text-align: right;">Net Pay</th>
                            <th style="text-align: center;">Status</th>
                            <th style="text-align: left;">Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="p" items="${payrollList}" varStatus="s">
                            <tr>
                                <td>
                                    <strong>${monthNames[p.month - 1]} / ${p.year}</strong>
                                </td>
                                <td style="text-align: right;">
                                    <fmt:formatNumber value="${p.basicSalary}" type="number" maxFractionDigits="0"/> VND
                                </td>
                                <td style="text-align: right; font-weight: bold; color: #2563EB;">
                                    <fmt:formatNumber value="${p.netPay}" type="number" maxFractionDigits="0"/> VND
                                </td>
                                <td style="text-align: center;">
                                    <span class="badge badge-active">
                                        <c:out value="${p.status}" />
                                    </span>
                                </td>
                                <td>
                                    <a href="${pageContext.request.contextPath}/payroll/my/detail?id=${p.id}&month=${p.month}&year=${p.year}" class="btn-save">
                                        View Details
                                    </a>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty payrollList}">
                            <tr>
                                <td colspan="5" class="empty-state" style="text-align: center; padding: 30px;">
                                    No payroll records found.
                                </td>
                            </tr>
                        </c:if>
                    </tbody>
                </table>
            </div>

            <c:if test="${totalPage > 1}">
                <div class="pagination" style="margin-top: 20px; display: flex; gap: 5px; justify-content: center;">

                    <c:url var="firstPageUrl" value="/payroll/my">
                        <c:param name="page" value="1" />
                        <c:if test="${not empty month}"><c:param name="month" value="${month}" /></c:if>
                        <c:if test="${not empty year}"><c:param name="year" value="${year}" /></c:if>
                    </c:url>
                    <a href="${firstPageUrl}" class="page-link ${currentPage == 1 ? 'disabled' : ''}">First</a>

                    <c:url var="prevPageUrl" value="/payroll/my">
                        <c:param name="page" value="${currentPage - 1}" />
                        <c:if test="${not empty month}"><c:param name="month" value="${month}" /></c:if>
                        <c:if test="${not empty year}"><c:param name="year" value="${year}" /></c:if>
                    </c:url>
                    <a href="${prevPageUrl}" class="page-link ${currentPage <= 1 ? 'disabled' : ''}">Previous</a>

                    <span class="page-link active" style="padding: 5px 10px; background-color: #2563EB; color: #fff; border-radius: 4px;">
                        ${currentPage} / ${totalPage}
                    </span>

                    <c:url var="nextPageUrl" value="/payroll/my">
                        <c:param name="page" value="${currentPage + 1}" />
                        <c:if test="${not empty month}"><c:param name="month" value="${month}" /></c:if>
                        <c:if test="${not empty year}"><c:param name="year" value="${year}" /></c:if>
                    </c:url>
                    <a href="${nextPageUrl}" class="page-link ${currentPage >= totalPage ? 'disabled' : ''}">Next</a>

                    <c:url var="lastPageUrl" value="/payroll/my">
                        <c:param name="page" value="${totalPage}" />
                        <c:if test="${not empty month}"><c:param name="month" value="${month}" /></c:if>
                        <c:if test="${not empty year}"><c:param name="year" value="${year}" /></c:if>
                    </c:url>
                    <a href="${lastPageUrl}" class="page-link ${currentPage == totalPage ? 'disabled' : ''}">Last</a>
                </div>
            </c:if>
        </div>
    </div>
</div>

</body>
</html>