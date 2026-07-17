<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
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
                    <span class="role-meta-value">${contract.startDateDisplay}</span>
                </div>
                <div class="role-meta">
                    <span class="role-meta-label">End Date</span>
                    <span class="role-meta-value">
                        <c:choose>
                            <c:when test="${not empty contract.endDate}">${contract.endDateDisplay}</c:when>
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
            </div>

            <div class="role-detail">
                <div class="role-meta" style="width: 100%;">
                    <span class="role-meta-label">Note</span>
                    <span class="role-meta-value">${contract.note}</span>
                </div>
            </div>

            <c:if test="${contract.status == 'TERMINATED'}">
                <div class="role-detail">
                    <div class="role-meta">
                        <span class="role-meta-label">Terminated At</span>
                        <span class="role-meta-value">${contract.terminatedAtDisplay}</span>
                    </div>
                    <div class="role-meta">
                        <span class="role-meta-label">Terminated By</span>
                        <span class="role-meta-value">
                            <c:choose>
                                <c:when test="${not empty contract.terminatedByName}">${contract.terminatedByName}</c:when>
                                <c:otherwise>-</c:otherwise>
                            </c:choose>
                        </span>
                    </div>
                    <div class="role-meta" style="width: 100%;">
                        <span class="role-meta-label">Termination Reason</span>
                        <span class="role-meta-value">
                            <c:choose>
                                <c:when test="${not empty contract.terminationReason}">${contract.terminationReason}</c:when>
                                <c:otherwise>-</c:otherwise>
                            </c:choose>
                        </span>
                    </div>
                </div>
            </c:if>

            <c:if test="${canTerminateContract && contract.status == 'ACTIVE'}">
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

            <div class="table-wrapper" style="margin-top: 24px;">
                <h2>Contract Change History</h2>
                <table>
                    <thead>
                    <tr>
                        <th>Time</th>
                        <th>Action</th>
                        <th>Field</th>
                        <th>Old Value</th>
                        <th>New Value</th>
                        <th>Changed By</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach items="${changeLogs}" var="log">
                        <tr>
                            <td><fmt:formatDate value="${log.changedAt}" pattern="dd/MM/yyyy HH:mm"/></td>
                            <td><c:out value="${log.action}"/></td>
                            <td><c:out value="${log.fieldName}"/></td>
                            <td>
                                <c:choose>
                                    <c:when test="${not empty log.oldValue}"><c:out value="${log.oldValue}"/></c:when>
                                    <c:otherwise>-</c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                <c:choose>
                                    <c:when test="${not empty log.newValue}"><c:out value="${log.newValue}"/></c:when>
                                    <c:otherwise>-</c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                <c:choose>
                                    <c:when test="${not empty log.changedByName}"><c:out value="${log.changedByName}"/></c:when>
                                    <c:otherwise>System</c:otherwise>
                                </c:choose>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty changeLogs}">
                        <tr>
                            <td colspan="6" class="empty-state">No contract changes recorded yet.</td>
                        </tr>
                    </c:if>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>

</body>
</html>
