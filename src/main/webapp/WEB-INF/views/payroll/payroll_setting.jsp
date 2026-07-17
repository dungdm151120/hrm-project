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
        .setting-grid {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 20px;
        }
        .readonly-box {
            background-color: #f8fafc;
            border: 1px solid #cbd5e1;
            padding: 10px 14px;
            border-radius: 6px;
            color: #334155;
            font-weight: 500;
            margin-top: 4px;
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
            <a class="back-link" href="${pageContext.request.contextPath}/payroll/setting/list">Return to setting list</a>
            <h2 class="form-title" style="margin-top: 10px;">Configuration Information (Read-Only)</h2>

            <div class="setting-grid">
                <div>
                    <h3>Employee Insurance Rates (%)</h3>
                    <div class="form-group">
                        <label>Social Insurance (%)</label>
                        <div class="readonly-box">${setting.employeeSocialInsurance}</div>
                    </div>
                    <div class="form-group">
                        <label>Health Insurance (%)</label>
                        <div class="readonly-box">${setting.employeeHealthInsurance}</div>
                    </div>
                    <div class="form-group">
                        <label>Unemployment Insurance (%)</label>
                        <div class="readonly-box">${setting.employeeUnemploymentInsurance}</div>
                    </div>
                    <div class="form-group">
                        <label>Union Fee (%)</label>
                        <div class="readonly-box">${setting.employeeUnion}</div>
                    </div>
                </div>

                <div>
                        <h3 class="form-section-title">OT & Sick Leave Configurations</h3>
                        <div class="form-group">
                            <label>Sick Leave Benefit Rate (%)</label>
                            <div class="readonly-box">${setting.sickLeaveRate}</div>
                        </div>
                        <div class="form-group">
                            <label>OT Weekday Rate (%)</label>
                            <div class="readonly-box">${setting.otWeekdayRate}</div>
                        </div>
                        <div class="form-group">
                            <label>OT Weekend Rate (%)</label>
                            <div class="readonly-box">${setting.otWeekendRate}</div>
                        </div>
                        <div class="form-group">
                            <label>OT Holiday Rate (%)</label>
                            <div class="readonly-box">${setting.otHolidayRate}</div>
                        </div>
                    </div>

                <div>
                    <h3>Company Insurance Rates & Deductions</h3>
                    <div class="form-group">
                        <label>Company Social Insurance (%)</label>
                        <div class="readonly-box">${setting.companySocialInsurance}</div>
                    </div>
                    <div class="form-group">
                        <label>Company Health Insurance (%)</label>
                        <div class="readonly-box">${setting.companyHealthInsurance}</div>
                    </div>
                    <div class="form-group">
                        <label>Company Unemployment Insurance (%)</label>
                        <div class="readonly-box">${setting.companyUnemploymentInsurance}</div>
                    </div>
                    <div class="form-group">
                        <label>Company Union Fee (%)</label>
                        <div class="readonly-box">${setting.companyUnion}</div>
                    </div>
                    <div class="form-group">
                        <label>Self Deduction (VND)</label>
                        <div class="readonly-box"><fmt:formatNumber value="${setting.selfDeduction}" type="number"/> VND</div>
                    </div>
                    <div class="form-group">
                        <label>Dependent Deduction (VND)</label>
                        <div class="readonly-box"><fmt:formatNumber value="${setting.dependentDeduction}" type="number"/> VND</div>
                    </div>
                    <div class="form-group">
                        <label>Effective Date</label>
                        <div class="readonly-box">${setting.effectiveDate}</div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

</body>
</html>