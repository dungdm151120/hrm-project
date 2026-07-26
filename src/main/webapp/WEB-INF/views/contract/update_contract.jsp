<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Update Contract | HRM</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body class="dashboard-body">

<div class="dashboard-wrapper">

    <jsp:include page="/WEB-INF/views/common/sidebar.jsp"/>

    <div class="dashboard-main">
        <div class="dashboard-header">
            <div class="header-left">
                <h1 class="header-title">Update Contract</h1>
            </div>
        </div>

        <div class="dashboard-content">
            <a class="back-link" href="${pageContext.request.contextPath}/contracts/detail?id=${contract.id}">Back to contract detail</a>
            <h2 class="form-title">Update Contract</h2>

            <c:if test="${not empty error}">
                <div class="alert alert-error"><c:out value="${error}"/></div>
            </c:if>

            <c:set var="formContractType" value="${formSubmitted ? param.contractType : contract.contractType}"/>
            <c:set var="formStartDate" value="${formSubmitted ? param.startDate : contract.startDate}"/>
            <c:set var="formEndDate" value="${formSubmitted ? param.endDate : contract.endDate}"/>
            <c:set var="formBaseSalary" value="${formSubmitted ? param.baseSalary : contract.baseSalary}"/>
            <c:set var="formWorkingTime" value="${formSubmitted ? param.workingTime : contract.workingTime}"/>
            <c:set var="formWorkLocation" value="${formSubmitted ? param.workLocation : contract.workLocation}"/>
            <c:set var="formNote" value="${formSubmitted ? param.note : contract.note}"/>
            <c:set var="formUnionMember"
                   value="${formSubmitted ? not empty param.unionMember : contract.unionMember}"/>

            <form action="${pageContext.request.contextPath}/contracts/update" method="post">
                <input type="hidden" name="id" value="${contract.id}">

                <div class="form-group">
                    <label for="userId">Employee <span class="required-star">*</span></label>
                    <c:choose>
                        <c:when test="${not empty contract.employeeName}">
                            <c:set var="readonlyEmployeeDisplay">
                                ${contract.employeeName}<c:if test="${not empty contract.employeeCode}"> (${contract.employeeCode})</c:if><c:if test="${not empty contract.employeeEmail}"> - ${contract.employeeEmail}</c:if>
                            </c:set>
                        </c:when>
                        <c:otherwise>
                            <c:set var="readonlyEmployeeDisplay" value="Employee ID ${contract.userId}" />
                        </c:otherwise>
                    </c:choose>
                    <input type="hidden" id="userId" name="userId" value="${contract.userId}">
                    <input type="text"
                           value="<c:out value='${fn:trim(readonlyEmployeeDisplay)}'/>" readonly>
                </div>

                <div class="form-group">
                    <label for="contractCode">Contract Code <span class="required-star">*</span></label>
                    <input type="text" id="contractCode" name="contractCode"
                           value="<c:out value='${contract.contractCode}'/>" required readonly>
                </div>

                <div class="form-group">
                    <label for="contractType">Contract Type <span class="required-star">*</span></label>
                    <select id="contractType" name="contractType" required>
                        <option value="FIXED_TERM" ${formContractType == 'FIXED_TERM' ? 'selected' : ''}>FIXED_TERM</option>
                        <option value="INDEFINITE_TERM" ${formContractType == 'INDEFINITE_TERM' ? 'selected' : ''}>INDEFINITE_TERM</option>
                        <option value="PROBATION" ${formContractType == 'PROBATION' ? 'selected' : ''}>PROBATION</option>
                    </select>
                </div>

                <div class="form-group">
                    <label for="startDate">Start Date <span class="required-star">*</span></label>
                    <input type="date" id="startDate" name="startDate"
                           value="<c:out value='${formStartDate}'/>" required>
                </div>

                <div class="form-group" id="endDateGroup">
                    <label for="endDate">End Date <span class="required-star">*</span></label>
                    <input type="date" id="endDate" name="endDate"
                           value="<c:out value='${formEndDate}'/>" required>
                </div>

                <div class="form-group">
                    <label for="baseSalaryDisplay">Base Salary <span class="required-star">*</span></label>
                    <input type="text" id="baseSalaryDisplay" inputmode="decimal"
                           value="<c:out value='${formBaseSalary}'/>"
                           placeholder="e.g. 15,000,000" required>
                    <input type="hidden" id="baseSalary" name="baseSalary"
                           value="<c:out value='${formBaseSalary}'/>">
                </div>

                <div class="form-group">
                    <label for="workingTime">Working Time <span class="required-star">*</span></label>
                    <input type="text" id="workingTime" name="workingTime"
                           value="<c:out value='${formWorkingTime}'/>"
                           maxlength="100" required>
                </div>

                <div class="form-group">
                    <label>
                        <input type="checkbox" name="unionMember" value="true"
                               ${formUnionMember ? 'checked' : ''}>
                        Union member
                    </label>
                </div>

                <div class="form-group">
                    <label for="workLocation">Work Location <span class="required-star">*</span></label>
                    <input type="text" id="workLocation" name="workLocation"
                           value="<c:out value='${formWorkLocation}'/>"
                           maxlength="255" required>
                </div>

                <div class="form-group">
                    <label for="status">Status <span class="required-star">*</span></label>
                    <input type="hidden" id="status" name="status" value="ACTIVE">
                    <input type="text" value="<c:out value='${contract.status}'/>" readonly>
                </div>

                <div class="form-group">
                    <label for="note">Note</label>
                    <textarea id="note" name="note" maxlength="1000"><c:out value="${formNote}"/></textarea>
                </div>

                <div class="form-actions">
                    <button type="submit" class="btn-save">Save</button>
                    <a href="${pageContext.request.contextPath}/contracts/detail?id=${contract.id}" class="btn-cancel">Cancel</a>
                </div>
            </form>
        </div>
    </div>
