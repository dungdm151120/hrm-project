<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:set var="currentPath" value="${requestScope['jakarta.servlet.forward.request_uri']}" />
<c:if test="${empty currentPath}">
  <c:set var="currentPath" value="${pageContext.request.requestURI}" />
</c:if>
<c:set var="ctx" value="${pageContext.request.contextPath}" />

<%
  java.util.Set<String> userPermissions = (java.util.Set<String>) session.getAttribute("userPermissions");
  pageContext.setAttribute("userPermissions", userPermissions);

  // Lấy roleId từ session (giả sử currentUser có getRoleId())
  model.User currentUser = (model.User) session.getAttribute("currentUser");
  boolean isAdmin = (currentUser != null && currentUser.getRoleId() == 1);
  pageContext.setAttribute("isAdmin", isAdmin);
%>

<c:set var="showEmployees"   value="${userPermissions.contains('USER_VIEW_LIST') or userPermissions.contains('USER_CREATE')}" />
<c:set var="showDepartments" value="${userPermissions.contains('DEPARTMENT_VIEW_LIST') or userPermissions.contains('DEPARTMENT_CREATE')}" />
<c:set var="showPositions"   value="${userPermissions.contains('POSITION_VIEW_LIST') or userPermissions.contains('POSITION_CREATE')}" />
<c:set var="showRoles"       value="${userPermissions.contains('ROLE_VIEW_LIST') or userPermissions.contains('ROLE_CREATE')}" />
<c:set var="showContracts"   value="${userPermissions.contains('CONTRACT_VIEW_LIST') or userPermissions.contains('CONTRACT_VIEW_OWN') or userPermissions.contains('CONTRACT_CREATE')}" />
<c:set var="showAttendance"  value="${userPermissions.contains('ATTENDANCE_VIEW_OWN') or userPermissions.contains('ATTENDANCE_VIEW_DEPARTMENT') or userPermissions.contains('ATTENDANCE_VIEW_ALL') or userPermissions.contains('ATTENDANCE_UPDATE') or userPermissions.contains('ATTENDANCE_EXPORT_REPORT')}" />
<c:set var="showPayroll"     value="${userPermissions.contains('PAYROLL_VIEW_OWN') or userPermissions.contains('PAYROLL_VIEW_LIST') or userPermissions.contains('PAYROLL_GENERATE') or userPermissions.contains('PAYROLL_EXPORT_REPORT')}" />

<style>
  .submenu {
    padding-left: 48px;
  }
  .submenu-item {
    padding: 8px 12px 8px 0 !important;
    margin: 2px 0;
  }
  .nav-toggle.open + .submenu {
    display: flex;
  }
</style>

