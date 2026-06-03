<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Move Member | HRM</title>
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
            <h1 class="header-title">Move Member</h1>
            <div class="header-right">
                <a href="${pageContext.request.contextPath}/admin/departments/employees?id=${user.departmentId}" class="btn btn-secondary">← Back</a>
            </div>
        </header>

        <div class="dashboard-content">
            <div class="role-detail" style="margin-bottom: 1.5rem;">
                <div class="role-meta">
                    <span class="role-meta-label">Employee</span>
                    <span class="role-meta-value"><strong>${user.fullName}</strong></span>
                </div>
            </div>

            <div class="form-wrapper">
                <form action="${pageContext.request.contextPath}/move_member" method="POST">
                    <input type="hidden" name="userId" value="${user.id}">
                    <input type="hidden" name="currentDeptId" value="${user.departmentId}">

                    <div class="form-group">
                        <label>Select New Department</label>
                        <select name="newDeptId">
                            <c:forEach items="${deptList}" var="d">
                                <option value="${d.id}">${d.name}</option>
                            </c:forEach>
                        </select>
                    </div>

                    <div class="form-actions">
                        <button type="submit" class="btn-save">Confirm Move</button>
                        <a href="${pageContext.request.contextPath}/admin/departments/employees?id=${user.departmentId}" class="btn-cancel">Cancel</a>
                    </div>
                </form>
            </div>
        </div>
    </main>
</div>
</body>
</html>