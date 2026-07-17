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
        .setting-grid {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 20px;
        }
        .form-section-title {
            margin-bottom: 15px;
            color: #1e293b;
            border-bottom: 2px solid #e2e8f0;
            padding-bottom: 6px;
        }
    </style>
</head>
<body class="dashboard-body">

<div class="dashboard-wrapper">
    <jsp:include page="/WEB-INF/views/common/sidebar.jsp"/>

    <div class="dashboard-main">
        <div class="dashboard-header">
            <div class="header-left">
                <h1 class="header-title">Configure New Payroll & Insurance Rates</h1>
            </div>
        </div>

        <div class="dashboard-content">
            <a class="back-link" href="${pageContext.request.contextPath}/payroll/setting/list">&larr; Back to setting list</a>
            <h2 class="form-title" style="margin-top: 10px;">Modify Configuration Parameters</h2>

            <c:if test="${not empty sessionScope.error}">
                <div class="alert alert-danger">${sessionScope.error}</div>
                <% session.removeAttribute("error"); %>
            </c:if>
            <c:if test="${not empty sessionScope.message}">
                <div class="alert alert-success">${sessionScope.message}</div>
                <% session.removeAttribute("message"); %>
            </c:if>

            <form action="${pageContext.request.contextPath}/payroll/setting/update" method="POST">
                <input type="hidden" name="id" value="${setting.id}">

                <div class="setting-grid">
                    <div>
                        <h3 class="form-section-title">Employee Insurance Rates (%)</h3>
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
                    </div>

                    <div>
                        <h3 class="form-section-title">OT & Sick Leave Configurations</h3>
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
                    </div>

                    <div>
                        <h3 class="form-section-title">Company Insurance Rates & Deductions</h3>
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

                        <div class="form-group">
                            <label>Self Deduction (VND)</label>
                            <input type="number" name="selfDeduction" class="form-control" value="${setting.selfDeduction}" required>
                        </div>

                        <div class="form-group">
                            <label>Dependent Deduction (VND)</label>
                            <input type="number" name="dependentDeduction" class="form-control" value="${setting.dependentDeduction}" required>
                        </div>

                        <c:set var="oldYear" value="${not empty setting.effectiveDate ? fn:substring(setting.effectiveDate, 0, 4) : ''}" />
                        <c:set var="oldMonth" value="${not empty setting.effectiveDate ? fn:substring(setting.effectiveDate, 5, 7) : ''}" />

                        <div class="form-group">
                            <label style="display: block; margin-bottom: 8px;">Effective Period</label>
                            <div style="display: flex; gap: 15px;">
                                <div style="flex: 1;">
                                    <select name="effectiveMonth" class="form-control" required>
                                        <option value="">-- Select Month --</option>
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
                                        <option value="">-- Select Year --</option>
                                        <c:set var="currentYear" value="<%= java.time.Year.now().getValue() %>"/>
                                        <c:forEach var="y" begin="${currentYear - 3}" end="${currentYear + 3}">
                                            <option value="${y}" ${oldYear == ''.concat(y) ? 'selected' : (empty oldYear && y == currentYear ? 'selected' : '')}>
                                                Year ${y}
                                            </option>
                                        </c:forEach>
                                    </select>
                                </div>
                            </div>
                            <small style="color: #64748b; display: block; margin-top: 6px;">
                                * The system will automatically apply this setting from the first date of the selected month.
                            </small>
                        </div>
                    </div>
                </div>

                <div class="form-actions" style="margin-top: 25px;">
                    <button type="submit" class="btn-save">Apply Configuration</button>
                    <a href="${pageContext.request.contextPath}/payroll/setting/list" class="btn-cancel">Cancel</a>
                </div>
            </form>
        </div>
    </div>
</div>

</body>
</html>