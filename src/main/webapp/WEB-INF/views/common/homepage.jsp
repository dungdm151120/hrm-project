<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dashboard | HRM</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body>

<jsp:include page="/WEB-INF/views/common/navbar.jsp" />

<c:set var="role" value="${currentUser.roleName}" />
<c:set var="permissions" value="${sessionScope.userPermissions}" />

<div class="dashboard-container">
    <h2 class="dashboard-title">
        <c:choose>
            <c:when test="${not empty role}">${role} Dashboard</c:when>
            <c:otherwise>Dashboard</c:otherwise>
        </c:choose>
    </h2>

    <c:if test="${not empty permissions}">
    <div class="dashboard-grid">
        <c:if test="${permissions.contains('PROFILE_VIEW') or permissions.contains('PROFILE_CHANGE_PASSWORD') or permissions.contains('CONTRACT_VIEW_OWN')}">
            <div class="dashboard-card">
                <h3>Personal</h3>
                <ul>
                    <c:if test="${permissions.contains('PROFILE_VIEW')}">
                        <li><a href="${pageContext.request.contextPath}/profile">View My Profile</a></li>
                    </c:if>
                    <c:if test="${permissions.contains('CONTRACT_VIEW_OWN')}">
                        <li><a href="${pageContext.request.contextPath}/contracts">View My Contract</a></li>
                    </c:if>
                    <c:if test="${permissions.contains('PROFILE_CHANGE_PASSWORD')}">
                        <li><a href="${pageContext.request.contextPath}/change-password">Change Password</a></li>
                    </c:if>
                </ul>
            </div>
        </c:if>

        <c:if test="${permissions.contains('USER_VIEW_LIST') or permissions.contains('USER_CREATE') or permissions.contains('USER_UPDATE') or permissions.contains('USER_TOGGLE_STATUS')}">
            <div class="dashboard-card">
                <h3>User Management</h3>
                <ul>
                    <c:if test="${permissions.contains('USER_VIEW_LIST')}">
                        <li><a href="${pageContext.request.contextPath}/user_list">View User List</a></li>
                    </c:if>
                    <c:if test="${permissions.contains('USER_CREATE')}">
                        <li><a href="${pageContext.request.contextPath}/admin/users/add">Add New User</a></li>
                    </c:if>
                    <c:if test="${permissions.contains('USER_UPDATE')}">
                        <li><a href="${pageContext.request.contextPath}/user_list">Update User Info</a></li>
                        <li><a href="${pageContext.request.contextPath}/admin/password-reset-requests">Password Reset Requests</a></li>
                    </c:if>
                    <c:if test="${permissions.contains('USER_TOGGLE_STATUS')}">
                        <li><a href="${pageContext.request.contextPath}/user_list">Activate/Deactivate User</a></li>
                    </c:if>
                </ul>
            </div>
        </c:if>

        <c:if test="${permissions.contains('ROLE_VIEW_LIST') or permissions.contains('ROLE_VIEW_PERMISSION') or permissions.contains('ROLE_CREATE') or permissions.contains('ROLE_UPDATE') or permissions.contains('ROLE_EDIT_PERMISSION') or permissions.contains('ROLE_TOGGLE_STATUS')}">
            <div class="dashboard-card">
                <h3>Role Management</h3>
                <ul>
                    <c:if test="${permissions.contains('ROLE_VIEW_LIST')}">
                        <li><a href="${pageContext.request.contextPath}/admin/roles">View Role List</a></li>
                    </c:if>
                    <c:if test="${permissions.contains('ROLE_VIEW_PERMISSION')}">
                        <li><a href="${pageContext.request.contextPath}/admin/roles/permissions?roleId=1">View Role Permissions</a></li>
                    </c:if>
                    <c:if test="${permissions.contains('ROLE_CREATE')}">
                        <li><a href="${pageContext.request.contextPath}/admin/roles/add">Add New Role</a></li>
                    </c:if>
                    <c:if test="${permissions.contains('ROLE_UPDATE')}">
                        <li><a href="${pageContext.request.contextPath}/admin/roles/update?roleId=1">Update Role Info</a></li>
                    </c:if>
                    <c:if test="${permissions.contains('ROLE_EDIT_PERMISSION')}">
                        <li><a href="${pageContext.request.contextPath}/admin/roles/edit_permissions?roleId=1">Edit Role Permissions</a></li>
                    </c:if>
                    <c:if test="${permissions.contains('ROLE_TOGGLE_STATUS')}">
                        <li><a href="${pageContext.request.contextPath}/admin/roles">Activate/Deactivate Role</a></li>
                    </c:if>
                </ul>
            </div>
        </c:if>

        <c:if test="${permissions.contains('DEPARTMENT_VIEW_LIST') or permissions.contains('DEPARTMENT_VIEW_DETAIL') or permissions.contains('DEPARTMENT_CREATE') or permissions.contains('DEPARTMENT_UPDATE') or permissions.contains('DEPARTMENT_TOGGLE_STATUS') or permissions.contains('DEPARTMENT_VIEW_EMPLOYEES')}">
            <div class="dashboard-card">
                <h3>Department Management</h3>
                <ul>
                    <c:if test="${permissions.contains('DEPARTMENT_VIEW_LIST')}">
                        <li><a href="${pageContext.request.contextPath}/admin/departments">View Department List</a></li>
                    </c:if>
                    <c:if test="${permissions.contains('DEPARTMENT_CREATE')}">
                        <li><a href="${pageContext.request.contextPath}/admin/departments/add">Add New Department</a></li>
                    </c:if>
                    <c:if test="${permissions.contains('DEPARTMENT_VIEW_DETAIL') and not empty currentUser.departmentId}">
                        <li><a href="${pageContext.request.contextPath}/admin/departments/detail?id=${currentUser.departmentId}">View My Department Detail</a></li>
                    </c:if>
                    <c:if test="${permissions.contains('DEPARTMENT_VIEW_EMPLOYEES') and not empty currentUser.departmentId}">
                        <li><a href="${pageContext.request.contextPath}/admin/departments/employees?id=${currentUser.departmentId}">View My Department Employees</a></li>
                    </c:if>
                    <c:if test="${permissions.contains('DEPARTMENT_UPDATE')}">
                        <li><a href="${pageContext.request.contextPath}/admin/departments">Update Department Info</a></li>
                    </c:if>
                    <c:if test="${permissions.contains('DEPARTMENT_TOGGLE_STATUS')}">
                        <li><a href="${pageContext.request.contextPath}/admin/departments">Activate/Deactivate Department</a></li>
                    </c:if>
                </ul>
            </div>
        </c:if>

        <c:if test="${permissions.contains('POSITION_VIEW_LIST') or permissions.contains('POSITION_CREATE') or permissions.contains('POSITION_UPDATE') or permissions.contains('POSITION_TOGGLE_STATUS')}">
            <div class="dashboard-card">
                <h3>Position Management</h3>
                <ul>
                    <c:if test="${permissions.contains('POSITION_VIEW_LIST')}">
                        <li><a href="${pageContext.request.contextPath}/position/list">View Position List</a></li>
                    </c:if>
                    <c:if test="${permissions.contains('POSITION_CREATE')}">
                        <li><a href="${pageContext.request.contextPath}/position/add">Add New Position</a></li>
                    </c:if>
                    <c:if test="${permissions.contains('POSITION_UPDATE')}">
                        <li><a href="${pageContext.request.contextPath}/position/list">Update Position Info</a></li>
                    </c:if>
                    <c:if test="${permissions.contains('POSITION_TOGGLE_STATUS')}">
                        <li><a href="${pageContext.request.contextPath}/position/list">Activate/Deactivate Position</a></li>
                    </c:if>
                </ul>
            </div>
        </c:if>

        <c:if test="${permissions.contains('CONTRACT_VIEW_LIST') or permissions.contains('CONTRACT_VIEW_OWN') or permissions.contains('CONTRACT_CREATE') or permissions.contains('CONTRACT_UPDATE') or permissions.contains('CONTRACT_TERMINATE')}">
            <div class="dashboard-card">
                <h3>Contract Management</h3>
                <ul>
                    <c:if test="${permissions.contains('CONTRACT_VIEW_LIST') or permissions.contains('CONTRACT_VIEW_OWN')}">
                        <li><a href="${pageContext.request.contextPath}/contracts">View Contracts</a></li>
                    </c:if>
                    <c:if test="${permissions.contains('CONTRACT_CREATE')}">
                        <li><a href="${pageContext.request.contextPath}/contracts/add">Add New Contract</a></li>
                    </c:if>
                    <c:if test="${permissions.contains('CONTRACT_UPDATE')}">
                        <li><a href="${pageContext.request.contextPath}/contracts">Update Contract</a></li>
                    </c:if>
                    <c:if test="${permissions.contains('CONTRACT_TERMINATE')}">
                        <li><a href="${pageContext.request.contextPath}/contracts">Terminate Contract</a></li>
                    </c:if>
                </ul>
            </div>
        </c:if>

        <c:if test="${permissions.contains('ATTENDANCE_VIEW_OWN') or permissions.contains('ATTENDANCE_VIEW_DEPARTMENT') or permissions.contains('ATTENDANCE_VIEW_ALL') or permissions.contains('ATTENDANCE_CHECK_IN') or permissions.contains('ATTENDANCE_CHECK_OUT')}">
            <div class="dashboard-card">
                <h3>Attendance</h3>
                <ul>
                    <c:if test="${permissions.contains('ATTENDANCE_VIEW_OWN')}">
                        <li><span>View My Attendance (Coming Soon)</span></li>
                    </c:if>
                    <c:if test="${permissions.contains('ATTENDANCE_VIEW_DEPARTMENT')}">
                        <li><span>View Department Attendance (Coming Soon)</span></li>
                    </c:if>
                    <c:if test="${permissions.contains('ATTENDANCE_VIEW_ALL')}">
                        <li><span>View All Attendance (Coming Soon)</span></li>
                    </c:if>
                    <c:if test="${permissions.contains('ATTENDANCE_CHECK_IN')}">
                        <li><span>Check In (Coming Soon)</span></li>
                    </c:if>
                    <c:if test="${permissions.contains('ATTENDANCE_CHECK_OUT')}">
                        <li><span>Check Out (Coming Soon)</span></li>
                    </c:if>
                </ul>
            </div>
        </c:if>

        <c:if test="${permissions.contains('PAYROLL_VIEW_OWN') or permissions.contains('PAYROLL_VIEW_LIST') or permissions.contains('PAYROLL_GENERATE') or permissions.contains('PAYROLL_EXPORT_REPORT')}">
            <div class="dashboard-card">
                <h3>Payroll</h3>
                <ul>
                    <c:if test="${permissions.contains('PAYROLL_VIEW_OWN')}">
                        <li><span>View My Payroll (Coming Soon)</span></li>
                    </c:if>
                    <c:if test="${permissions.contains('PAYROLL_VIEW_LIST')}">
                        <li><span>View Payroll List (Coming Soon)</span></li>
                    </c:if>
                    <c:if test="${permissions.contains('PAYROLL_GENERATE')}">
                        <li><span>Generate Payroll (Coming Soon)</span></li>
                    </c:if>
                    <c:if test="${permissions.contains('PAYROLL_EXPORT_REPORT')}">
                        <li><span>Export Payroll Report (Coming Soon)</span></li>
                    </c:if>
                </ul>
            </div>
        </c:if>
    </div>
    </c:if>

    <c:if test="${empty permissions}">
        <div class="role-invalid">
            <p>No permissions found for this account.</p>
        </div>
    </c:if>
</div>

</body>
</html>
