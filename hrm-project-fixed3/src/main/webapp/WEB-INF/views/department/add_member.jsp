<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Add Member | HRM</title>
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
            <h1 class="header-title">Add Member to Department</h1>
            <div class="header-right">
                <a href="${pageContext.request.contextPath}/admin/departments/employees?id=${deptId}" class="btn btn-secondary">← Back</a>
            </div>
        </header>

        <div class="dashboard-content">
            <form action="${pageContext.request.contextPath}/add_member" method="POST">
                <input type="hidden" name="deptId" value="${deptId}">

                <div class="toolbar">
                    <button type="button" class="btn-secondary" onclick="selectAll()">Select All</button>
                    <button type="button" class="btn-secondary" onclick="clearAll()">Deselect All</button>
                    <span class="selected-count-wrapper">
                        Selected: <span id="selectedCount" class="selected-count">0</span> employees
                    </span>
                </div>

                <div class="table-wrapper">
                    <table>
                        <thead>
                        <tr>
                            <th style="width: 60px; text-align: center;">Select</th>
                            <th>Employee Name</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach items="${unassignedUsers}" var="u">
                            <tr>
                                <td style="text-align: center;">
                                    <input type="checkbox" name="userIds" value="${u.id}" class="perm-checkbox">
                                </td>
                                <td><strong>${u.fullName}</strong></td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty unassignedUsers}">
                            <tr>
                                <td colspan="2" class="empty-state">No available employees.</td>
                            </tr>
                        </c:if>
                        </tbody>
                    </table>
                </div>

                <div class="form-actions">
                    <button type="submit" class="btn-save">Add Selected</button>
                    <a href="${pageContext.request.contextPath}/admin/departments/employees?id=${deptId}" class="btn-cancel">Cancel</a>
                </div>
            </form>
        </div>
    </main>
</div>

<script>
    function updateCount() {
        var checked = document.querySelectorAll('.perm-checkbox:checked').length;
        document.getElementById('selectedCount').textContent = checked;
    }
    function selectAll() {
        document.querySelectorAll('.perm-checkbox').forEach(function(cb) { cb.checked = true; });
        updateCount();
    }
    function clearAll() {
        document.querySelectorAll('.perm-checkbox').forEach(function(cb) { cb.checked = false; });
        updateCount();
    }
    document.querySelectorAll('.perm-checkbox').forEach(function(cb) {
        cb.addEventListener('change', updateCount);
    });
    updateCount();
</script>

</body>
</html>