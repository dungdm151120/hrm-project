<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Contract Detail | HRM</title>
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
            <h1 class="header-title">Contract ${contract.contractCode}</h1>
            <div class="header-right">
                <a href="${backUrl}" class="btn btn-secondary">← Back</a>
                <c:if test="${canUpdateContract}">
                    <a href="${pageContext.request.contextPath}/contracts/update?id=${contract.id}" class="btn btn-primary">Update</a>
                </c:if>
            </div>
        </header>

        <div class="dashboard-content">
            <div class="role-detail">
                <div class="role-meta">
                    <span class="role-meta-label">Employee</span>
                    <span class="role-meta-value">${contract.employeeName}</span>
                </div>
                <div class="role-meta">
                    <span class="role-meta-label">Employee Code</span>
                    <span class="role-meta-value">${contract.employeeCode}</span>
                </div>
                <div class="role-meta">
                    <span class="role-meta-label">Email</span>
                    <span class="role-meta-value">${contract.employeeEmail}</span>
                </div>
                <div class="role-meta">
                    <span class="role-meta-label">Status</span>
                    <span class="role-meta-value">${contract.status}</span>
                </div>
                <div class="role-meta">
                    <span class="role-meta-label">Contract Type</span>
                    <span class="role-meta-value">${contract.contractType}</span>
                </div>
                <div class="role-meta">
                    <span class="role-meta-label">Base Salary</span>
                    <span class="role-meta-value">${contract.baseSalary}</span>
                </div>
                <div class="role-meta">
                    <span class="role-meta-label">Start Date</span>
                    <span class="role-meta-value">${contract.startDate}</span>
                </div>
                <div class="role-meta">
                    <span class="role-meta-label">End Date</span>
                    <span class="role-meta-value">
                        <c:choose>
                            <c:when test="${not empty contract.endDate}">${contract.endDate}</c:when>
                            <c:otherwise>Open-ended</c:otherwise>
                        </c:choose>
                    </span>
                </div>
                <div class="role-meta">
                    <span class="role-meta-label">Working Time</span>
                    <span class="role-meta-value">${contract.workingTime}</span>
                </div>
                <div class="role-meta">
                    <span class="role-meta-label">Work Location</span>
                    <span class="role-meta-value">${contract.workLocation}</span>
                </div>
                <c:if test="${not empty contract.fileUrl}">
                    <div class="role-meta">
                        <span class="role-meta-label">File</span>
                        <span class="role-meta-value">
                            <a href="${contract.fileUrl}" target="_blank" rel="noopener">Open file</a>
                        </span>
                    </div>
                </c:if>
<div class="container" style="margin-top: 2rem;">
    <a class="back-link" href="${backUrl}">Back</a>
    <c:if test="${not empty param.error}">
        <div class="alert alert-error">${param.error}</div>
    </c:if>
    <div class="page-header">
        <h2>Contract ${contract.contractCode}</h2>
        <c:if test="${canUpdateContract}">
            <div class="actions">
                <a href="${pageContext.request.contextPath}/contracts/update?id=${contract.id}">Update</a>
            </div>

            <div class="role-detail" style="margin-top: 1.5rem;">
                <div class="role-meta" style="width: 100%;">
                    <span class="role-meta-label">Note</span>
                    <span class="role-meta-value">${contract.note}</span>
                </div>
            </div>

            <c:if test="${canTerminateContract && contract.status != 'TERMINATED'}">
                <form action="${pageContext.request.contextPath}/contracts/terminate" method="post"
                      onsubmit="return confirm('Terminate this contract?')">
                    <input type="hidden" name="id" value="${contract.id}">
                    <div class="form-group" style="margin-top: 1.5rem;">
                        <label for="terminationReason">Termination Reason</label>
                        <textarea id="terminationReason" name="terminationReason"
                                  placeholder="Optional reason for terminating this contract"></textarea>
                    </div>
                    <button type="submit" class="btn btn-danger">Terminate Contract</button>
                </form>
            </c:if>
        </div>
    </main>
</div>
</body>
</html>