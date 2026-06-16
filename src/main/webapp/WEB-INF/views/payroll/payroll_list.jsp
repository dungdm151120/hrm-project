<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Payroll Statements | HRM</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body class="dashboard-body">

<div class="dashboard-wrapper">

    <jsp:include page="/WEB-INF/views/common/sidebar.jsp"/>

    <div class="dashboard-main">

        <div class="dashboard-header">
            <div class="header-left">
                <h1 class="header-title">Payroll Statements</h1>
            </div>

            <c:if test="${currentUserRole == 'PAYROLL_STAFF' || currentUserRole == 'PAYROLL_MANAGER' || currentUserRole == 'HR_MANAGER' || currentUserRole == 'BUSINESSADMIN' || currentUserRole == 'ADMIN'}">
                <div class="header-right">
                    <a href="${pageContext.request.contextPath}/payroll/confirm?type=all&month=6&year=2026" class="btn-save" style="text-decoration: none;" onclick="return confirm('Confirm all current draft payroll sheets?');">
                        Confirm Draft Payrolls
                    </a>
                </div>
            </c:if>
        </div>

        <div class="dashboard-content">

            <c:if test="${not empty error}">
                <div class="alert alert-error">${error}</div>
            </c:if>

            <c:if test="${not empty success}">
                <div class="alert alert-success">${success}</div>
            </c:if>

            <div class="table-wrapper">
                <table>
                    <thead>
                        <tr>
                            <c:if test="${currentUserRole != 'EMPLOYEE'}">
                                <th>Employee ID</th>
                                <th>Full Name</th>
                                <th>Department</th>
                                <th>Position</th>
                            </c:if>
                            <th>Pay Period</th>
                            <th style="text-align: right;">Basic Salary</th>
                            <th style="text-align: right;">Gross Income</th>
                            <th style="text-align: right;">Net Pay Amount</th>
                            <th style="text-align: center;">Status</th>
                            <th style="text-align: center;">Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="p" items="${payrollList}">
                            <tr>
                                <c:if test="${currentUserRole != 'EMPLOYEE'}">
                                    <td><strong>User #${p.userId}</strong></td>
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
                                </c:if>
                                <td>
                                    <strong>Month ${p.month} / ${p.year}</strong>
                                </td>
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
                                    <span class="status-badge ${p.status == 'CONFIRMED' || p.status == 'Approved' ? 'status-active' : 'status-pending'}">
                                        ${p.status}
                                    </span>
                                </td>
                                <td style="text-align: center;">
                                    <div class="actions">
                                        <a href="${pageContext.request.contextPath}/payroll/detail?id=${p.id}" class="btn-save" style="padding: 5px 12px; font-size: 0.85rem; text-decoration: none;">
                                            View Details
                                        </a>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty payrollList}">
                            <tr>
                                <td colspan="${currentUserRole != 'EMPLOYEE' ? 10 : 6}" class="empty-state" style="text-align: center; padding: 30px;">
                                    No payroll records found.
                                </td>
                            </tr>
                        </c:if>
                    </tbody>
                </table>
            </div>

            <c:if test="${totalPage > 1}">
                <div class="pagination" style="margin-top: 20px; display: flex; gap: 5px; justify-content: center;">

                    <c:url var="firstPageUrl" value="/payroll/list">
                        <c:param name="page" value="1" />
                    </c:url>
                    <a href="${firstPageUrl}" class="page-link ${currentPage == 1 ? 'disabled' : ''}">First</a>

                    <c:url var="prevPageUrl" value="/payroll/list">
                        <c:param name="page" value="${currentPage - 1}" />
                    </c:url>
                    <a href="${prevPageUrl}" class="page-link ${currentPage <= 1 ? 'disabled' : ''}">Previous</a>

                    <span class="page-link active" style="padding: 5px 10px; background-color: #2563EB; color: #fff; border-radius: 4px;">
                        ${currentPage} / ${totalPage}
                    </span>

                    <c:url var="nextPageUrl" value="/payroll/list">
                        <c:param name="page" value="${currentPage + 1}" />
                    </c:url>
                    <a href="${nextPageUrl}" class="page-link ${currentPage >= totalPage ? 'disabled' : ''}">Next</a>

                    <c:url var="lastPageUrl" value="/payroll/list">
                        <c:param name="page" value="${totalPage}" />
                    </c:url>
                    <a href="${lastPageUrl}" class="page-link ${currentPage == totalPage ? 'disabled' : ''}">Last</a>
                </div>
            </c:if>

        </div>
    </div>
</div>

</body>
</html>