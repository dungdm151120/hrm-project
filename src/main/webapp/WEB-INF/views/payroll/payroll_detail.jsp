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

    <style>
        .dashboard-content { padding: 15px 20px !important; }

        .emp-profile-bar {
            display: flex;
            align-items: center;
            justify-content: space-between;
            background: #ffffff;
            border: 1px solid #e2e8f0;
            border-left: 4px solid #2563eb;
            border-radius: 6px;
            padding: 10px 16px;
            margin-bottom: 12px;
            box-shadow: 0 1px 3px rgba(0,0,0,0.05);
        }
        .emp-info-item { display: flex; flex-direction: column; }
        .emp-info-label { font-size: 0.72rem; color: #64748b; text-transform: uppercase; font-weight: 600; }
        .emp-info-value { font-size: 0.88rem; font-weight: 700; color: #0f172a; }

        .payslip-grid {
            display: grid;
            grid-template-columns: 1fr 1fr 1fr;
            gap: 12px;
        }

        .payslip-card {
            background: #ffffff;
            border: 1px solid #e2e8f0;
            border-radius: 8px;
            padding: 12px 14px;
            display: flex;
            flex-direction: column;
            justify-content: space-between;
        }

        .card-header-title {
            font-size: 0.92rem;
            font-weight: 700;
            padding-bottom: 6px;
            margin-bottom: 8px;
            border-bottom: 2px solid;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .item-row {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 4px 0;
            border-bottom: 1px dashed #f1f5f9;
            font-size: 0.8rem;
        }
        .item-row:last-child { border-bottom: none; }
        .item-label { color: #475569; font-weight: 500; }
        .item-value { font-weight: 600; color: #0f172a; text-align: right; }

        .summary-box {
            border-radius: 6px;
            padding: 8px 10px;
            margin-top: 8px;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }
        .summary-box-gross { background: #eff6ff; border: 1px solid #bfdbfe; }
        .summary-box-deduct { background: #fef2f2; border: 1px solid #fecaca; }
        .summary-box-net {
            background: linear-gradient(135deg, #2563eb, #1d4ed8);
            color: #ffffff;
            border-radius: 6px;
            padding: 10px 12px;
            margin-top: 8px;
        }

        .badge-sub {
            background: #f1f5f9;
            color: #475569;
            font-size: 0.7rem;
            padding: 1px 5px;
            border-radius: 3px;
            margin-left: 3px;
        }
        .bonus-desc {
            font-size: 0.74rem;
            color: #64748b;
            background: #f8fafc;
            border-left: 2px solid #3b82f6;
            padding: 3px 6px;
            margin: 4px 0;
            border-radius: 0 4px 4px 0;
        }
    </style>
</head>
<body class="dashboard-body">

<div class="dashboard-wrapper">

    <jsp:include page="/WEB-INF/views/common/sidebar.jsp"/>

    <div class="dashboard-main">

        <div class="dashboard-header" style="margin-bottom: 8px;">
            <div class="header-left">
                <h1 class="header-title" style="font-size: 1.3rem;">Employee Payslip Detail</h1>
            </div>
            <div class="header-right">
                <c:set var="statusLower" value="${fn:toLowerCase(not empty payroll.status ? payroll.status : 'draft')}" />
                <span class="badge ${statusLower == 'confirmed' ? 'badge-active' : 'badge-inactive'}" style="font-size: 0.75rem; padding: 3px 8px;">
                     <c:out value="${statusLower == 'confirmed' ? 'CONFIRMED' : 'DRAFT'}" />
                </span>
            </div>
        </div>

        <c:if test="${not empty sessionScope.message}">
            <div class="alert alert-success" style="padding: 5px 10px; font-size: 0.82rem; margin-bottom: 8px;">
                ${sessionScope.message}
            </div>
            <% session.removeAttribute("message"); %>
        </c:if>

        <form action="${pageContext.request.contextPath}/payroll/confirm" method="post">
            <input type="hidden" name="id" value="${payroll.id}">

            <div class="dashboard-content">

                <div style="margin-bottom: 8px;">
                    <a class="back-link" href="${pageContext.request.contextPath}/payroll/list?month=${param.month}&year=${param.year}" style="font-size: 0.8rem;">
                        Return to payroll list
                    </a>
                </div>

                <div class="emp-profile-bar">
                    <div class="emp-info-item">
                        <span class="emp-info-label">Employee Code</span>
                        <span class="emp-info-value" style="color: #2563eb;"><c:out value='${employee.employeeCode}' default='N/A'/></span>
                    </div>
                    <div class="emp-info-item">
                        <span class="emp-info-label">Full Name</span>
                        <span class="emp-info-value"><c:out value='${employee.fullName}' default='Unknown'/></span>
                    </div>
                    <div class="emp-info-item">
                        <span class="emp-info-label">Department</span>
                        <span class="emp-info-value"><c:out value='${employee.departmentName}' default='N/A'/></span>
                    </div>
                    <div class="emp-info-item">
                        <span class="emp-info-label">Job Position</span>
                        <span class="emp-info-value"><c:out value='${employee.positionName}' default='N/A'/></span>
                    </div>
                </div>

                <div class="payslip-grid">

                    <div class="payslip-card">
                        <div>
                            <div class="card-header-title" style="color: #1e3a8a; border-color: #3b82f6;">
                                <span>1. Earnings & Working Hours</span>
                            </div>

                            <div class="item-row">
                                <span class="item-label">Hours (Act / Exp)</span>
                                <span class="item-value"><strong>${payroll.actualHours}</strong> / ${payroll.expectedHours} hrs</span>
                            </div>
                            <div class="item-row">
                                <span class="item-label">Basic Salary</span>
                                <span class="item-value"><fmt:formatNumber value='${payroll.basicSalary}' type='number' maxFractionDigits='0'/> VND</span>
                            </div>
                            <div class="item-row">
                                <span class="item-label" style="font-weight: 600; color: #1e40af;">Actual Base Income</span>
                                <span class="item-value" style="font-weight: 700; color: #1e40af;"><fmt:formatNumber value='${payroll.actualBasicSalary}' type='number' maxFractionDigits='0'/> VND</span>
                            </div>
                            <div class="item-row" style="border-bottom: none; padding-bottom: 0;">
                                <span class="item-label" style="font-weight: 600; color: #1e40af;">Allowance</span>
                                <span class="item-value" style="color: #059669;">+<fmt:formatNumber value='${payroll.bonus}' type='number' maxFractionDigits='0'/> VND</span>
                            </div>
                            <div class="bonus-desc">
                                <strong>Note:</strong> <c:out value="${not empty payroll.description ? payroll.description : 'Position allowance & fixed allowances'}" />
                            </div>
                        </div>

                        <div class="summary-box summary-box-gross">
                            <span style="font-size: 0.8rem; font-weight: 700; color: #1e40af;">TOTAL GROSS INCOME</span>
                            <span style="font-size: 0.95rem; font-weight: 800; color: #1e40af;">
                                <fmt:formatNumber value='${payroll.totalIncome}' type='number' maxFractionDigits='0'/> VND
                            </span>
                        </div>
                    </div>

                    <div class="payslip-card">
                        <div>
                            <div class="card-header-title" style="color: #991b1b; border-color: #ef4444;">
                                <span>2. Deductions & Tax Reliefs</span>
                            </div>

                            <div style="font-size: 0.72rem; font-weight: 700; color: #94a3b8; text-transform: uppercase; margin-bottom: 2px;">Mandatory Insurances</div>
                            <div class="item-row">
                                <span class="item-label">Social Ins. (${setting.employeeSocialInsurance}%)</span>
                                <span class="item-value" style="color: #dc2626;">-<fmt:formatNumber value='${payroll.socialInsurance}' type='number' maxFractionDigits='0'/></span>
                            </div>
                            <div class="item-row">
                                <span class="item-label">Health Ins. (${setting.employeeHealthInsurance}%)</span>
                                <span class="item-value" style="color: #dc2626;">-<fmt:formatNumber value='${payroll.healthInsurance}' type='number' maxFractionDigits='0'/></span>
                            </div>
                            <div class="item-row">
                                <span class="item-label">Unemployment Ins. (${setting.employeeUnemploymentInsurance}%)</span>
                                <span class="item-value" style="color: #dc2626;">-<fmt:formatNumber value='${payroll.unemploymentInsurance}' type='number' maxFractionDigits='0'/></span>
                            </div>
                            <div class="item-row">
                                <span class="item-label">Union Fee (${setting.employeeUnion}%)</span>
                                <span class="item-value" style="color: #dc2626;">-<fmt:formatNumber value='${payroll.unionFee}' type='number' maxFractionDigits='0'/></span>
                            </div>

                            <div style="font-size: 0.72rem; font-weight: 700; color: #94a3b8; text-transform: uppercase; margin-top: 8px; margin-bottom: 2px;">PIT Tax Reliefs</div>
                            <div class="item-row">
                                <span class="item-label">Personal Relief Allowance</span>
                                <span class="item-value"><fmt:formatNumber value='${setting.selfDeduction}' type='number' maxFractionDigits='0'/></span>
                            </div>
                            <div class="item-row">
                                <span class="item-label">Dependents Relief <span class="badge-sub">${numberOfDependents} pers</span></span>
                                <span class="item-value"><fmt:formatNumber value='${setting.dependentDeduction * numberOfDependents}' type='number' maxFractionDigits='0'/></span>
                            </div>
                        </div>

                        <div class="summary-box summary-box-deduct">
                            <span style="font-size: 0.78rem; font-weight: 700; color: #991b1b;">TOTAL DEDUCTION</span>
                            <span style="font-size: 0.9rem; font-weight: 800; color: #991b1b;">
                                -<fmt:formatNumber value='${payroll.socialInsurance + payroll.healthInsurance + payroll.unemploymentInsurance + payroll.unionFee}' type='number' maxFractionDigits='0'/> VND
                            </span>
                        </div>
                    </div>

                    <div class="payslip-card">
                        <div>
                            <div class="card-header-title" style="color: #047857; border-color: #10b981;">
                                <span>3. PIT Tax & Net Take-Home</span>
                            </div>

                            <div class="item-row">
                                <span class="item-label">Income Before Tax</span>
                                <span class="item-value"><fmt:formatNumber value='${payroll.incomeBeforeTax}' type='number' maxFractionDigits='0'/> VND</span>
                            </div>
                            <div class="item-row">
                                <span class="item-label">Taxable Income</span>
                                <span class="item-value"><fmt:formatNumber value='${payroll.taxableIncome}' type='number' maxFractionDigits='0'/> VND</span>
                            </div>
                            <div class="item-row" style="background: #fef2f2; padding: 4px 6px; border-radius: 4px; margin-top: 4px; margin-bottom: 6px;">
                                <span class="item-label" style="font-weight: 700; color: #b91c1c;">Personal Income Tax (PIT)</span>
                                <span class="item-value" style="font-weight: 800; color: #b91c1c;">-<fmt:formatNumber value='${payroll.incomeTax}' type='number' maxFractionDigits='0'/> VND</span>
                            </div>

                            <div style="font-size: 0.72rem; font-weight: 700; color: #94a3b8; text-transform: uppercase; margin-top: 8px; margin-bottom: 2px;">Non-Taxable Additions</div>
                            <div class="item-row">
                                <span class="item-label">Overtime Pay <span class="badge-sub">${overtimeHours != null ? overtimeHours : 0.0}h</span></span>
                                <span class="item-value" style="color: #059669;">+<fmt:formatNumber value='${payroll.overtimePay}' type='number' maxFractionDigits='0'/> VND</span>
                            </div>
                            <div class="item-row">
                                <span class="item-label">Sick Leave Pay <span class="badge-sub">${sickLeaveDays != null ? sickLeaveDays : 0}d</span></span>
                                <span class="item-value" style="color: #059669;">+<fmt:formatNumber value='${payroll.sickLeavePay}' type='number' maxFractionDigits='0'/> VND</span>
                            </div>
                        </div>

                        <div>
                            <div class="summary-box-net">
                                <div style="display: flex; justify-content: space-between; align-items: center;">
                                    <span style="font-size: 0.72rem; font-weight: 700; text-transform: uppercase; letter-spacing: 0.5px; opacity: 0.95;">Net Take-Home Pay</span>
                                    <span style="font-size: 0.68rem; background: rgba(255,255,255,0.25); padding: 1px 5px; border-radius: 3px;">FINAL</span>
                                </div>
                                <div style="font-size: 1.4rem; font-weight: 800; margin-top: 2px;">
                                    <fmt:formatNumber value='${payroll.netPay}' type='number' maxFractionDigits='0'/> <span style="font-size: 0.85rem; font-weight: 500;">VND</span>
                                </div>
                            </div>

                            <c:if test="${statusLower == 'draft' && userPermissions.contains('PAYROLL_CONFIRM')}">
                                <div style="margin-top: 8px;">
                                    <button type="submit" class="btn-save" style="width: 100%; padding: 7px; font-size: 0.85rem; font-weight: bold; text-align: center; justify-content: center;">
                                        Confirm Payroll Statement
                                    </button>
                                </div>
                            </c:if>
                        </div>
                    </div>

                </div>

            </div>
        </form>
    </div>
</div>

</body>
</html>