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
                <div class="alert alert-error">${error}</div>
            </c:if>

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
                    <input type="text" value="${fn:trim(readonlyEmployeeDisplay)}" readonly>
                </div>

                <div class="form-group">
                    <label for="contractCode">Contract Code <span class="required-star">*</span></label>
                    <input type="text" id="contractCode" name="contractCode" value="${contract.contractCode}" required readonly>
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
                    <input type="number" id="baseSalary" name="baseSalary" min="0.01" step="0.01" value="${contract.baseSalary}" required>
                </div>

                <div class="form-group">
                    <label for="workingTime">Working Time <span class="required-star">*</span></label>
                    <input type="text" id="workingTime" name="workingTime" value="${contract.workingTime}" required>
                </div>

                <div class="form-group">
                    <label for="workLocation">Work Location <span class="required-star">*</span></label>
                    <input type="text" id="workLocation" name="workLocation" value="${contract.workLocation}" required>
                </div>

                <div class="form-group">
                    <label for="status">Status <span class="required-star">*</span></label>
                    <input type="hidden" id="status" name="status" value="ACTIVE">
                    <input type="text" value="${contract.status}" readonly>
                </div>

                <div class="form-group">
                    <label for="note">Note</label>
                    <textarea id="note" name="note">${contract.note}</textarea>
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
        const endDateGroup = document.getElementById('endDateGroup');
        const endDate = document.getElementById('endDate');
        const today = new Date().toISOString().split('T')[0];

        endDate.min = today;

        function handleContractTypeChange() {
            const isIndefiniteTerm = contractType.value === 'INDEFINITE_TERM';
            endDateGroup.classList.toggle('hidden', isIndefiniteTerm);
            endDate.disabled = isIndefiniteTerm;
            if (isIndefiniteTerm) {
                endDate.value = '';
            }
        }

        contractType.addEventListener('change', handleContractTypeChange);
        handleContractTypeChange();
    });
</script>
</body>
</html>
