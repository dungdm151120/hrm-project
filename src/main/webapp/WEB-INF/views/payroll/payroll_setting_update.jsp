<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Create / Update Payroll Setting | HRM</title>
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

        .card-box {
            background: #ffffff;
            border: 1px solid #e2e8f0;
            border-radius: 8px;
            padding: 12px 14px;
            box-shadow: 0 1px 3px rgba(0,0,0,0.02);
        }

        .form-section-title {
            font-size: 0.9rem;
            font-weight: 700;
            margin-bottom: 10px;
            color: #0f172a;
            border-bottom: 2px solid #3b82f6;
            padding-bottom: 4px;
            text-transform: uppercase;
            letter-spacing: 0.3px;
        }

        .form-group {
            margin-bottom: 8px;
        }

        .form-group label {
            display: block;
            font-size: 0.78rem;
            font-weight: 600;
            color: #475569;
            margin-bottom: 2px;
        }

        .form-control {
            width: 100%;
            padding: 5px 10px;
            font-size: 0.82rem;
            border: 1px solid #cbd5e1;
            border-radius: 5px;
            background-color: #f8fafc;
            color: #0f172a;
            box-sizing: border-box;
            transition: all 0.2s;
        }

        .form-control:focus {
            background-color: #ffffff;
            border-color: #2563eb;
            outline: none;
            box-shadow: 0 0 0 2px rgba(37, 99, 235, 0.1);
        }

        .form-actions {
            margin-top: 14px;
            display: flex;
            gap: 10px;
            justify-content: flex-end;
            border-top: 1px solid #e2e8f0;
            padding-top: 10px;
        }

        .btn-save {
            padding: 6px 18px;
            font-size: 0.82rem;
            font-weight: 600;
        }

        .btn-cancel {
            padding: 6px 16px;
            font-size: 0.82rem;
            background: #f1f5f9;
            color: #475569;
            border-radius: 6px;
            text-decoration: none;
            display: inline-flex;
            align-items: center;
        }
        .btn-cancel:hover {
            background: #e2e8f0;
        }
    </style>
</head>
<body class="dashboard-body">

