<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Department Employees | HRM</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body>

<jsp:include page="/WEB-INF/views/common/navbar.jsp" />

<div class="container" style="margin-top: 2rem;">
    <div class="page-header">
        <h2>
            <c:if test="${not empty department}">
                ${department.name} -
            </c:if>
            Employee List

        </h2>
        <a href="${pageContext.request.contextPath}/admin/departments/detail?id=${param.id}" class="btn btn-secondary">← Back to Department Detail</a>
    </div>

    <c:if test="${not empty param.error}">
        <div class="alert alert-error" style="margin-bottom: 1rem;">
            ⚠ ${param.error}
        </div>
    </c:if>

    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.5rem;">
        <form action="${pageContext.request.contextPath}/admin/departments/employees" method="get" style="display: flex; align-items: center; gap: 0.5rem; margin: 0;">
            <input type="hidden" name="id" value="${param.id}">

            <input type="text" name="search" placeholder="Search by name or email..." value="${search}">

            <button type="submit" class="search-btn">Search</button>

            <c:if test="${not empty search}">
                <a href="${pageContext.request.contextPath}/admin/departments/employees?id=${param.id}" class="btn-reset">Clear</a>
            </c:if>
        </form>

        <a href="${pageContext.request.contextPath}/add_member?deptId=${param.id}" class="btn btn-primary" style="height: 36px; padding: 0 1.25rem; display: inline-flex; align-items: center; margin: 0; box-sizing: border-box;">Add Member</a>
    </div>

    <div class="table-wrapper">
        <table>
            <thead>
            <tr>
                <th>No.</th>
                <th>Full Name</th>
                <th>Email</th>
                <th>Phone</th>
                <th>Position</th>
                <th>Department</th>
                <th>Status</th>
                <th>Action</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach items="${userList}" var="user" varStatus="s">
                <tr>
                    <td>${s.index + 1}</td>
                    <td><strong>${user.fullName}</strong></td>
                    <td>${user.email}</td>
                    <td>
                        <c:choose>
                            <c:when test="${not empty user.phone}">
                                ${user.phone}
                            </c:when>
                            <c:otherwise>
                                <span class="text-muted">No phone</span>
                            </c:otherwise>
                        </c:choose>
                    </td>
                    <td>
                        <c:choose>
                            <c:when test="${not empty user.positionName}">
                                ${user.positionName}
                            </c:when>
                            <c:otherwise>
                                <span class="text-muted">No position</span>
                            </c:otherwise>
                        </c:choose>
                    </td>
                    <td>${user.departmentName}</td>
                    <td>
                        <c:choose>
                            <c:when test="${user.active}">
                                <span class="badge badge-active">Active</span>
                            </c:when>
                            <c:otherwise>
                                <span class="badge badge-inactive">Inactive</span>
                            </c:otherwise>
                        </c:choose>
                    </td>

                    <td class="actions">
                        <a href="${pageContext.request.contextPath}/move_member?userId=${user.id}&currentDeptId=${param.id}"
                           class="btn-move"
                                <c:if test="${user.manager}">
                                    onclick="return confirmManagerMove();"
                                </c:if>>
                            Move
                        </a>

                        <a href="${pageContext.request.contextPath}/remove_member?userId=${user.id}&deptId=${param.id}"
                           class="btn-danger"
                           onclick="return confirm('Bạn chắc chắn muốn remove employee này khỏi phòng ban?')">Remove</a>
                    </td>
                </tr>
            </c:forEach>
            <c:if test="${empty userList}">
                <tr>
                    <td colspan="8" class="empty-state">No employees found in this department.</td>
                </tr>
            </c:if>
            </tbody>
        </table>
    </div>
</div>
<script>
    function confirmManagerMove() {
        alert("Không thể chuyển phòng ban vì nhân viên này đang là Manager!");
        return false;
    }
</script>
</body>
</html>
