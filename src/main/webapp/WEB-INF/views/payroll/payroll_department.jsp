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
    <style>
        .payroll-cards-grid {
            display: grid;
            grid-template-columns: repeat(3, 1fr); /* 3 cột mỗi hàng */
            gap: 20px;
            margin-top: 20px;
        }
        .payroll-card {
            border: 2px solid #333;
            border-radius: 8px;
            background-color: #fff;
            box-shadow: 2px 4px 10px rgba(0,0,0,0.1);
            overflow: hidden;
        }
        .card-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 12px 15px;
            background-color: #f8f9fa;
            border-bottom: 2px solid #333;
        }
        .card-header h3 {
            margin: 0;
            font-size: 1.2rem;
            color: #333;
        }
        .card-icon {
            color: #2563EB;
            font-size: 1.2rem;
            text-decoration: none;
        }
        .card-icon:hover { color: #1e40af; }
        .card-body { padding: 15px; }
        .card-body p { margin: 8px 0; font-size: 1.05rem; }
        .card-total { font-size: 1.2rem; font-weight: bold; color: #10b981; }
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
                <form action="${pageContext.request.contextPath}/payroll/department" method="GET">

                    <select name="departmentId" onchange="this.form.submit()">
                        <option value="" ${empty departmentId ? 'selected' : ''}>All Departments</option>

                        <c:forEach items="${departments}" var="dept">
                            <option value="${dept.id}" ${departmentId == dept.id ? 'selected' : ''}>${dept.name}</option>
                        </c:forEach>
                    </select>

                    <c:set var="monthNames" value="${fn:split('January,February,March,April,May,June,July,August,September,October,November,December', ',')}" />

                    <select name="month" onchange="this.form.submit()">
                        <option value="" ${empty month ? 'selected' : ''}>All months</option>
                        <c:forEach var="m" begin="1" end="12">
                            <option value="${m}" ${month == m ? 'selected' : ''}>${monthNames[m - 1]}</option>
                        </c:forEach>
                    </select>

                    <select name="year" onchange="this.form.submit()">
                        <option value="" ${empty year ? 'selected' : ''}>All years</option>
                        <c:forEach var="y" begin="${currentYear - 2}" end="${currentYear + 1}">
                            <option value="${y}" ${year == y ? 'selected' : ''}>${y}</option>
                        </c:forEach>
                    </select>

                    <button type="submit" class="search-btn">Search</button>

                    <c:if test="${not empty month || not empty year || not empty departmentId}">
                        <a href="${pageContext.request.contextPath}/payroll/department" class="btn-reset" style="text-decoration: none; padding: 8px 12px; margin-left: 5px;">Clear</a>
                    </c:if>
                </form>
            </div>

            <div class="payroll-cards-grid">
                <c:forEach var="summary" items="${summaryList}">
                    <div class="payroll-card">
                        <div class="card-header">
                            <h3>${summary.monthName} ${summary.year}</h3>

                            <a href="${pageContext.request.contextPath}/payroll/list?departmentId=${summary.departmentId}&month=${summary.month}&year=${summary.year}"
                               class="card-icon" title="View Detail List" style="font-size: 1rem; font-weight: bold; text-decoration: underline; color: #2563EB;">
                                View Details
                            </a>
                        </div>

                        <div class="card-body">
                            <p><strong>Department:</strong> ${not empty summary.departmentName ? summary.departmentName : 'N/A'}</p>
                            <p><strong>Total:</strong> <span class="card-total"><fmt:formatNumber value="${summary.totalPayroll}" type="number" maxFractionDigits="0"/> VND</span></p>
                        </div>
                    </div>
                </c:forEach>

                <c:if test="${empty summaryList}">
                    <div style="grid-column: span 3; text-align: center; padding: 40px; color: #666;">
                        No aggregated payroll data found.
                    </div>
                </c:if>
            </div>

            <c:if test="${totalPage > 1}">
                <div class="pagination" style="margin-top: 20px; display: flex; gap: 5px; justify-content: center;">

                    <c:url var="firstPageUrl" value="/payroll/department">
                        <c:param name="page" value="1" />
                        <c:if test="${not empty month}"><c:param name="month" value="${month}" /></c:if>
                        <c:if test="${not empty year}"><c:param name="year" value="${year}" /></c:if>
                        <c:if test="${not empty departmentId}"><c:param name="departmentId" value="${departmentId}" /></c:if>
                    </c:url>
                    <a href="${firstPageUrl}" class="page-link ${currentPage == 1 ? 'disabled' : ''}">First</a>

                    <c:url var="prevPageUrl" value="/payroll/department">
                        <c:param name="page" value="${currentPage - 1}" />
                        <c:if test="${not empty month}"><c:param name="month" value="${month}" /></c:if>
                        <c:if test="${not empty year}"><c:param name="year" value="${year}" /></c:if>
                        <c:if test="${not empty departmentId}"><c:param name="departmentId" value="${departmentId}" /></c:if>
                    </c:url>
                    <a href="${prevPageUrl}" class="page-link ${currentPage <= 1 ? 'disabled' : ''}">Previous</a>

                    <span class="page-link active" style="padding: 5px 10px; background-color: #2563EB; color: #fff; border-radius: 4px;">
                        ${currentPage} / ${totalPage}
                    </span>

                    <c:url var="nextPageUrl" value="/payroll/department">
                        <c:param name="page" value="${currentPage + 1}" />
                        <c:if test="${not empty month}"><c:param name="month" value="${month}" /></c:if>
                        <c:if test="${not empty year}"><c:param name="year" value="${year}" /></c:if>
                        <c:if test="${not empty departmentId}"><c:param name="departmentId" value="${departmentId}" /></c:if>
                    </c:url>
                    <a href="${nextPageUrl}" class="page-link ${currentPage >= totalPage ? 'disabled' : ''}">Next</a>

                    <c:url var="lastPageUrl" value="/payroll/department">
                        <c:param name="page" value="${totalPage}" />
                        <c:if test="${not empty month}"><c:param name="month" value="${month}" /></c:if>
                        <c:if test="${not empty year}"><c:param name="year" value="${year}" /></c:if>
                        <c:if test="${not empty departmentId}"><c:param name="departmentId" value="${departmentId}" /></c:if>
                    </c:url>
                    <a href="${lastPageUrl}" class="page-link ${currentPage == totalPage ? 'disabled' : ''}">Last</a>
                </div>
            </c:if>
        </div>
    </div>
</div>
