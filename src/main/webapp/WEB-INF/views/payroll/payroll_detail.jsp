<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
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
            <div class="header-right">
                <span class="status-badge ${payroll.status == 'CONFIRMED' || payroll.status == 'Approved' ? 'status-active' : 'status-pending'}">
                    <c:out value="${payroll.status}" default="DRAFT" />
                </span>
            </div>
        </div>

        <div class="dashboard-content">
            <a class="back-link" href="${pageContext.request.contextPath}/payroll/list">Return to payroll list</a>

            <div class="form-row" style="display: flex; gap: 20px; margin-top: 20px; margin-bottom: 24px;">
                <div class="form-group" style="flex: 1;">
                    <label>Employee ID</label>
                    <input type="text" value="User #<c:out value='${employee.id}' default='N/A'/>" readonly />
                </div>
                <div class="form-group" style="flex: 1;">
                    <label>Full Name</label>
                    <input type="text" value="<c:out value='${employee.fullName}' default='Unknown'/>" readonly />
                </div>
            </div>

            <div class="form-row" style="display: flex; gap: 20px; margin-bottom: 24px;">
                <div class="form-row" style="display: flex; gap: 20px; margin-bottom: 24px; width: 100%;">
                    <div class="form-group" style="flex: 1;">
                        <label>Department Unit</label>
                        <input type="text" value="<c:out value='${employee.departmentName}' default='N/A'/>" readonly />
                    </div>
                    <div class="form-group" style="flex: 1;">
                        <label>Job Position</label>
                        <input type="text" value="<c:out value='${employee.positionName}' default='N/A'/>" readonly />
                    </div>
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
                        <label>Gross Total Income</label>
                        <input type="text" value="<fmt:formatNumber value='${payroll.totalIncome}' type='number' maxFractionDigits='0'/> VND" readonly />
                    </div>
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

            <div class="form-actions">
                <a href="${pageContext.request.contextPath}/payroll/list" class="btn-cancel" style="text-decoration: none; display: inline-block; text-align: center;">
                    Cancel
                </a>

                <c:if test="${payroll.status == 'DRAFT' && (currentUserRole == 'PAYROLL_STAFF' || currentUserRole == 'PAYROLL_MANAGER' || currentUserRole == 'HR_MANAGER' || currentUserRole == 'BUSINESSADMIN' || currentUserRole == 'ADMIN')}">
                    <a href="${pageContext.request.contextPath}/payroll/confirm?type=single&id=${payroll.id}" class="btn-save" style="text-decoration: none; display: inline-block; text-align: center;">
                        Confirm This Payroll
                    </a>
                </c:if>
            </div>

        </div>
    </div>
</div>

</body>
</html>