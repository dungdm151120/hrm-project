<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:set var="currentPath" value="${requestScope['jakarta.servlet.forward.request_uri']}" />
<c:if test="${empty currentPath}">
  <c:set var="currentPath" value="${pageContext.request.requestURI}" />
</c:if>
<c:set var="ctx" value="${pageContext.request.contextPath}" />

<%
  // Lấy set permission từ session (được thiết lập sau login)
  java.util.Set<String> userPermissions = (java.util.Set<String>) session.getAttribute("userPermissions");
  pageContext.setAttribute("userPermissions", userPermissions);
%>

<c:set var="showEmployees" value="${userPermissions.contains('USER_VIEW_LIST') or userPermissions.contains('USER_CREATE')}" />
<c:set var="showDepartments" value="${userPermissions.contains('DEPARTMENT_VIEW_LIST') or userPermissions.contains('DEPARTMENT_CREATE')}" />
<c:set var="showPositions" value="${userPermissions.contains('POSITION_VIEW_LIST') or userPermissions.contains('POSITION_CREATE')}" />
<c:set var="showRoles" value="${userPermissions.contains('ROLE_VIEW_LIST') or userPermissions.contains('ROLE_CREATE')}" />
<c:set var="showContracts" value="${userPermissions.contains('CONTRACT_VIEW_LIST') or userPermissions.contains('CONTRACT_VIEW_OWN') or userPermissions.contains('CONTRACT_CREATE')}" />

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
                <circle cx="12" cy="20" r="8" stroke="#FF9F43" stroke-width="3" fill="none"/>
                <circle cx="28" cy="20" r="8" stroke="#3B82F6" stroke-width="3" fill="none"/>
            </svg>
        </div>
        <span class="logo-text">HR<span>Sync</span></span>
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
    </nav>
</aside>

<script>
  (function() {
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
  })();
</script>