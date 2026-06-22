<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Payroll Statement Detail | HRM</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body class="dashboard-body">

<div class="dashboard-wrapper">

    <jsp:include page="/WEB-INF/views/common/sidebar.jsp"/>

    <div class="dashboard-main">

        <div class="dashboard-header">
            <div class="header-left">
                <h1 class="header-title">Employee Payslip</h1>
            </div>
        </div>

        <c:if test="${not empty sessionScope.message}">
            <div class="alert alert-success">
                ${sessionScope.message}
            </div>
            <% session.removeAttribute("message"); %>
        </c:if>

        <form action="${pageContext.request.contextPath}/payroll/confirm" method="post" style="display: inline;">
            <div class="dashboard-content">
                <div style="display: flex; justify-content: space-between; align-items: center; width: 100%; margin-bottom: 20px;">
                    <c:choose>
                        <c:when test="${isMyPayroll}">
                            <a class="back-link" href="${pageContext.request.contextPath}/payroll/my" style="margin: 0;">
                                Return to my payroll list
                            </a>
                        </c:when>

                        <c:otherwise>
                            <a class="back-link" href="${pageContext.request.contextPath}/payroll/list?departmentId=${param.redirectDepartmentId}&month=${param.redirectMonth}&year=${param.redirectYear}" style="margin: 0;">
                                Return to payroll list
                            </a>
                        </c:otherwise>
                    </c:choose>
                    <div>
                        <c:set var="statusLower" value="${fn:toLowerCase(not empty payroll.status ? payroll.status : 'draft')}" />

                        <span class="badge ${statusLower == 'confirmed' ? 'badge-active' : 'badge-inactive'}">
                             <c:out value="${statusLower == 'confirmed' ? 'CONFIRMED' : 'DRAFT'}" />
                        </span>
                    </div>
                </div>

                <div class="form-row" style="display: flex; gap: 20px; ">
                    <div class="form-group" style="flex: 1;">
                        <label>Employee Code</label>
                        <input type="text" value="<c:out value='${employee.employeeCode}' default='N/A'/>" readonly />
                    </div>
                    <div class="form-group" style="flex: 1;">
                        <label>Full Name</label>
                        <input type="text" value="<c:out value='${employee.fullName}' default='Unknown'/>" readonly />
                    </div>
                </div>

                <div class="form-row" style="display: flex; gap: 20px; ">
                    <div class="form-group" style="flex: 1;">
                        <label>Department Unit</label>
                        <input type="text" value="<c:out value='${employee.departmentName}' default='N/A'/>" readonly />
                    </div>
                    <div class="form-group" style="flex: 1;">
                        <label>Job Position</label>
                        <input type="text" value="<c:out value='${employee.positionName}' default='N/A'/>" readonly />
                    </div>
                </div>

                <div class="form-row" style="display: flex; gap: 40px; margin-bottom: 24px;">

                    <div style="flex: 1;">
                        <h3 style="border-bottom: 2px solid #3B82F6; padding-bottom: 8px; margin-bottom: 16px;">Earnings & Hours</h3>
                        <div class="form-group">
                            <label>Expected Working Hours</label>
                            <input type="text" value="<c:out value='${payroll.expectedHours}' default='0'/> hrs" readonly />
                        </div>
                        <div class="form-group">
                            <label>Actual Working Hours</label>
                            <input type="text" value="<c:out value='${payroll.actualHours}' default='0'/> hrs" readonly />
                        </div>
                        <div class="form-group">
                            <label>Basic Salary (Contract)</label>
                            <input type="text" value="<fmt:formatNumber value='${payroll.basicSalary}' type='number' maxFractionDigits='0'/> VND" readonly />
                        </div>
                        <div class="form-group">
                            <label>Rate Multiplier</label>
                            <input type="text" value="${payroll.rateMultiplier}" readonly />
                        </div>
                        <div class="form-group">
                            <label>Gross Income</label>
                            <input type="text" value="<fmt:formatNumber value='${payroll.totalIncome}' type='number' maxFractionDigits='0'/> VND" readonly />
                        </div>
                        <div class="form-group">
                            <label>Income Before Tax</label>
                            <input type="text" value="<fmt:formatNumber value='${payroll.incomeBeforeTax}' type='number' maxFractionDigits='0'/> VND" readonly />
                        </div>
                        <form action="${pageContext.request.contextPath}/payroll/confirm" method="post" style="display: inline;">
                            <input type="hidden" name="id" value="${payroll.id}">

                            <div class="form-group" style="flex: 1;">
                                <div class="form-group" style="margin-bottom: 15px;">
                                    <label>Bonus</label>
                                    <input type="text" name="bonus" value="<fmt:formatNumber value='${payroll.bonus}' type='number' maxFractionDigits='0'/> VND" readonly>
                                </div>

                                <div class="form-group" style="margin-bottom: 15px;">
                                    <label>Bonus Description:</label>
                                    <input type="text" name="description" value="${payroll.description}" readonly>
                                </div>
                            </div>
                        </form>
                    </div>

                    <div style="flex: 1;">
                        <h3 style="border-bottom: 2px solid #EF4444; padding-bottom: 8px; margin-bottom: 16px;">Statutory Deductions</h3>
                        <div class="form-group">
                            <label>Social Insurance (8%)</label>
                            <input type="text" value="- <fmt:formatNumber value='${payroll.socialInsurance}' type='number' maxFractionDigits='0'/> VND" readonly />
                        </div>
                        <div class="form-group">
                            <label>Health Insurance (1.5%)</label>
                            <input type="text" value="- <fmt:formatNumber value='${payroll.healthInsurance}' type='number' maxFractionDigits='0'/> VND" readonly />
                        </div>
                        <div class="form-group">
                            <label>Unemployment Ins. (1%)</label>
                            <input type="text" value="- <fmt:formatNumber value='${payroll.unemploymentInsurance}' type='number' maxFractionDigits='0'/> VND" readonly />
                        </div>
                        <div class="form-group">
                            <label>Personal Income Tax (PIT)</label>
                            <input type="text" value="- <fmt:formatNumber value='${payroll.incomeTax}' type='number' maxFractionDigits='0'/> VND" readonly />
                        </div>
                    </div>
                </div>

                <div class="form-group" style="margin-bottom: 32px;">
                    <label style="font-weight: bold; font-size: 1.1rem;">Net Take-Home Pay (Total Remuneration)</label>
                    <input type="text" value="<fmt:formatNumber value='${payroll.netPay}' type='number' maxFractionDigits='0'/> VND" readonly style="font-size: 1.5rem; font-weight: bold; color: #2563EB;" />
                </div>
            </div>
        </form>
    </div>
</div>

</body>
</html>
