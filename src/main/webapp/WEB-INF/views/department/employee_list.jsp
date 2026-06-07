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
<body class="dashboard-body">

<div class="dashboard-wrapper">

    <jsp:include page="/WEB-INF/views/common/sidebar.jsp"/>

    <div class="dashboard-main">
        <div class="dashboard-header">
            <div class="header-left">
                <h1 class="header-title">
                    <c:if test="${not empty department}">
                        ${department.name} -
                    </c:if>
                    Employee List
                </h1>
            </div>
            <div class="header-right">
                <a href="${pageContext.request.contextPath}/admin/departments/detail?id=${param.id}" class="btn-secondary">← Back to Department Detail</a>
            </div>
        </div>

        <div class="dashboard-content">
            <c:if test="${not empty param.error}">
                <div class="alert alert-error">⚠ ${param.error}</div>
            </c:if>

            <c:if test="${not empty param.msg}">
                <div class="alert alert-success">
                    <c:choose>
                        <c:when test="${param.msg == 'unassign_manager_success'}">Manager unassigned successfully.</c:when>
                        <c:otherwise>${param.msg}</c:otherwise>
                    </c:choose>
                </div>
            </c:if>

            <div class="employee-list-toolbar">
                <form action="${pageContext.request.contextPath}/admin/departments/employees" method="get" class="employee-search-form">
                    <input type="hidden" name="id" value="${id}">
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
                    <a href="${pageContext.request.contextPath}/admin/departments/employees?id=${id}" class="btn-reset">Clear</a>
                </form>

                <a href="${pageContext.request.contextPath}/add_member?deptId=${id}" class="employee-add-member">Add Member</a>
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
                            <td>
                                <strong>${user.fullName}</strong>
                                <c:if test="${user.manager}">
                                    <span class="badge badge-manager">Manager</span>
                                </c:if>
                            </td>
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
                                   class="btn-secondary"
                                   <c:if test="${user.manager}">
                                       onclick="alert('Cannot move Manager!'); return false;"
                                   </c:if>>
                                    Move
                                </a>

                                <a href="${pageContext.request.contextPath}/remove_member?userId=${user.id}&deptId=${param.id}"
                                   class="btn-danger"
                                   <c:if test="${user.manager}">
                                       onclick="alert('Cannot remove Manager!'); return false;"
                                   </c:if>
                                   <c:if test="${not user.manager}">
                                       onclick="return confirm('Remove this employee?')"
                                   </c:if>>
                                    Remove
                                </a>

                                <c:if test="${user.manager}">
                                    <form action="${pageContext.request.contextPath}/admin/departments/unassign-manager"
                                          method="post"
                                          class="inline-action-form">
                                        <input type="hidden" name="departmentId" value="${id}">
                                        <input type="hidden" name="userId" value="${user.id}">
                                        <button type="submit"
                                                class="btn btn-warning"
                                                onclick="return confirm('Unassign this manager?')">
                                            Unassign
                                        </button>
                                    </form>
                                </c:if>
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
                <c:url var="firstPageUrl" value="/admin/departments/employees">
                    <c:param name="id" value="${id}" />
                    <c:param name="keyword" value="${keyword}" />
                    <c:param name="status" value="${status}" />
                    <c:param name="sort" value="${sort}" />
                    <c:param name="page" value="1" />
                </c:url>
                <c:url var="previousPageUrl" value="/admin/departments/employees">
                    <c:param name="id" value="${id}" />
                    <c:param name="keyword" value="${keyword}" />
                    <c:param name="status" value="${status}" />
                    <c:param name="sort" value="${sort}" />
                    <c:param name="page" value="${currentPage - 1}" />
                </c:url>
                <c:url var="nextPageUrl" value="/admin/departments/employees">
                    <c:param name="id" value="${id}" />
                    <c:param name="keyword" value="${keyword}" />
                    <c:param name="status" value="${status}" />
                    <c:param name="sort" value="${sort}" />
                    <c:param name="page" value="${currentPage + 1}" />
                </c:url>
                <c:url var="lastPageUrl" value="/admin/departments/employees">
                    <c:param name="id" value="${id}" />
                    <c:param name="keyword" value="${keyword}" />
                    <c:param name="status" value="${status}" />
                    <c:param name="sort" value="${sort}" />
                    <c:param name="page" value="${totalPages}" />
                </c:url>

                <c:choose>
                    <c:when test="${currentPage <= 1 || totalPages <= 1}">
                        <span class="page-link disabled">First</span>
                    </c:when>
                    <c:otherwise>
                        <a href="${firstPageUrl}" class="page-link">First</a>
                    </c:otherwise>
                </c:choose>

                <c:choose>
                    <c:when test="${currentPage <= 1 || totalPages <= 1}">
                        <span class="page-link disabled">Previous</span>
                    </c:when>
                    <c:otherwise>
                        <a href="${previousPageUrl}" class="page-link">Previous</a>
                    </c:otherwise>
                </c:choose>

                <span class="page-link active">
                    <c:choose>
                        <c:when test="${totalPages == 0}">1</c:when>
                        <c:otherwise>${currentPage}</c:otherwise>
                    </c:choose>
                </span>

                <c:choose>
                    <c:when test="${currentPage >= totalPages || totalPages <= 1}">
                        <span class="page-link disabled">Next</span>
                    </c:when>
                    <c:otherwise>
                        <a href="${nextPageUrl}" class="page-link">Next</a>
                    </c:otherwise>
                </c:choose>

                <c:choose>
                    <c:when test="${currentPage >= totalPages || totalPages <= 1}">
                        <span class="page-link disabled">Last</span>
                    </c:when>
                    <c:otherwise>
                        <a href="${lastPageUrl}" class="page-link">Last</a>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
</div>
</body>
</html>
