<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Contract Detail | HRM</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body class="dashboard-body">

<div class="dashboard-wrapper">

    <jsp:include page="/WEB-INF/views/common/sidebar.jsp"/>

    <div class="dashboard-main">
        <div class="dashboard-header">
            <div class="header-left">
                <h1 class="header-title">Contract ${contract.contractCode}</h1>
            </div>
            <div class="header-right">
                <c:if test="${canUpdateContract}">
                    <a href="${pageContext.request.contextPath}/contracts/update?id=${contract.id}" class="btn-primary">Update</a>
                </c:if>
            </div>
        </div>

        <div class="dashboard-content">
            <a class="back-link" href="${backUrl}">Back</a>

            <c:if test="${not empty param.error}">
                <div class="alert alert-error">${param.error}</div>
            </c:if>

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
            </div>

            <div class="role-detail">
                <div class="role-meta" style="width: 100%;">
                    <span class="role-meta-label">Note</span>
                    <span class="role-meta-value">${contract.note}</span>
                </div>
            </div>

            <c:if test="${canTerminateContract && contract.status != 'TERMINATED'}">
                <form action="${pageContext.request.contextPath}/contracts/terminate" method="post"
                      onsubmit="return confirm('Terminate this contract?')">
                    <input type="hidden" name="id" value="${contract.id}">
                    <div class="form-group">
                        <label for="terminationReason">Termination Reason</label>
                        <textarea id="terminationReason" name="terminationReason"
                                  placeholder="Optional reason for terminating this contract"></textarea>
                    </div>
                    <button type="submit" class="btn btn-danger">Terminate Contract</button>
                </form>
            </c:if>
        </div>
    </div>
</div>

</body>
</html>