<aside class="sidebar">
    <a href="${ctx}/home" class="sidebar-logo">
        <div class="logo-icon">
            <svg viewBox="0 0 40 40" fill="none" xmlns="http://www.w3.org/2000/svg">
                <circle cx="12" cy="20" r="8" stroke="#8EC5FF" stroke-width="3" fill="none"/>
                <circle cx="28" cy="20" r="8" stroke="#CDB4FF" stroke-width="3" fill="none"/>
            </svg>
        </div>
        <span class="logo-text">HRM</span>
    </a>

    <nav class="sidebar-nav">
        <!-- Dashboard (luôn hiện) -->
        <a href="${ctx}/home"
           class="nav-item ${currentPath == ctx.concat('/home') ? 'active' : ''}">
            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <rect x="3" y="3" width="7" height="7"></rect>
                <rect x="14" y="3" width="7" height="7"></rect>
                <rect x="14" y="14" width="7" height="7"></rect>
                <rect x="3" y="14" width="7" height="7"></rect>
            </svg>
            <span>Dashboard</span>
        </a>

        <!-- Employees Group -->
        <c:if test="${showEmployees}">
        <c:set var="empActive" value="${currentPath.startsWith(ctx.concat('/user_list')) ||
                                      currentPath.startsWith(ctx.concat('/admin/users/add')) ||
                                      currentPath.startsWith(ctx.concat('/user_detail')) ||
                                      currentPath.startsWith(ctx.concat('/users/update')) ||
                                      currentPath.startsWith(ctx.concat('/users/toggle-status'))}" />
        <div class="nav-group">
            <button class="nav-item nav-toggle ${empActive ? 'open' : ''}">
                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2"/>
                    <circle cx="9" cy="7" r="4"/>
                    <path d="M22 21v-2a4 4 0 0 0-3-3.87"/>
                    <path d="M16 3.13a4 4 0 0 1 0 7.75"/>
                </svg>
                <span>Employees</span>
                <svg class="chevron" xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <polyline points="6 9 12 15 18 9"/>
                </svg>
            </button>
            <div class="submenu">
                <c:if test="${userPermissions.contains('USER_VIEW_LIST')}">
                    <a href="${ctx}/user_list" class="submenu-item ${currentPath == ctx.concat('/user_list') ? 'active' : ''}">View employee list</a>
                </c:if>
                <c:if test="${userPermissions.contains('USER_CREATE')}">
                    <a href="${ctx}/admin/users/add" class="submenu-item ${currentPath == ctx.concat('/admin/users/add') ? 'active' : ''}">Add new employee</a>
                </c:if>
            </div>
        </div>
        </c:if>

        <!-- Departments Group -->
        <c:if test="${showDepartments}">
        <c:set var="deptActive" value="${currentPath.startsWith(ctx.concat('/admin/departments'))}" />
        <div class="nav-group">
            <button class="nav-item nav-toggle ${deptActive ? 'open' : ''}">
                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/>
                    <polyline points="9 22 9 12 15 12 15 22"/>
                </svg>
                <span>Departments</span>
                <svg class="chevron" xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <polyline points="6 9 12 15 18 9"/>
                </svg>
            </button>
            <div class="submenu">
                <c:if test="${userPermissions.contains('DEPARTMENT_VIEW_LIST')}">
                    <a href="${ctx}/admin/departments" class="submenu-item ${currentPath == ctx.concat('/admin/departments') ? 'active' : ''}">View department list</a>
                </c:if>
                <c:if test="${userPermissions.contains('DEPARTMENT_CREATE')}">
                    <a href="${ctx}/admin/departments/add" class="submenu-item ${currentPath == ctx.concat('/admin/departments/add') ? 'active' : ''}">Add new department</a>
                </c:if>
            </div>
        </div>
        </c:if>

        <!-- Positions Group -->
        <c:if test="${showPositions}">
        <c:set var="posActive" value="${currentPath.startsWith(ctx.concat('/position'))}" />
        <div class="nav-group">
            <button class="nav-item nav-toggle ${posActive ? 'open' : ''}">
                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <rect x="2" y="7" width="20" height="14" rx="2" ry="2"/>
                    <path d="M16 21V5a2 2 0 0 0-2-2h-4a2 2 0 0 0-2 2v16"/>
                </svg>
                <span>Positions</span>
                <svg class="chevron" xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <polyline points="6 9 12 15 18 9"/>
                </svg>
            </button>
            <div class="submenu">
                <c:if test="${userPermissions.contains('POSITION_VIEW_LIST')}">
                    <a href="${ctx}/position/list" class="submenu-item ${currentPath == ctx.concat('/position/list') ? 'active' : ''}">View position list</a>
                </c:if>
                <c:if test="${userPermissions.contains('POSITION_CREATE')}">
                    <a href="${ctx}/position/add" class="submenu-item ${currentPath == ctx.concat('/position/add') ? 'active' : ''}">Add new position</a>
                </c:if>
            </div>
        </div>
        </c:if>

        <!-- Roles Group -->
        <c:if test="${showRoles}">
        <c:set var="roleActive" value="${currentPath.startsWith(ctx.concat('/admin/roles'))}" />
        <div class="nav-group">
            <button class="nav-item nav-toggle ${roleActive ? 'open' : ''}">
                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <circle cx="12" cy="12" r="3"/>
                    <path d="M19.4 15a1.65 1.65 0 0 0 .33 1.82l.06.06a2 2 0 0 1 0 2.83 2 2 0 0 1-2.83 0l-.06-.06a1.65 1.65 0 0 0-1.82-.33 1.65 1.65 0 0 0-1 1.51V21a2 2 0 0 1-2 2 2 2 0 0 1-2-2v-.09A1.65 1.65 0 0 0 9 19.4a1.65 1.65 0 0 0-1.82.33l-.06.06a2 2 0 0 1-2.83 0 2 2 0 0 1 0-2.83l.06-.06a1.65 1.65 0 0 0 .33-1.82 1.65 1.65 0 0 0-1.51-1H3a2 2 0 0 1-2-2 2 2 0 0 1 2-2h.09A1.65 1.65 0 0 0 4.6 9a1.65 1.65 0 0 0-.33-1.82l-.06-.06a2 2 0 0 1 0-2.83 2 2 0 0 1 2.83 0l.06.06a1.65 1.65 0 0 0 1.82.33H9a1.65 1.65 0 0 0 1-1.51V3a2 2 0 0 1 2-2 2 2 0 0 1 2 2v.09a1.65 1.65 0 0 0 1 1.51 1.65 1.65 0 0 0 1.82-.33l.06-.06a2 2 0 0 1 2.83 0 2 2 0 0 1 0 2.83l-.06.06a1.65 1.65 0 0 0-.33 1.82V9a1.65 1.65 0 0 0 1.51 1H21a2 2 0 0 1 2 2 2 2 0 0 1-2 2h-.09a1.65 1.65 0 0 0-1.51 1z"/>
                </svg>
                <span>Roles</span>
                <svg class="chevron" xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <polyline points="6 9 12 15 18 9"/>
                </svg>
            </button>
            <div class="submenu">
                <c:if test="${userPermissions.contains('ROLE_VIEW_LIST')}">
                    <a href="${ctx}/admin/roles" class="submenu-item ${currentPath == ctx.concat('/admin/roles') ? 'active' : ''}">View role list</a>
                </c:if>
                <c:if test="${userPermissions.contains('ROLE_CREATE')}">
                    <a href="${ctx}/admin/roles/add" class="submenu-item ${currentPath == ctx.concat('/admin/roles/add') ? 'active' : ''}">Add new role</a>
                </c:if>
            </div>
        </div>
        </c:if>

        <!-- Contracts Group -->
        <c:if test="${showContracts}">
        <c:set var="contractActive" value="${currentPath.startsWith(ctx.concat('/contracts')) || currentPath.startsWith(ctx.concat('/my-contract'))}" />
        <div class="nav-group">
            <button class="nav-item nav-toggle ${contractActive ? 'open' : ''}">
                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <path d="M14.5 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V7.5L14.5 2z"/>
                    <polyline points="14 2 14 8 20 8"/>
                </svg>
                <span>Contracts</span>
                <svg class="chevron" xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <polyline points="6 9 12 15 18 9"/>
                </svg>
            </button>
            <div class="submenu">
                <c:if test="${userPermissions.contains('CONTRACT_VIEW_LIST')}">
                    <a href="${ctx}/contracts" class="submenu-item ${currentPath == ctx.concat('/contracts') ? 'active' : ''}">View all contracts</a>
                </c:if>
                <c:if test="${userPermissions.contains('CONTRACT_CREATE')}">
                    <a href="${ctx}/contracts/add" class="submenu-item ${currentPath == ctx.concat('/contracts/add') ? 'active' : ''}">Add new contract</a>
                </c:if>
                <c:if test="${userPermissions.contains('CONTRACT_VIEW_OWN')}">
                    <a href="${ctx}/my-contract" class="submenu-item ${currentPath == ctx.concat('/my-contract') ? 'active' : ''}">View my contracts</a>
                </c:if>
            </div>
        </div>
        </c:if>

        <!-- Attendance Group -->
        <c:if test="${showAttendance}">
        <c:set var="attActive" value="${currentPath.startsWith(ctx.concat('/attendance'))}" />
        <div class="nav-group">
            <button class="nav-item nav-toggle ${attActive ? 'open' : ''}">
                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <circle cx="12" cy="12" r="10"></circle>
                    <polyline points="12 6 12 12 16 14"></polyline>
                </svg>
                <span>Attendance</span>
                <svg class="chevron" xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <polyline points="6 9 12 15 18 9"/>
                </svg>
            </button>
            <div class="submenu">
                <c:if test="${userPermissions.contains('ATTENDANCE_VIEW_OWN')}">
                    <a href="${ctx}/attendance/my" class="submenu-item ${currentPath == ctx.concat('/attendance/my') ? 'active' : ''}">My Attendance</a>
                </c:if>
                <c:if test="${userPermissions.contains('ATTENDANCE_VIEW_DEPARTMENT')}">
                    <a href="${ctx}/attendance/department" class="submenu-item ${currentPath == ctx.concat('/attendance/department') ? 'active' : ''}">Department Attendance</a>
                </c:if>
                <c:if test="${userPermissions.contains('ATTENDANCE_VIEW_ALL')}">
                    <a href="${ctx}/attendance/records" class="submenu-item ${currentPath == ctx.concat('/attendance/records') ? 'active' : ''}">Attendance Records</a>
                </c:if>
                <c:if test="${userPermissions.contains('ATTENDANCE_UPDATE')}">
                    <a href="${ctx}/attendance/update" class="submenu-item ${currentPath == ctx.concat('/attendance/update') ? 'active' : ''}">Update Attendance</a>
                    <a href="${ctx}/admin/attendance/import" class="submenu-item ${currentPath == ctx.concat('/admin/attendance/import') ? 'active' : ''}">Import Attendance</a>
                </c:if>
                <c:if test="${userPermissions.contains('ATTENDANCE_EXPORT_REPORT')}">
                    <a href="${ctx}/attendance/export" class="submenu-item ${currentPath == ctx.concat('/attendance/export') ? 'active' : ''}">Export Report</a>
                </c:if>
            </div>
        </div>
        </c:if>

        <!-- Payroll Group -->
        <c:if test="${showPayroll}">
        <c:set var="payrollActive" value="${currentPath.startsWith(ctx.concat('/payroll'))}" />
        <div class="nav-group">
            <button class="nav-item nav-toggle ${payrollActive ? 'open' : ''}">
                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <line x1="12" y1="1" x2="12" y2="23"></line>
                    <path d="M17 5H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H6"></path>
                </svg>
                <span>Payroll</span>
                <svg class="chevron" xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <polyline points="6 9 12 15 18 9"/>
                </svg>
            </button>
            <div class="submenu">
                <c:if test="${userPermissions.contains('PAYROLL_VIEW_OWN')}">
                    <a href="${ctx}/payroll/my" class="submenu-item ${currentPath == ctx.concat('/payroll/my') ? 'active' : ''}">My Payroll</a>
                </c:if>
                <c:if test="${userPermissions.contains('PAYROLL_VIEW_LIST')}">
                    <a href="${ctx}/payroll/list" class="submenu-item ${currentPath == ctx.concat('/payroll/list') ? 'active' : ''}">Payroll List</a>
                </c:if>
                <c:if test="${userPermissions.contains('PAYROLL_GENERATE')}">
                    <a href="${ctx}/payroll/generate" class="submenu-item ${currentPath == ctx.concat('/payroll/generate') ? 'active' : ''}">Generate Payroll</a>
                </c:if>
                <c:if test="${userPermissions.contains('PAYROLL_EXPORT_REPORT')}">
                    <a href="${ctx}/payroll/export" class="submenu-item ${currentPath == ctx.concat('/payroll/export') ? 'active' : ''}">Export Report</a>
                </c:if>
            </div>
        </div>
        </c:if>

        <!-- Others Group (chỉ hiển thị nếu là ADMIN) -->
        <c:if test="${isAdmin}">
        <c:set var="othersActive" value="${currentPath.startsWith(ctx.concat('/admin/password-reset-requests'))}" />
        <div class="nav-group">
            <button class="nav-item nav-toggle ${othersActive ? 'open' : ''}">
                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <circle cx="12" cy="12" r="10"></circle>
                    <polyline points="12 6 12 12 16 14"></polyline>
                </svg>
                <span>Others</span>
                <svg class="chevron" xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <polyline points="6 9 12 15 18 9"/>
                </svg>
            </button>
            <div class="submenu">
                <a href="${ctx}/admin/password-reset-requests"
                   class="submenu-item ${currentPath == ctx.concat('/admin/password-reset-requests') ? 'active' : ''}">
                   Password Reset Requests
                </a>
            </div>
        </div>
        </c:if>
    </nav>
