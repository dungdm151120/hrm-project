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

<div class="dashboard-container">

    <%-- ========== ADMIN ========== --%>
    <c:if test="${role eq 'ADMIN'}">
        <h2 class="dashboard-title">Admin Dashboard</h2>
        <div class="dashboard-grid">
            <div class="dashboard-card">
                <h3>👤 Personal</h3>
                <ul>
                    <li><a href="${pageContext.request.contextPath}/profile">View My Profile</a></li>
                    <li><a href="${pageContext.request.contextPath}/change-password">Change Password</a></li>
                </ul>
            </div>
            <div class="dashboard-card">
                <h3>👥 User Management</h3>
                <ul>
                    <li><a href="${pageContext.request.contextPath}/user_list">View User List</a></li>
                    <li><a href="${pageContext.request.contextPath}/admin/users/add">Add New User</a></li>
                    <li><a href="${pageContext.request.contextPath}/user_list">Update User Info</a></li>
                    <li><a href="${pageContext.request.contextPath}/user_list">Active/Deactive User</a></li>
                    <li><a href="${pageContext.request.contextPath}/admin/password-reset-requests">Request Reset Password</a></li>
                </ul>
            </div>
            <div class="dashboard-card">
                <h3>🛡️ Role Management</h3>
                <ul>
                    <li><a href="${pageContext.request.contextPath}/admin/roles">View Role List</a></li>
                    <li><a href="${pageContext.request.contextPath}/admin/roles/permissions?roleId=1">View Role Permissions</a></li>
                    <li><a href="${pageContext.request.contextPath}/admin/roles/update?roleId=1">Update Role Info</a></li>
                    <li><a href="${pageContext.request.contextPath}/admin/roles/add">Add New Role</a></li>
                    <li><a href="${pageContext.request.contextPath}/admin/roles/edit_permissions?roleId=1">Edit Role Permissions</a></li>
                </ul>
            </div>
            <div class="dashboard-card">
                <h3>🏢 Department Management</h3>
                <ul>
                    <li><a href="${pageContext.request.contextPath}/admin/departments">View Department List</a></li>
                    <li><a href="${pageContext.request.contextPath}/admin/departments/add">Add New Department</a></li>
                </ul>
            </div>
            <div class="dashboard-card">
                <h3>Contract</h3>
                <ul>
                    <li><a href="${pageContext.request.contextPath}/my-contract">View My Contract</a></li>
                </ul>
            </div>
            <div class="dashboard-card">
                            <h3>Position Management</h3>
                            <ul>
                                <li><a href="${pageContext.request.contextPath}/position/list">View Position List</a></li>
                                <li><a href="${pageContext.request.contextPath}/position/add">Add New Position</a></li>
                                <li><a href="${pageContext.request.contextPath}/position/list">Update Position Info</a></li>

                            </ul>
                        </div>
        </div>
    </c:if>

    <%-- ========== HR MANAGER ========== --%>
    <c:if test="${role eq 'HR_MANAGER'}">
        <h2 class="dashboard-title">HR Manager Dashboard</h2>
        <div class="dashboard-grid">
            <div class="dashboard-card">
                <h3>👤 Personal</h3>
                <ul>
                    <li><a href="${pageContext.request.contextPath}/profile">View My Profile</a></li>
                    <li><a href="${pageContext.request.contextPath}/change-password">Change Password</a></li>
                </ul>
            </div>
            <div class="dashboard-card">
                <h3>👥 User Management</h3>
                <ul>
                    <li><a href="${pageContext.request.contextPath}/user_list">View User List</a></li>
                    <li><a href="${pageContext.request.contextPath}/admin/users/add">Add New User</a></li>
                    <li><a href="${pageContext.request.contextPath}/user_list">Update User Info</a></li>
                    <li><a href="${pageContext.request.contextPath}/user_list">Active/Deactive User</a></li>
                </ul>
            </div>
            <div class="dashboard-card">
                <h3>🏢 Department Management</h3>
                <ul>
                    <li><a href="${pageContext.request.contextPath}/admin/departments">View Department List</a></li>
                    <li><a href="${pageContext.request.contextPath}/admin/departments/add">Add New Department</a></li>
                </ul>
            </div>
            <div class="dashboard-card">
                <h3>Contract</h3>
                <ul>
                    <li><a href="${pageContext.request.contextPath}/contracts">View Contract List</a></li>
                </ul>
            </div>
            <!-- Các module khác như Position, Attendance, Payroll có thể thêm sau khi phát triển servlet -->
        </div>
    </c:if>

    <%-- ========== HR STAFF ========== --%>
    <c:if test="${role eq 'HR_STAFF'}">
        <h2 class="dashboard-title">HR Staff Dashboard</h2>
        <div class="dashboard-grid">
            <div class="dashboard-card">
                <h3>👤 Personal</h3>
                <ul>
                    <li><a href="${pageContext.request.contextPath}/profile">View My Profile</a></li>
                    <li><a href="${pageContext.request.contextPath}/change-password">Change Password</a></li>
                </ul>
            </div>
            <div class="dashboard-card">
                <h3>👥 User Management</h3>
                <ul>
                    <li><a href="${pageContext.request.contextPath}/user_list">View User List</a></li>
                </ul>
            </div>
            <div class="dashboard-card">
                <h3>🏢 Department Management</h3>
                <ul>
                    <li><a href="${pageContext.request.contextPath}/admin/departments">View Department List</a></li>
                </ul>
            </div>
            <div class="dashboard-card">
                <h3>Contract</h3>
                <ul>
                    <li><a href="${pageContext.request.contextPath}/contracts">View Contract List</a></li>
                </ul>
            </div>
        </div>
    </c:if>

    <%-- ========== DEPARTMENT MANAGER ========== --%>
    <c:if test="${role eq 'DEPARTMENT_MANAGER'}">
        <h2 class="dashboard-title">Department Manager Dashboard</h2>
        <div class="dashboard-grid">
            <div class="dashboard-card">
                <h3>👤 Personal</h3>
                <ul>
                    <li><a href="${pageContext.request.contextPath}/profile">View My Profile</a></li>
                    <li><a href="${pageContext.request.contextPath}/change-password">Change Password</a></li>
                </ul>
            </div>
            <div class="dashboard-card">
                <h3>Contract</h3>
                <ul>
                    <li><a href="${pageContext.request.contextPath}/my-contract">View My Contract</a></li>
                </ul>
            </div>
            <c:if test="${not empty currentUser.departmentId}">
                <div class="dashboard-card">
                    <h3>🏢 My Department</h3>
                    <ul>
                        <li><a href="${pageContext.request.contextPath}/admin/departments/detail?id=${currentUser.departmentId}">View Department Detail</a></li>
                        <!-- Sau này có thể thêm View Employees, Attendance Report... -->
                    </ul>
                </div>
            </c:if>
            <!-- Self-service: Attendance, Contract, Payroll (cá nhân) sẽ bổ sung khi có servlet -->
        </div>
    </c:if>

    <%-- ========== PAYROLL STAFF ========== --%>
    <c:if test="${role eq 'PAYROLL_STAFF'}">
        <h2 class="dashboard-title">Payroll Staff Dashboard</h2>
        <div class="dashboard-grid">
            <div class="dashboard-card">
                <h3>👤 Personal</h3>
                <ul>
                    <li><a href="${pageContext.request.contextPath}/profile">View My Profile</a></li>
                    <li><a href="${pageContext.request.contextPath}/change-password">Change Password</a></li>
                </ul>
            </div>
            <div class="dashboard-card">
                <h3>Contract</h3>
                <ul>
                    <li><a href="${pageContext.request.contextPath}/my-contract">View My Contract</a></li>
                </ul>
            </div>
            <!-- Module Payroll sẽ xuất hiện sau khi có servlet -->
            <div class="dashboard-card">
                <h3>💰 Payroll (Coming Soon)</h3>
                <ul>
                    <li><span>Generate Payroll</span></li>
                    <li><span>View Payroll List</span></li>
                </ul>
            </div>
        </div>
    </c:if>

    <%-- ========== EMPLOYEE ========== --%>
    <c:if test="${role eq 'EMPLOYEE'}">
        <h2 class="dashboard-title">Employee Dashboard</h2>
        <div class="dashboard-grid">
            <div class="dashboard-card">
                <h3>👤 Personal</h3>
                <ul>
                    <li><a href="${pageContext.request.contextPath}/profile">View My Profile</a></li>
                    <li><a href="${pageContext.request.contextPath}/change-password">Change Password</a></li>
                </ul>
            </div>
            <div class="dashboard-card">
                <h3>Contract</h3>
                <ul>
                    <li><a href="${pageContext.request.contextPath}/my-contract">View My Contract</a></li>
                </ul>
            </div>
            <!-- Self-service: Attendance, Payroll (cá nhân) sẽ bổ sung sau -->
        </div>
    </c:if>

    <%-- Nếu role không khớp --%>
    <c:if test="${empty role or (role ne 'ADMIN' and role ne 'HR_MANAGER' and role ne 'HR_STAFF' and role ne 'DEPARTMENT_MANAGER' and role ne 'PAYROLL_STAFF' and role ne 'EMPLOYEE')}">
        <div class="role-invalid">
            <p>⚠ Role không hợp lệ hoặc chưa được hỗ trợ.</p>
        </div>
    </c:if>

</div>

</body>
</html>