</div>

<script>
    document.addEventListener('DOMContentLoaded', function () {
        const contractType = document.getElementById('contractType');
        const startDate = document.getElementById('startDate');
        const endDateGroup = document.getElementById('endDateGroup');
        const endDate = document.getElementById('endDate');
        const baseSalaryDisplay = document.getElementById('baseSalaryDisplay');
        const baseSalary = document.getElementById('baseSalary');
        const today = new Date().toISOString().split('T')[0];

        function formatSalary() {
            let value = baseSalaryDisplay.value.replace(/,/g, '').replace(/[^\d.]/g, '');
            const decimalPoint = value.indexOf('.');

            if (decimalPoint >= 0) {
                value = value.substring(0, decimalPoint + 1)
                        + value.substring(decimalPoint + 1).replace(/\./g, '').substring(0, 2);
            }

            const parts = value.split('.');
            const integerPart = parts[0].replace(/^0+(?=\d)/, '');
            const decimalPart = parts.length > 1 ? '.' + parts[1] : '';

            baseSalary.value = integerPart + decimalPart;
            baseSalaryDisplay.value =
                    integerPart.replace(/\B(?=(\d{3})+(?!\d))/g, ',') + decimalPart;

            const numericSalary = Number(baseSalary.value);
            const isValid = baseSalary.value !== ''
                    && numericSalary > 0
                    && numericSalary <= 9999999999999.99;
            baseSalaryDisplay.setCustomValidity(
                    isValid ? '' : 'Base salary must be between 0.01 and 9,999,999,999,999.99.'
            );
        }

        function addDays(dateValue, days) {
            const date = new Date(dateValue + 'T00:00:00Z');
            date.setUTCDate(date.getUTCDate() + days);
            return date.toISOString().split('T')[0];
        }

        function addMonths(dateValue, months) {
            const parts = dateValue.split('-').map(Number);
            const target = new Date(Date.UTC(parts[0], parts[1] - 1 + months, 1));
            const lastDay = new Date(Date.UTC(
                    target.getUTCFullYear(), target.getUTCMonth() + 1, 0
            )).getUTCDate();
            target.setUTCDate(Math.min(parts[2], lastDay));
            return target.toISOString().split('T')[0];
        }

        function laterDate(first, second) {
            return first > second ? first : second;
        }

        function updateEndDateRange() {
            let minimumEndDate = today;
            endDate.removeAttribute('max');

            if (startDate.value) {
                minimumEndDate = laterDate(minimumEndDate, addDays(startDate.value, 1));

                if (contractType.value === 'FIXED_TERM') {
                    minimumEndDate = laterDate(minimumEndDate, addMonths(startDate.value, 1));
                    endDate.max = addMonths(startDate.value, 36);
                }
            }

            endDate.min = minimumEndDate;
        }

        function handleContractTypeChange() {
            const isIndefiniteTerm = contractType.value === 'INDEFINITE_TERM';
            endDateGroup.classList.toggle('hidden', isIndefiniteTerm);
            endDate.disabled = isIndefiniteTerm;
            if (isIndefiniteTerm) {
                endDate.value = '';
            }
            updateEndDateRange();
        }

        contractType.addEventListener('change', handleContractTypeChange);
        startDate.addEventListener('change', updateEndDateRange);
        baseSalaryDisplay.addEventListener('input', formatSalary);
        formatSalary();
        handleContractTypeChange();
    });
</script>
</body>
</html>
