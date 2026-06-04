<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Assign Department Manager | HRM</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body class="dashboard-body">

<div class="dashboard-wrapper">

    <jsp:include page="/WEB-INF/views/common/sidebar.jsp"/>

    <div class="dashboard-main">
        <div class="dashboard-header">
            <div class="header-left">
                <h1 class="header-title">Assign Department Manager</h1>
            </div>
            <div class="header-right">
                <a href="${pageContext.request.contextPath}/admin/departments/detail?id=${department.id}" class="btn-secondary">← Back to Department</a>
            </div>
        </div>

        <div class="dashboard-content">
            <h2 class="form-title">Department: <strong>${department.name}</strong></h2>
            <p class="role-description">Select an active employee to become the department manager.</p>

            <c:if test="${not empty sessionScope.error}">
                <div class="alert alert-error">${sessionScope.error}</div>
                <% session.removeAttribute("error"); %>
            </c:if>

            <form action="${pageContext.request.contextPath}/admin/departments/assign-manager" method="post">
                <input type="hidden" name="departmentId" value="${department.id}" />

                <c:if test="${empty employees}">
                    <div class="empty-state">No active employees found in this department.</div>
                </c:if>

                <c:if test="${not empty employees}">
                    <div class="employee-select-list">
                        <c:forEach var="emp" items="${employees}">
                            <label class="employee-select-item">
                                <input type="radio" name="userId" value="${emp.id}" required />
                                <div class="employee-select-info">
                                    <strong>${emp.fullName}</strong> (${emp.email})
                                    <c:if test="${not empty emp.positionName}">
                                        <span class="badge badge-manager">${emp.positionName}</span>
                                    </c:if>
                                </div>
                            </label>
                        </c:forEach>
                    </div>

                    <div class="info-note">
                        <strong>Note:</strong> If this is the <em>Human Resources</em> department,
                        the selected employee will become <strong>HR Manager</strong>.
                        For other departments, the position will be <strong>Department Manager</strong>.
                    </div>

                    <div class="form-actions">
                        <button type="submit" class="btn-save" onclick="return confirm('Assign this employee as department manager?')">Assign Manager</button>
                        <a href="${pageContext.request.contextPath}/admin/departments/detail?id=${department.id}" class="btn-cancel">Cancel</a>
                    </div>
                </c:if>
            </form>
        </div>
    </div>
</div>

</body>
</html>