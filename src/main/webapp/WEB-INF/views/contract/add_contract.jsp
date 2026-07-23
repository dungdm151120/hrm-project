<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Add Contract | HRM</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body class="dashboard-body">

<div class="dashboard-wrapper">

    <jsp:include page="/WEB-INF/views/common/sidebar.jsp"/>

    <div class="dashboard-main">
        <div class="dashboard-header">
            <div class="header-left">
                <h1 class="header-title">Add Contract</h1>
            </div>
        </div>

        <div class="dashboard-content">
            <a class="back-link" href="${pageContext.request.contextPath}/contracts">Back to contract list</a>
            <h2 class="form-title">Add Contract</h2>

            <c:if test="${not empty error}">
                <div class="alert alert-error">${error}</div>
            </c:if>

            <form action="${pageContext.request.contextPath}/contracts/add" method="post">
                <div class="form-group">
                    <label for="userId">Employee <span class="required-star">*</span></label>
                    <input type="text" id="employeeSearch" placeholder="Search employee by name or email...">
                    <select id="userId" name="userId" required>
                        <option value="">Select employee</option>
                        <c:forEach items="${users}" var="user">
                            <option value="${user.id}"
                                    data-search="${user.fullName} ${user.email}"
                                    ${contract.userId == user.id ? 'selected' : ''}>
                                    ${user.fullName} - ${user.email}
                            </option>
                        </c:forEach>
                    </select>
                </div>

                <div class="form-group">
                    <label for="contractCode">Contract Code <span class="required-star">*</span></label>
                    <input type="text" id="contractCode" name="contractCode" value="${contract.contractCode}"
                           maxlength="50" pattern="[A-Za-z0-9/_-]+"
                           title="Use only letters, numbers, hyphens, underscores, and slashes" required>
                </div>

                <div class="form-group">
                    <label for="contractType">Contract Type <span class="required-star">*</span></label>
                    <select id="contractType" name="contractType" required>
                        <option value="FIXED_TERM" ${contract.contractType == 'FIXED_TERM' ? 'selected' : ''}>FIXED_TERM</option>
                        <option value="INDEFINITE_TERM" ${contract.contractType == 'INDEFINITE_TERM' ? 'selected' : ''}>INDEFINITE_TERM</option>
                        <option value="PROBATION" ${contract.contractType == 'PROBATION' ? 'selected' : ''}>PROBATION</option>
                        <option value="PART_TIME" ${contract.contractType == 'PART_TIME' ? 'selected' : ''}>PART_TIME</option>
                    </select>
                </div>

                <div class="form-group">
                    <label for="startDate">Start Date <span class="required-star">*</span></label>
                    <input type="date" id="startDate" name="startDate" value="${contract.startDate}" required>
                </div>

                <div class="form-group" id="endDateGroup">
                    <label for="endDate">End Date <span class="required-star">*</span></label>
                    <input type="date" id="endDate" name="endDate" value="${contract.endDate}" required>
                </div>

                <div class="form-group">
                    <label for="baseSalary">Base Salary <span class="required-star">*</span></label>
                    <input type="number" id="baseSalary" name="baseSalary" min="0.01" max="9999999999999.99"
                           step="0.01" value="${contract.baseSalary}" required>
                </div>

                <div class="form-group">
                    <label for="workingTime">Working Time <span class="required-star">*</span></label>
                    <input type="text" id="workingTime" name="workingTime" value="${contract.workingTime}"
                           maxlength="100" required>
                </div>

                <div class="form-group">
                    <label for="workLocation">Work Location <span class="required-star">*</span></label>
                    <input type="text" id="workLocation" name="workLocation" value="${contract.workLocation}"
                           maxlength="255" required>
                </div>

                <div class="form-group">
                    <label for="status">Status <span class="required-star">*</span></label>
                    <input type="hidden" id="status" name="status" value="ACTIVE">
                    <input type="text" value="ACTIVE" readonly>
                </div>

                <div class="form-group">
                    <label for="note">Note</label>
                    <textarea id="note" name="note" maxlength="1000">${contract.note}</textarea>
                </div>

                <div class="form-actions">
                    <button type="submit" class="btn-save">Save</button>
                    <a href="${pageContext.request.contextPath}/contracts" class="btn-cancel">Cancel</a>
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
        const employeeSearch = document.getElementById('employeeSearch');
        const employeeSelect = document.getElementById('userId');
        const employeeOptions = Array.from(employeeSelect.options);
        const today = new Date().toISOString().split('T')[0];

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

        function handleEmployeeSearch() {
            const keyword = employeeSearch.value.trim().toLowerCase();
            employeeOptions.forEach(function (option) {
                if (!option.value) {
                    option.hidden = false;
                    return;
                }
                option.hidden = !option.dataset.search.toLowerCase().includes(keyword);
            });
        }

        contractType.addEventListener('change', handleContractTypeChange);
        startDate.addEventListener('change', updateEndDateRange);
        employeeSearch.addEventListener('input', handleEmployeeSearch);
        handleContractTypeChange();
    });
</script>
</body>
</html>
