<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Contract Form | HRM</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body class="dashboard-page">
<div class="dashboard-layout">
    <jsp:include page="/WEB-INF/views/common/sidebar.jsp" />

    <main class="main-content">
        <header class="main-header">
            <h1 class="header-title">
                <c:choose>
                    <c:when test="${not empty contract.id && contract.id > 0}">Update Contract</c:when>
                    <c:otherwise>Add Contract</c:otherwise>
                </c:choose>
            </h1>
            <div class="header-right">
                <a href="${pageContext.request.contextPath}/contracts" class="btn btn-secondary">← Back to List</a>
            </div>
        </header>

        <div class="dashboard-content">
            <c:if test="${not empty error}">
                <div class="alert alert-error">⚠ ${error}</div>
            </c:if>

            <div class="form-wrapper">
                <form action="${formAction}" method="post">
                    <c:if test="${not empty contract.id && contract.id > 0}">
                        <input type="hidden" name="id" value="${contract.id}">
                    </c:if>

                    <div class="form-group">
                        <label for="userId">Employee <span style="color: var(--danger);">*</span></label>
                        <select id="userId" name="userId" required>
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
                        <select id="contractType" name="contractType" required>
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
                        <select id="status" name="status" required>
                            <option value="ACTIVE" ${empty contract.status || contract.status == 'ACTIVE' ? 'selected' : ''}>ACTIVE</option>
                            <option value="EXPIRED" ${contract.status == 'EXPIRED' ? 'selected' : ''}>EXPIRED</option>
                            <option value="TERMINATED" ${contract.status == 'TERMINATED' ? 'selected' : ''}>TERMINATED</option>
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
        </div>
    </main>
</div>
</body>
</html>