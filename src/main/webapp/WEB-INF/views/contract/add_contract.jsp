<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Add Contract | HRM</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body>

<jsp:include page="/WEB-INF/views/common/navbar.jsp" />

<div class="container" style="margin-top: 2rem;">
    <a class="back-link" href="${pageContext.request.contextPath}/contracts">Back to contract list</a>
    <h2 class="form-title">Add Contract</h2>

    <c:if test="${not empty error}">
        <div class="alert alert-error">${error}</div>
    </c:if>

    <form action="${pageContext.request.contextPath}/contracts/add" method="post">
        <div class="form-group">
            <label for="userId">Employee <span style="color: var(--danger);">*</span></label>
            <select id="userId" name="userId" class="form-select" required>
                <option value="">Select employee</option>
                <c:forEach items="${users}" var="user">
                    <option value="${user.id}" ${contract.userId == user.id ? 'selected' : ''}>
                        ${user.fullName} - ${user.email}
                    </option>
                </c:forEach>
            </select>
        </div>

        <div class="form-group">
            <label for="contractCode">Contract Code <span style="color: var(--danger);">*</span></label>
            <input type="text" id="contractCode" name="contractCode" value="${contract.contractCode}" required>
        </div>

        <div class="form-group">
            <label for="contractType">Contract Type <span style="color: var(--danger);">*</span></label>
            <select id="contractType" name="contractType" class="form-select" required>
                <option value="FIXED_TERM" ${contract.contractType == 'FIXED_TERM' ? 'selected' : ''}>FIXED_TERM</option>
                <option value="INDEFINITE_TERM" ${contract.contractType == 'INDEFINITE_TERM' ? 'selected' : ''}>INDEFINITE_TERM</option>
                <option value="PROBATION" ${contract.contractType == 'PROBATION' ? 'selected' : ''}>PROBATION</option>
                <option value="PART_TIME" ${contract.contractType == 'PART_TIME' ? 'selected' : ''}>PART_TIME</option>
            </select>
        </div>

        <div class="form-group">
            <label for="startDate">Start Date <span style="color: var(--danger);">*</span></label>
            <input type="date" id="startDate" name="startDate" value="${contract.startDate}" required>
        </div>

        <div class="form-group">
            <label for="endDate">End Date</label>
            <input type="date" id="endDate" name="endDate" value="${contract.endDate}">
        </div>

        <div class="form-group">
            <label for="baseSalary">Base Salary</label>
            <input type="number" id="baseSalary" name="baseSalary" min="0" step="0.01" value="${contract.baseSalary}">
        </div>

        <div class="form-group">
            <label for="workingTime">Working Time</label>
            <input type="text" id="workingTime" name="workingTime" value="${contract.workingTime}">
        </div>

        <div class="form-group">
            <label for="workLocation">Work Location</label>
            <input type="text" id="workLocation" name="workLocation" value="${contract.workLocation}">
        </div>

        <div class="form-group">
            <label for="status">Status <span style="color: var(--danger);">*</span></label>
            <select id="status" name="status" class="form-select" required>
                <option value="ACTIVE" selected>ACTIVE</option>
            </select>
        </div>

        <div class="form-group">
            <label for="fileUrl">File URL</label>
            <input type="url" id="fileUrl" name="fileUrl" value="${contract.fileUrl}">
        </div>

        <div class="form-group">
            <label for="note">Note</label>
            <textarea id="note" name="note">${contract.note}</textarea>
        </div>

        <div class="form-actions">
            <button type="submit" class="btn-save">Save</button>
            <a href="${pageContext.request.contextPath}/contracts" class="btn-cancel">Cancel</a>
        </div>
    </form>
</div>

</body>
</html>