</aside>

<template id="globalHeaderActions">
  <div class="header-right global-header-actions">
    <button type="button" class="header-icon" aria-label="Notifications">
      <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
        <path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9"></path>
        <path d="M13.73 21a2 2 0 0 1-3.46 0"></path>
      </svg>
    </button>

    <div class="header-profile" data-profile-dropdown-toggle>
      <c:choose>
        <c:when test="${not empty sessionScope.currentUser.avatarUrl}">
          <img src="${sessionScope.currentUser.avatarUrl}" alt="User" class="profile-avatar">
        </c:when>
        <c:otherwise>
          <div class="avatar-placeholder-small">${sessionScope.currentUser.fullName.substring(0,1)}</div>
        </c:otherwise>
      </c:choose>
      <div class="profile-info">
        <p class="profile-name">${sessionScope.currentUser.fullName}</p>
        <p class="profile-status">Online</p>
      </div>
      <div class="dropdown-menu" data-profile-dropdown>
        <a href="${ctx}/profile" class="dropdown-item">View My Profile</a>
        <a href="${ctx}/change-password" class="dropdown-item">Change Password</a>
        <a href="${ctx}/logout" class="dropdown-item">Logout</a>
      </div>
    </div>
  </div>
</template>

<script>
  (function() {
    document.addEventListener('DOMContentLoaded', function() {
      const toggles = document.querySelectorAll('.nav-toggle');
      toggles.forEach(function(toggle) {
        const submenu = toggle.nextElementSibling;
        if (!submenu) return;
        if (toggle.classList.contains('open')) {
          submenu.style.display = 'flex';
        } else {
          submenu.style.display = 'none';
        }
        toggle.addEventListener('click', function(e) {
          e.preventDefault();
          if (this.classList.contains('open') && submenu.querySelector('.submenu-item.active')) {
            return;
          }
          this.classList.toggle('open');
          if (this.classList.contains('open')) {
            submenu.style.display = 'flex';
          } else {
            submenu.style.display = 'none';
          }
        });
      });

      const header = document.querySelector('.dashboard-header');
      const content = document.querySelector('.dashboard-content');
      const headerTemplate = document.getElementById('globalHeaderActions');

      if (header && headerTemplate) {
        const existingActions = header.querySelector('.header-right:not(.global-header-actions)');
        if (existingActions) {
          if (content && existingActions.children.length > 0) {
            const pageActions = document.createElement('div');
            pageActions.className = 'page-actions';
            while (existingActions.firstChild) {
              pageActions.appendChild(existingActions.firstChild);
            }
            content.prepend(pageActions);
          }
          existingActions.remove();
        }

        header.appendChild(headerTemplate.content.cloneNode(true));
      }

      const profileToggle = document.querySelector('[data-profile-dropdown-toggle]');
      const profileDropdown = document.querySelector('[data-profile-dropdown]');
      if (profileToggle && profileDropdown) {
        profileToggle.addEventListener('click', function(e) {
          e.stopPropagation();
          profileDropdown.classList.toggle('show');
        });

        document.addEventListener('click', function() {
          profileDropdown.classList.remove('show');
        });
      }
    });
  })();
</script>
