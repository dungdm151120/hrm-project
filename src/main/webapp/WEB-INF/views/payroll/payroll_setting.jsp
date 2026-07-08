<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Update Payroll Settings | HRM</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body class="dashboard-body">

<div class="dashboard-wrapper">
    <jsp:include page="/WEB-INF/views/common/sidebar.jsp"/>

    <div class="dashboard-main">
        <div class="dashboard-header">
            <div class="header-left">
                <h1 class="header-title">Update Payroll & Insurance Rates</h1>
            </div>
        </div>

        <div class="dashboard-content">
            <a class="back-link" href="${pageContext.request.contextPath}/payroll/update_component">Back to settings</a>
            <h2 class="form-title">Configure Payroll Settings</h2>

            <c:if test="${not empty sessionScope.error}">
                <div class="alert alert-danger">${sessionScope.error}</div>
                <% session.removeAttribute("error"); %>
            </c:if>
            <c:if test="${not empty sessionScope.message}">
                <div class="alert alert-success">${sessionScope.message}</div>
                <% session.removeAttribute("message"); %>
            </c:if>

            <form action="${pageContext.request.contextPath}/payroll/setting" method="POST">
                <input type="hidden" name="id" value="${setting.id}">

                <div class="form-group">
                    <h3>Employee Insurance Rates (%)</h3>
                    <div class="form-group">
                        <label>Social Insurance (%)</label>
                        <input type="number" name="employeeSocialInsurance" class="form-control" value="${setting.employeeSocialInsurance}" required>
                    </div>
                    <div class="form-group">
                        <label>Health Insurance (%)</label>
                        <input type="number" name="employeeHealthInsurance" class="form-control" value="${setting.employeeHealthInsurance}" required>
                    </div>
                    <div class="form-group">
                        <label>Unemployment Insurance (%)</label>
                        <input type="number" name="employeeUnemploymentInsurance" class="form-control" value="${setting.employeeUnemploymentInsurance}" required>
                    </div>
                    <div class="form-group">
                        <label>Union Fee (%)</label>
                        <input type="number" name="employeeUnion" class="form-control" value="${setting.employeeUnion}" required>
                    </div>
                </div>

                <div class="form-group">
                    <h3>Company Insurance Rates (%)</h3>
                    <div class="form-group">
                        <label>Social Insurance (%)</label>
                        <input type="number" name="companySocialInsurance" class="form-control" value="${setting.companySocialInsurance}" required>
                    </div>
                    <div class="form-group">
                        <label>Health Insurance (%)</label>
                        <input type="number" name="companyHealthInsurance" class="form-control" value="${setting.companyHealthInsurance}" required>
                    </div>
                    <div class="form-group">
                        <label>Unemployment Insurance (%)</label>
                        <input type="number" name="companyUnemploymentInsurance" class="form-control" value="${setting.companyUnemploymentInsurance}" required>
                    </div>
                    <div class="form-group">
                        <label>Company Union Fee (%)</label>
                        <input type="number" name="companyUnion" class="form-control" value="${setting.companyUnion}" required>
                    </div>
                </div>

                <div class="form-group">
                    <h3>Deductions & Effective Date</h3>
                    <div class="form-group">
                        <label>Self Deduction (VND)</label>
                        <input type="number" name="selfDeduction" class="form-control" value="${setting.selfDeduction}" required>
                    </div>
                    <div class="form-group">
                        <label>Dependent Deduction (VND)</label>
                        <input type="number" name="dependentDeduction" class="form-control" value="${setting.dependentDeduction}" required>
                    </div>
                    <div class="form-group">
                        <label>Effective Date</label>
                        <input type="date" name="effectiveDate" class="form-control" value="${setting.effectiveDate}" required>
                    </div>
                </div>

                <div class="form-actions">
                    <button type="submit" class="btn-save">Save Changes</button>
                    <a href="${pageContext.request.contextPath}/payroll/update_component" class="btn-cancel">Cancel</a>
                </div>
            </form>
        </div>
    </div>
</div>

</body>
</html>