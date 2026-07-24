<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Payroll Setting Details | HRM</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">

    <style>
        .dashboard-content {
            padding: 12px 20px !important;
        }

        .setting-grid {
            display: grid;
            grid-template-columns: repeat(3, 1fr);
            gap: 16px;
            margin-top: 10px;
        }

        @media (max-width: 992px) {
            .setting-grid {
                grid-template-columns: 1fr;
            }
        }

        .card-box {
            background: #ffffff;
            border: 1px solid #e2e8f0;
            border-radius: 8px;
            padding: 14px 16px;
            box-shadow: 0 1px 3px rgba(0,0,0,0.05);
        }

        .card-title {
            font-size: 0.92rem;
            font-weight: 700;
            color: #1e293b;
            padding-bottom: 8px;
            margin-bottom: 12px;
            border-bottom: 2px solid #2563eb;
            display: flex;
            align-items: center;
            gap: 6px;
        }

        .form-group {
            margin-bottom: 10px;
        }

        .form-label {
            display: block;
            font-size: 0.78rem;
            font-weight: 600;
            color: #475569;
            margin-bottom: 4px;
        }

        .readonly-box {
            background-color: #f8fafc;
            border: 1px solid #cbd5e1;
            padding: 7px 10px;
            border-radius: 6px;
            color: #0f172a;
            font-weight: 600;
            font-size: 0.86rem;
        }
    </style>
</head>
<body class="dashboard-body">

<div class="dashboard-wrapper">
    <jsp:include page="/WEB-INF/views/common/sidebar.jsp"/>

    <div class="dashboard-main">
        <div class="dashboard-header">
            <div class="header-left">
                <h1 class="header-title">Payroll Setting Details</h1>
            </div>
        </div>

        <div class="dashboard-content">
            <div style="margin-bottom: 12px;">
                <a class="back-link" href="${pageContext.request.contextPath}/payroll/setting/list" style="font-size: 0.82rem;">
                    Return to payroll settings list
                </a>
            </div>

            <div class="setting-grid">

                <div class="card-box">
                    <div class="card-title">
                        <span>Employee Deductions & Reliefs</span>
                    </div>

                    <div class="form-group">
                        <label class="form-label">Social Insurance (%)</label>
                        <div class="readonly-box">${setting.employeeSocialInsurance}%</div>
                    </div>

                    <div class="form-group">
                        <label class="form-label">Health Insurance (%)</label>
                        <div class="readonly-box">${setting.employeeHealthInsurance}%</div>
                    </div>

                    <div class="form-group">
                        <label class="form-label">Unemployment Insurance (%)</label>
                        <div class="readonly-box">${setting.employeeUnemploymentInsurance}%</div>
                    </div>

                    <div class="form-group">
                        <label class="form-label">Union Fee (%)</label>
                        <div class="readonly-box">${setting.employeeUnion}%</div>
                    </div>

                    <div class="form-group">
                        <label class="form-label">Self Deduction (VND/month)</label>
                        <div class="readonly-box">
                            <fmt:formatNumber value="${setting.selfDeduction}" type="number" maxFractionDigits="0"/> VND
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="form-label">Dependent Deduction (VND/person/month)</label>
                        <div class="readonly-box">
                            <fmt:formatNumber value="${setting.dependentDeduction}" type="number" maxFractionDigits="0"/> VND
                        </div>
                    </div>
                </div>

                <div class="card-box">
                    <div class="card-title">
                        <span>Company Contributions</span>
                    </div>

                    <div class="form-group">
                        <label class="form-label">Social Insurance (%)</label>
                        <div class="readonly-box">${setting.companySocialInsurance}%</div>
                    </div>

                    <div class="form-group">
                        <label class="form-label">Health Insurance (%)</label>
                        <div class="readonly-box">${setting.companyHealthInsurance}%</div>
                    </div>

                    <div class="form-group">
                        <label class="form-label">Unemployment Insurance (%)</label>
                        <div class="readonly-box">${setting.companyUnemploymentInsurance}%</div>
                    </div>

                    <div class="form-group">
                        <label class="form-label">Union Fee (%)</label>
                        <div class="readonly-box">${setting.companyUnion}%</div>
                    </div>
                </div>

                <div class="card-box">
                    <div class="card-title">
                        <span>OT Rates & Effective Date</span>
                    </div>

                    <div class="form-group">
                        <label class="form-label">Sick Leave Pay Rate (%)</label>
                        <div class="readonly-box">
                            <c:out value="${not empty setting.sickLeaveRate ? setting.sickLeaveRate : setting.sickLeavePayRate}" default="75"/>%
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="form-label">Weekday OT Rate (%)</label>
                        <div class="readonly-box">
                            <c:out value="${not empty setting.otWeekdayRate ? setting.otWeekdayRate : (not empty setting.normalOtRate ? setting.normalOtRate : setting.otWeekday)}" default="150"/>%
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="form-label">Weekend OT Rate (%)</label>
                        <div class="readonly-box">
                            <c:out value="${not empty setting.otWeekendRate ? setting.otWeekendRate : setting.otWeekend}" default="200"/>%
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="form-label">Holiday OT Rate (%)</label>
                        <div class="readonly-box">
                            <c:out value="${not empty setting.otHolidayRate ? setting.otHolidayRate : setting.otHoliday}" default="300"/>%
                        </div>
                    </div>

                    <div class="form-group" style="margin-top: 14px; padding-top: 10px; border-top: 1px dashed #cbd5e1;">
                        <label class="form-label" style="color: #2563eb; font-weight: 700;">Effective Date</label>
                        <div class="readonly-box" style="color: #2563eb; font-weight: 700; background-color: #eff6ff; border-color: #bfdbfe;">
                            <c:out value="${setting.effectiveDate}" default="N/A"/>
                        </div>
                    </div>
                </div>

            </div>
        </div>
    </div>
</div>

</body>
</html>