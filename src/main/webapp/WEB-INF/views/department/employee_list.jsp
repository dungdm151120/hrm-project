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

    <div class="employee-list-toolbar">
        <form action="${pageContext.request.contextPath}/admin/departments/employees" method="get" class="employee-search-form">
            <input type="hidden" name="departmentId" value="${departmentId}">
            <input type="hidden" name="page" value="${currentPage}">

            <input type="text" name="keyword" placeholder="Search by name or email..." value="${keyword}">

            <select name="status">
                <option value="all" ${status == 'all' ? 'selected' : ''}>All Status</option>
                <option value="active" ${status == 'active' ? 'selected' : ''}>Active</option>
                <option value="inactive" ${status == 'inactive' ? 'selected' : ''}>Inactive</option>
            </select>

            <select name="sort" onchange="this.form.submit()">
                <option value="name_asc" ${sort == 'name_asc' ? 'selected' : ''}>Name A-Z</option>
                <option value="name_desc" ${sort == 'name_desc' ? 'selected' : ''}>Name Z-A</option>
            </select>

            <button type="submit" class="search-btn">Search</button>
            <a href="${pageContext.request.contextPath}/admin/departments/employees?departmentId=${departmentId}" class="btn-reset">Clear</a>
        </form>

        <a href="${pageContext.request.contextPath}/add_member?deptId=${departmentId}" class="btn btn-primary employee-add-member">Add Member</a>
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
            <c:forEach items="${employees}" var="user" varStatus="s">
                <tr>
                    <td>${(currentPage - 1) * pageSize + s.index + 1}</td>
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
                        <a href="${pageContext.request.contextPath}/move_member?userId=${user.id}&currentDeptId=${departmentId}"
                           class="btn-move"
                                <c:if test="${user.manager}">
                                    onclick="return confirmManagerMove();"
                                </c:if>>
                            Move
                        </a>

                        <a href="${pageContext.request.contextPath}/remove_member?userId=${user.id}&deptId=${departmentId}"
                           class="btn-danger"
                           onclick="return confirm('Bạn chắc chắn muốn remove employee này khỏi phòng ban?')">Remove</a>
                    </td>
                </tr>
            </c:forEach>
            <c:if test="${empty employees}">
                <tr>
                    <td colspan="8" class="empty-state">No employees found in this department.</td>
                </tr>
            </c:if>
            </tbody>
        </table>
    </div>

    <div class="pagination employee-pagination">
        <span class="page-link active">
            ${totalPages == 0 ? 1 : currentPage}/${totalPages == 0 ? 1 : totalPages}
        </span>

        <c:if test="${totalPages > 1}">
            <c:choose>
                <c:when test="${currentPage <= 1}">
                    <span class="page-link disabled">Previous</span>
                </c:when>
                <c:otherwise>
                    <c:url var="previousPageUrl" value="/admin/departments/employees">
                        <c:param name="departmentId" value="${departmentId}" />
                        <c:param name="keyword" value="${keyword}" />
                        <c:param name="status" value="${status}" />
                        <c:param name="sort" value="${sort}" />
                        <c:param name="page" value="${currentPage - 1}" />
                    </c:url>
                    <a href="${previousPageUrl}" class="page-link">Previous</a>
                </c:otherwise>
            </c:choose>

            <c:forEach begin="1" end="${totalPages}" var="pageNumber">
                <c:url var="pageUrl" value="/admin/departments/employees">
                    <c:param name="departmentId" value="${departmentId}" />
                    <c:param name="keyword" value="${keyword}" />
                    <c:param name="status" value="${status}" />
                    <c:param name="sort" value="${sort}" />
                    <c:param name="page" value="${pageNumber}" />
                </c:url>
                <a href="${pageUrl}" class="page-link ${currentPage == pageNumber ? 'active' : ''}">${pageNumber}</a>
            </c:forEach>

            <c:choose>
                <c:when test="${currentPage >= totalPages}">
                    <span class="page-link disabled">Next</span>
                </c:when>
                <c:otherwise>
                    <c:url var="nextPageUrl" value="/admin/departments/employees">
                        <c:param name="departmentId" value="${departmentId}" />
                        <c:param name="keyword" value="${keyword}" />
                        <c:param name="status" value="${status}" />
                        <c:param name="sort" value="${sort}" />
                        <c:param name="page" value="${currentPage + 1}" />
                    </c:url>
                    <a href="${nextPageUrl}" class="page-link">Next</a>
                </c:otherwise>
            </c:choose>
        </c:if>
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
