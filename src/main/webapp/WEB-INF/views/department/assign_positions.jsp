<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Phân công vị trí | ${department.name}</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    <style>
        .container { margin-top: 2rem; }
        .alert { padding: 0.75rem 1rem; border-radius: 6px; margin-bottom: 1rem; }
        .alert-success { background-color: #d4edda; color: #155724; border: 1px solid #c3e6cb; }
        .alert-error { background-color: #f8d7da; color: #721c24; border: 1px solid #f5c6cb; }
        table { width: 100%; border-collapse: collapse; background: #fff; box-shadow: 0 2px 8px rgba(0,0,0,0.05); margin-top: 1.5rem; }
        th, td { padding: 0.75rem 1rem; text-align: left; border-bottom: 1px solid #e9ecef; }
        th { background-color: #f8f9fa; font-weight: 600; color: #495057; }
        .current-position { cursor: pointer; text-decoration: underline; color: #0d6efd; }
        .current-position:hover { color: #0a58ca; }
        .text-muted { color: #6c757d; font-style: italic; }
        .btn { padding: 0.25rem 0.5rem; font-size: 0.875rem; border: none; border-radius: 4px; cursor: pointer; }
        .btn-secondary { background: #6c757d; color: white; text-decoration: none; }
        .btn-link { background: none; color: #0d6efd; text-decoration: underline; cursor: pointer; border: none; font-size: 0.875rem; }
        .header-actions { display: flex; gap: 1rem; align-items: center; margin-bottom: 1rem; }
        .inline-form { display: inline; }
        select { padding: 0.25rem; }
        .note {
            background: #e9f5ff;
            padding: 0.75rem;
            border-radius: 6px;
            margin: 1rem 0;
            color: #004085;
            font-size: 0.9rem;
        }
        .key-position-badge {
            opacity: 0.7;
            cursor: not-allowed;
        }
    </style>
</head>
<body>
<jsp:include page="/WEB-INF/views/common/navbar.jsp" />

<div class="container">
    <h2>Phân công vị trí cho phòng ban: <strong>${department.name}</strong></h2>
    <p>Nhấp vào vị trí hiện tại của nhân viên để thay đổi (trừ các vị trí quản lý chủ chốt).</p>

    <%-- Thông báo --%>
    <c:if test="${not empty sessionScope.successMessage}">
        <div class="alert alert-success">${sessionScope.successMessage}</div>
        <% session.removeAttribute("successMessage"); %>
    </c:if>
    <c:if test="${not empty sessionScope.error}">
        <div class="alert alert-error">${sessionScope.error}</div>
        <% session.removeAttribute("error"); %>
    </c:if>

    <c:if test="${empty employees}">
        <p class="text-muted">Không có nhân viên nào đang hoạt động trong phòng ban này.</p>
    </c:if>

    <c:if test="${not empty employees}">

        <table>
            <thead>
                <tr>
                    <th style="width: 50%;">Nhân viên</th>
                    <th>Vị trí hiện tại</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach items="${employees}" var="emp">
                    <c:set var="isKeyPosition" value="${emp.positionName == 'HR Manager' || emp.positionName == 'System Administrator' || emp.positionName == 'Department Manager'}" />
                    <tr>
                        <td>
                            <strong>${emp.fullName}</strong>
                            <c:if test="${not empty emp.email}">
                                <small style="color:#6c757d">(${emp.email})</small>
                            </c:if>
                        </td>
                        <td>
                            <c:choose>
                                <%-- Vị trí chủ chốt: chỉ hiển thị, không cho chỉnh sửa --%>
                                <c:when test="${isKeyPosition}">
                                    <span class="badge badge-secondary key-position-badge" title="Vị trí quản lý chủ chốt, không thể thay đổi tại đây">
                                        ${emp.positionName}
                                    </span>
                                </c:when>
                                <%-- Vị trí thông thường: cho phép click để sửa --%>
                                <c:otherwise>
                                    <span class="current-position" id="pos-display-${emp.id}" onclick="editPosition(${emp.id})">
                                        <c:choose>
                                            <c:when test="${not empty emp.positionName}">
                                                <span class="badge badge-secondary">${emp.positionName}</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="text-muted">Chưa có vị trí</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </span>
                                    <form id="form-${emp.id}" method="post"
                                          action="${pageContext.request.contextPath}/admin/departments/assign-positions"
                                          style="display:none;" class="inline-form">
                                        <input type="hidden" name="userId" value="${emp.id}">
                                        <input type="hidden" name="departmentId" value="${department.id}">
                                        <select name="positionId" onchange="this.form.submit()">
                                            <option value="-1">Không có vị trí</option>
                                            <c:forEach items="${assignablePositions}" var="pos">
                                                <option value="${pos.id}" ${emp.positionId != null && emp.positionId == pos.id ? 'selected' : ''}>${pos.name}</option>
                                            </c:forEach>
                                        </select>
                                        <button type="button" class="btn-link" onclick="cancelEdit(${emp.id})">Hủy</button>
                                    </form>
                                </c:otherwise>
                            </c:choose>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </c:if>

    <div style="margin-top: 1.5rem;">
        <a href="${pageContext.request.contextPath}/admin/departments/detail?id=${department.id}" class="btn btn-secondary">← Quay lại</a>
    </div>
</div>

<script>
    function editPosition(userId) {
        document.getElementById('pos-display-' + userId).style.display = 'none';
        document.getElementById('form-' + userId).style.display = 'inline';
    }
    function cancelEdit(userId) {
        document.getElementById('form-' + userId).style.display = 'none';
        document.getElementById('pos-display-' + userId).style.display = 'inline';
    }
</script>
</body>
</html>