<div class="dashboard-wrapper">
    <jsp:include page="/WEB-INF/views/common/sidebar.jsp"/>

    <div class="dashboard-main">
        <div class="dashboard-header" style="padding-bottom: 8px;">
            <div class="header-left">
                <h1 class="header-title" style="font-size: 1.25rem;">Configure Payroll & Insurance Rates</h1>
            </div>
        </div>

        <div class="dashboard-content">
            <div style="display: flex; justify-content: space-between; align-items: center;">
                <a class="back-link" href="${pageContext.request.contextPath}/payroll/setting/list" style="font-size: 0.8rem;">
                   Back to setting list
                </a>
            </div>

            <c:if test="${not empty sessionScope.error}">
                <div class="alert alert-danger" style="padding: 6px 12px; font-size: 0.8rem; margin-top: 6px;">${sessionScope.error}</div>
                <% session.removeAttribute("error"); %>
            </c:if>
            <c:if test="${not empty sessionScope.message}">
                <div class="alert alert-success" style="padding: 6px 12px; font-size: 0.8rem; margin-top: 6px;">${sessionScope.message}</div>
                <% session.removeAttribute("message"); %>
            </c:if>

            <form action="${pageContext.request.contextPath}/payroll/setting/update" method="POST">
                <input type="hidden" name="id" value="${setting.id}">

                <div class="setting-grid">

                    <div class="card-box">
                        <h3 class="form-section-title">Employee Rates & Deductions</h3>

                        <div class="form-group">
                            <label>Social Insurance (%)</label>
                            <input type="number" step="0.01" name="employeeSocialInsurance" class="form-control" value="${setting.employeeSocialInsurance}" required>
                        </div>

                        <div class="form-group">
                            <label>Health Insurance (%)</label>
                            <input type="number" step="0.01" name="employeeHealthInsurance" class="form-control" value="${setting.employeeHealthInsurance}" required>
                        </div>

                        <div class="form-group">
                            <label>Unemployment Insurance (%)</label>
                            <input type="number" step="0.01" name="employeeUnemploymentInsurance" class="form-control" value="${setting.employeeUnemploymentInsurance}" required>
                        </div>

                        <div class="form-group">
                            <label>Union Fee (%)</label>
                            <input type="number" step="0.01" name="employeeUnion" class="form-control" value="${setting.employeeUnion}" required>
                        </div>

                        <div class="form-group" style="margin-top: 10px; border-top: 1px dashed #cbd5e1; padding-top: 6px;">
                            <label style="color: #1e293b; font-weight: 700;">Self Deduction (VND)</label>
                            <input type="number" name="selfDeduction" class="form-control" value="${setting.selfDeduction}" required>
                        </div>

                        <div class="form-group">
                            <label style="color: #1e293b; font-weight: 700;">Dependent Deduction (VND)</label>
                            <input type="number" name="dependentDeduction" class="form-control" value="${setting.dependentDeduction}" required>
                        </div>
                    </div>

                    <div class="card-box">
                        <h3 class="form-section-title">Company Insurance Rates</h3>

                        <div class="form-group">
                            <label>Company Social Insurance (%)</label>
                            <input type="number" step="0.01" name="companySocialInsurance" class="form-control" value="${setting.companySocialInsurance}" required>
                        </div>

                        <div class="form-group">
                            <label>Company Health Insurance (%)</label>
                            <input type="number" step="0.01" name="companyHealthInsurance" class="form-control" value="${setting.companyHealthInsurance}" required>
                        </div>

                        <div class="form-group">
                            <label>Company Unemployment Insurance (%)</label>
                            <input type="number" step="0.01" name="companyUnemploymentInsurance" class="form-control" value="${setting.companyUnemploymentInsurance}" required>
                        </div>

                        <div class="form-group">
                            <label>Company Union Fee (%)</label>
                            <input type="number" step="0.01" name="companyUnion" class="form-control" value="${setting.companyUnion}" required>
                        </div>
                    </div>

                    <div class="card-box">
                        <h3 class="form-section-title">OT, Leave & Effective Date</h3>

                        <div class="form-group">
                            <label>Sick Leave Benefit Rate (%)</label>
                            <input type="number" step="0.01" name="sickLeaveRate" class="form-control" value="${setting.sickLeaveRate != null ? setting.sickLeaveRate : 75.00}" required>
                        </div>

                        <div class="form-group">
                            <label>OT Weekday Rate (%)</label>
                            <input type="number" step="0.01" name="otWeekdayRate" class="form-control" value="${setting.otWeekdayRate != null ? setting.otWeekdayRate : 150.00}" required>
                        </div>

                        <div class="form-group">
                            <label>OT Weekend Rate (%)</label>
                            <input type="number" step="0.01" name="otWeekendRate" class="form-control" value="${setting.otWeekendRate != null ? setting.otWeekendRate : 200.00}" required>
                        </div>

                        <div class="form-group">
                            <label>OT Holiday Rate (%)</label>
                            <input type="number" step="0.01" name="otHolidayRate" class="form-control" value="${setting.otHolidayRate != null ? setting.otHolidayRate : 300.00}" required>
                        </div>

                        <c:set var="oldYear" value="${not empty setting.effectiveDate ? fn:substring(setting.effectiveDate, 0, 4) : ''}" />
                        <c:set var="oldMonth" value="${not empty setting.effectiveDate ? fn:substring(setting.effectiveDate, 5, 7) : ''}" />

                        <div class="form-group" style="margin-top: 10px; border-top: 1px dashed #cbd5e1; padding-top: 6px;">
                            <label style="color: #1e293b; font-weight: 700;">Effective Period</label>
                            <div style="display: flex; gap: 8px;">
                                <div style="flex: 1;">
                                    <select name="effectiveMonth" class="form-control" required>
                                        <option value="">Month...</option>
                                        <c:forEach var="m" begin="1" end="12">
                                            <c:set var="mStr" value="${m < 10 ? '0'.concat(m) : ''.concat(m)}" />
                                            <option value="${m}" ${oldMonth == mStr ? 'selected' : ''}>
                                                Month ${m}
                                            </option>
                                        </c:forEach>
                                    </select>
                                </div>

                                <div style="flex: 1;">
                                    <select name="effectiveYear" class="form-control" required>
                                        <option value="">Year...</option>
                                        <c:set var="currentYear" value="<%= java.time.Year.now().getValue() %>"/>
                                        <c:forEach var="y" begin="${currentYear - 3}" end="${currentYear + 1}">
                                            <option value="${y}" ${oldYear == ''.concat(y) ? 'selected' : (empty oldYear && y == currentYear ? 'selected' : '')}>
                                                Year ${y}
                                            </option>
                                        </c:forEach>
                                    </select>
                                </div>
                            </div>
                            <small style="color: #64748b; font-size: 0.7rem; display: block; margin-top: 3px;">
                                * Auto-applied from 1st day of month.
                            </small>
                        </div>
                    </div>

                </div>

                <div class="form-actions">
                    <button type="submit" class="btn-save">Apply Configuration</button>
                    <a href="${pageContext.request.contextPath}/payroll/setting/list" class="btn-cancel">Cancel</a>
                </div>
            </form>
        </div>
    </div>
</div>

</body>
</html>