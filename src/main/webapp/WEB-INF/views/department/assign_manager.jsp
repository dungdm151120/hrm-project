<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Assign Department Manager | HRM</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    <style>
        .container { margin-top: 2rem; }
        .employee-list { margin: 1.5rem 0; }
        .employee-item {
            display: flex;
            align-items: center;
            padding: 0.75rem;
            border: 1px solid #dee2e6;
            border-radius: 6px;
            margin-bottom: 0.5rem;
            cursor: pointer;
            transition: background-color 0.2s;
        }
        .employee-item:hover { background-color: #f8f9fa; }
        .employee-item input[type="radio"] { margin-right: 1rem; }
        .employee-info { flex: 1; }
        .btn-submit { margin-top: 1.5rem; }
        .empty-message { color: #6c757d; font-style: italic; }
        .note {
            background: #e9f5ff;
            padding: 0.75rem;
            border-radius: 6px;
            margin: 1rem 0;
            color: #004085;
        }
        .alert-error {
            background-color: #f8d7da;
            color: #721c24;
            padding: 0.75rem;
            border-radius: 6px;
            margin-bottom: 1rem;
        }
    </style>
</head>
<body>
<jsp:include page="/WEB-INF/views/common/navbar.jsp" />

<div class="container">
    <h2>Assign Manager for Department: <strong>${department.name}</strong></h2>
    <p>Select an active employee to become the department manager.</p>

    <c:if test="${not empty sessionScope.error}">
        <div class="alert alert-error">
            ${sessionScope.error}
        </div>
        <% session.removeAttribute("error"); %>
    </c:if>

    <form action="${pageContext.request.contextPath}/admin/departments/assign-manager" method="post">
        <input type="hidden" name="departmentId" value="${department.id}" />

        <c:if test="${empty employees}">
            <p class="empty-message">No active employees found in this department.</p>
        </c:if>

        <c:if test="${not empty employees}">
            <div class="employee-list">
                <c:forEach var="emp" items="${employees}">
                    <label class="employee-item">
                        <input type="radio" name="userId" value="${emp.id}" required />
                        <div class="employee-info">
                            <strong>${emp.fullName}</strong> (${emp.email})
                            <c:if test="${not empty emp.positionName}">
                                – <span class="badge badge-secondary">${emp.positionName}</span>
                            </c:if>
                        </div>
                    </label>
                </c:forEach>
            </div>

            <div class="note">
                <small>
                    <strong>Note:</strong> If this is the <em>Human Resources</em> department,
                    the selected employee will become <strong>HR Manager</strong>.
                    For other departments, the position will be <strong>Department Manager</strong>.
                </small>
            </div>

            <button type="submit" class="btn btn-success" onclick="return confirm('Assign this employee as department manager?')">
                Assign Manager
            </button>
        </c:if>

        <a href="${pageContext.request.contextPath}/admin/departments/detail?id=${department.id}" class="btn btn-secondary" style="margin-left: 1rem;">Cancel</a>
    </form>
</div>
</body>
</html>