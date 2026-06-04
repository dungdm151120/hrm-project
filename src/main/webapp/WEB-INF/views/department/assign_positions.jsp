<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Assign Positions | ${department.name} | HRM</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body class="dashboard-body">

<div class="dashboard-wrapper">

    <jsp:include page="/WEB-INF/views/common/sidebar.jsp"/>

    <div class="dashboard-main">
        <div class="dashboard-header">
            <div class="header-left">
                <h1 class="header-title">Assign Positions</h1>
            </div>
            <div class="header-right">
                <a href="${pageContext.request.contextPath}/admin/departments/detail?id=${department.id}" class="btn-secondary">← Back to Department</a>
            </div>
        </div>

        <div class="dashboard-content">
            <div class="form-title">Department: <strong>${department.name}</strong></div>
            <p class="role-description">Click on an employee's current position to change it (except key management positions).</p>

            <c:if test="${not empty sessionScope.successMessage}">
                <div class="alert alert-success">${sessionScope.successMessage}</div>
                <% session.removeAttribute("successMessage"); %>
            </c:if>
            <c:if test="${not empty sessionScope.error}">
                <div class="alert alert-error">${sessionScope.error}</div>
                <% session.removeAttribute("error"); %>
            </c:if>

            <c:if test="${empty employees}">
                <div class="empty-state">No active employees found in this department.</div>
            </c:if>

            <c:if test="${not empty employees}">
                <div class="table-wrapper">
                    <table class="assign-positions-table">
                        <thead>
                            <tr>
                                <th>Employee</th>
                                <th>Current Position</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach items="${employees}" var="emp">
                                <c:set var="isKeyPosition" value="${emp.positionName == 'HR Manager' || emp.positionName == 'System Administrator' || emp.positionName == 'Department Manager'}" />
                                <tr>
                                    <td>
                                        <strong>${emp.fullName}</strong>
                                        <c:if test="${not empty emp.email}">
                                            <small class="text-muted">(${emp.email})</small>
                                        </c:if>
                                    </td>
                                    <td class="position-cell">
                                        <c:choose>
                                            <c:when test="${isKeyPosition}">
                                                <span class="badge badge-manager key-position-badge" title="Key management position, cannot be changed here">
                                                    ${emp.positionName}
                                                </span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="current-position" id="pos-display-${emp.id}" onclick="editPosition(${emp.id})">
                                                    <c:choose>
                                                        <c:when test="${not empty emp.positionName}">
                                                            <span class="badge badge-manager">${emp.positionName}</span>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <span class="text-muted">No position</span>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </span>
                                                <form id="form-${emp.id}" method="post"
                                                      action="${pageContext.request.contextPath}/admin/departments/assign-positions"
                                                      class="inline-edit-form hidden">
                                                    <input type="hidden" name="userId" value="${emp.id}">
                                                    <input type="hidden" name="departmentId" value="${department.id}">
                                                    <select name="positionId" onchange="this.form.submit()">
                                                        <option value="-1">No position</option>
                                                        <c:forEach items="${assignablePositions}" var="pos">
                                                            <option value="${pos.id}" ${emp.positionId != null && emp.positionId == pos.id ? 'selected' : ''}>${pos.name}</option>
                                                        </c:forEach>
                                                    </select>
                                                    <button type="button" class="btn-link" onclick="cancelEdit(${emp.id})">Cancel</button>
                                                </form>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:if>
        </div>
    </div>
</div>

<script>
    function editPosition(userId) {
        document.getElementById('pos-display-' + userId).classList.add('hidden');
        document.getElementById('form-' + userId).classList.remove('hidden');
    }
    function cancelEdit(userId) {
        document.getElementById('form-' + userId).classList.add('hidden');
        document.getElementById('pos-display-' + userId).classList.remove('hidden');
    }
</script>

</body>
</html>