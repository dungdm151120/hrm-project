<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Payslip Detail | HRM</title>
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

        <input type="hidden" name="id" value="${payroll.id}">

        <div class="dashboard-content">
            <div style="display: flex; justify-content: space-between; align-items: center; width: 100%; margin-bottom: 20px;">
                <a class="back-link" href="${pageContext.request.contextPath}/payroll/my">
                    Return to my payroll list
                </a>
            </div>

            <div class="form-row" style="display: flex; gap: 20px;">
                <div class="form-group" style="flex: 1;">
                    <label>Employee Code</label>
                    <input type="text" value="<c:out value='${employee.employeeCode}' default='N/A'/>" readonly />
                </div>
                <div class="form-group" style="flex: 1;">
                    <label>Full Name</label>
                    <input type="text" value="<c:out value='${employee.fullName}' default='Unknown'/>" readonly />
                </div>
            </div>

            <div class="form-row" style="display: flex; gap: 20px; margin-bottom: 30px;">
                <div class="form-group" style="flex: 1;">
                    <label>Department Unit</label>
                    <input type="text" value="<c:out value='${employee.departmentName}' default='N/A'/>" readonly />
                </div>
                <div class="form-group" style="flex: 1;">
                    <label>Job Position</label>
                    <input type="text" value="<c:out value='${employee.positionName}' default='N/A'/>" readonly />
                </div>
            </div>

            <div class="form-row" style="display: flex; gap: 30px; margin-bottom: 20px;">

                <div style="flex: 1;">
                    <h3 style="border-bottom: 2px solid #3B82F6; padding-bottom: 8px; margin-bottom: 16px; color: #1E3A8A;">1. Earnings & Hours</h3>
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
                    <div class="form-group" style="margin-top: 12px;">
                        <label style="font-weight: bold; color: #2563EB;">Gross Income (Total Base Income)</label>
                        <input type="text" value="<fmt:formatNumber value='${payroll.totalIncome}' type='number' maxFractionDigits='0'/> VND" readonly style="font-weight: bold; background-color: #EFF6FF;" />
                    </div>
                </div>

                <div style="flex: 1;">
                    <h3 style="border-bottom: 2px solid #EF4444; padding-bottom: 8px; margin-bottom: 16px; color: #991B1B;">2. Employee Deductions</h3>
                    <div class="form-group">
                        <label>Social Insurance (${setting.employeeSocialInsurance}%)</label>
                        <input type="text" value="- <fmt:formatNumber value='${payroll.socialInsurance}' type='number' maxFractionDigits='0'/> VND" readonly />
                    </div>
                    <div class="form-group">
                        <label>Health Insurance (${setting.employeeHealthInsurance}%)</label>
                        <input type="text" value="- <fmt:formatNumber value='${payroll.healthInsurance}' type='number' maxFractionDigits='0'/> VND" readonly />
                    </div>
                    <div class="form-group">
                        <label>Unemployment Ins. (${setting.employeeUnemploymentInsurance}%)</label>
                        <input type="text" value="- <fmt:formatNumber value='${payroll.unemploymentInsurance}' type='number' maxFractionDigits='0'/> VND" readonly />
                    </div>
                    <div class="form-group">
                        <label>Trade Union Fee (${setting.employeeUnion}%)</label>
                        <input type="text" value="- <fmt:formatNumber value='${payroll.unionFee}' type='number' maxFractionDigits='0'/> VND" readonly />
                    </div>
                </div>

                <div style="flex: 1;">
                    <h3 style="border-bottom: 2px solid #10B981; padding-bottom: 8px; margin-bottom: 16px; color: #065F46;">3. Company Contributions</h3>
                    <div class="form-group">
                        <label>Social Insurance (${setting.companySocialInsurance}%)</label>
                        <input type="text" value="<fmt:formatNumber value='${payroll.companySocialInsurance}' type='number' maxFractionDigits='0'/> VND" readonly />
                    </div>
                    <div class="form-group">
                        <label>Health Insurance (${setting.companyHealthInsurance}%)</label>
                        <input type="text" value="<fmt:formatNumber value='${payroll.companyHealthInsurance}' type='number' maxFractionDigits='0'/> VND" readonly />
                    </div>
                    <div class="form-group">
                        <label>Unemployment Ins. (${setting.companyUnemploymentInsurance}%)</label>
                        <input type="text" value="<fmt:formatNumber value='${payroll.companyUnemploymentInsurance}' type='number' maxFractionDigits='0'/> VND" readonly />
                    </div>
                    <div class="form-group">
                        <label>Union Fund (${setting.companyUnion}%)</label>
                        <input type="text" value="<fmt:formatNumber value='${payroll.companyUnionFee}' type='number' maxFractionDigits='0'/> VND" readonly />
                    </div>
                </div>
            </div>

            <hr style="border: 0; border-top: 2px dashed #CBD5E1; margin: 30px 0;" />

            <div style="background: #F8FAFC; border: 1px solid #E2E8F0; padding: 24px; border-radius: 8px; margin-bottom: 24px;">
                <h3 style="color: #334155; margin-bottom: 20px; font-size: 1.2rem; display: flex; align-items: center; gap: 8px;">
                    <span style="background: #64748B; color: white; border-radius: 50%; width: 24px; height: 24px; display: inline-flex; justify-content: center; align-items: center; font-size: 0.9rem;">4</span>
                    Tax Deductions & Net Take-Home Pay Breakdown
                </h3>

                <div style="display: flex; gap: 40px;">
                    <div style="flex: 1.2;">
                        <div class="form-group">
                            <label style="font-weight: 600;">Income Before Tax</label>
                            <input type="text" value="<fmt:formatNumber value='${payroll.incomeBeforeTax}' type='number' maxFractionDigits='0'/> VND" readonly style="background: #FFFFFF;" />
                        </div>

                        <div style="background: #FFFFFF; border: 1px solid #E2E8F0; padding: 15px; border-radius: 6px; margin: 15px 0;">
                            <span style="font-size: 0.85rem; font-weight: bold; color: #64748B; text-transform: uppercase; display: block; margin-bottom: 10px;">Family Relief Details</span>

                            <div class="form-row" style="display: flex; gap: 15px; margin-bottom: 10px;">
                                <div class="form-group" style="flex: 1; margin: 0;">
                                    <label style="font-size: 0.8rem;">Personal Deduction Allowance</label>
                                    <input type="text" value="<fmt:formatNumber value='${setting.selfDeduction}' type='number' maxFractionDigits='0'/> VND" readonly style="font-size: 0.9rem; background: #F1F5F9;" />
                                </div>
                                <div class="form-group" style="flex: 0.5; margin: 0;">
                                    <label style="font-size: 0.8rem;">Dependents Count</label>
                                    <input type="text" value="${numberOfDependents}" readonly style="font-size: 0.9rem; text-align: center; background: #F1F5F9;" />
                                </div>
                            </div>

                            <div class="form-group" style="margin: 0;">
                                <label style="font-size: 0.8rem;">Deduction Amount Per Dependent</label>
                                <input type="text" value="<fmt:formatNumber value='${setting.dependentDeduction}' type='number' maxFractionDigits='0'/> VND / person" readonly style="font-size: 0.9rem; background: #F1F5F9;" />
                            </div>
                        </div>

                        <div class="form-group">
                            <label style="font-weight: 600;">Taxable Income</label>
                            <input type="text" value="<fmt:formatNumber value='${payroll.taxableIncome}' type='number' maxFractionDigits='0'/> VND" readonly style="background: #FFFFFF;" />
                        </div>

                        <div class="form-group">
                            <label style="font-weight: bold; color: #B91C1C;">Personal Income Tax (PIT)</label>
                            <input type="text" value="- <fmt:formatNumber value='${payroll.incomeTax}' type='number' maxFractionDigits='0'/> VND" readonly style="font-weight: bold; color: #B91C1C; background: #FEF2F2;" />
                        </div>
                    </div>

                    <div style="flex: 1; display: flex; flex-direction: column; justify-content: space-between;">
                        <div>
                            <div class="form-group">
                                <label style="font-weight: 600; color: #047857;">Bonus Amount</label>
                                <input type="text" value="+ <fmt:formatNumber value='${payroll.bonus}' type='number' maxFractionDigits='0'/> VND" readonly style="background: #ECFDF5; color: #047857; font-weight: 600;" />
                            </div>
                            <div class="form-group">
                                <label>Bonus Description</label>
                                <textarea readonly style="width: 100%; height: 90px; padding: 8px; border: 1px solid #CBD5E1; border-radius: 4px; background: #FFFFFF; resize: none; font-family: inherit; font-size: 0.9rem;"><c:out value="${payroll.description}" default="No bonus descriptions provided."/></textarea>
                            </div>
                        </div>

                        <div style="background: #2563EB; padding: 20px; border-radius: 6px; color: white; box-shadow: 0 4px 6px -1px rgba(37, 99, 235, 0.2);">
                            <label style="font-weight: bold; font-size: 0.9rem; text-transform: uppercase; letter-spacing: 0.05em; display: block; margin-bottom: 5px; color: #BFDBFE;">Net Take-Home Pay</label>
                            <span style="font-size: 1.8rem; font-weight: 800; display: block;">
                                <fmt:formatNumber value='${payroll.netPay}' type='number' maxFractionDigits='0'/> VND
                            </span>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

</body>
</html>