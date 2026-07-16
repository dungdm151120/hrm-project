<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:set var="currentPath" value="${requestScope['jakarta.servlet.forward.request_uri']}" />
<c:if test="${empty currentPath}">
  <c:set var="currentPath" value="${pageContext.request.requestURI}" />
</c:if>
<c:set var="ctx" value="${pageContext.request.contextPath}" />

<%
  java.util.Set<String> userPermissions = (java.util.Set<String>) session.getAttribute("userPermissions");
  pageContext.setAttribute("userPermissions", userPermissions);

  model.User currentUser = (model.User) session.getAttribute("currentUser");

  int unreadAnnouncementCount = 0;
  int unreadRequestNotificationCount = 0;
  java.util.List<model.Announcement> unreadAnnouncements = java.util.Collections.emptyList();
  java.util.List<model.RequestNotification> unreadRequestNotifications = java.util.Collections.emptyList();
  if (currentUser != null && userPermissions != null && userPermissions.contains("ANNOUNCEMENT_VIEW_LIST")) {
    dao.AnnouncementDAO announcementDAO = new dao.AnnouncementDAO();
    unreadAnnouncementCount = announcementDAO.countUnread(currentUser.getId());
    unreadAnnouncements = announcementDAO.getLatestUnread(currentUser.getId(), 5);
  }
  if (currentUser != null && userPermissions != null
          && (userPermissions.contains("VIEW_MY_REQUEST")
              || userPermissions.contains("VIEW_ALL_REQUEST")
              || userPermissions.contains("PROCESS_REQUEST")
              || userPermissions.contains("CREATE_REQUEST"))) {
    dao.RequestDAO requestDAO = new dao.RequestDAO();
    unreadRequestNotificationCount = requestDAO.countUnreadRequestNotifications(currentUser.getId());
    unreadRequestNotifications = requestDAO.getLatestUnreadRequestNotifications(currentUser.getId(), 5);
  }
  int totalUnreadNotificationCount = unreadAnnouncementCount + unreadRequestNotificationCount;
  pageContext.setAttribute("unreadAnnouncementCount", unreadAnnouncementCount);
  pageContext.setAttribute("unreadRequestNotificationCount", unreadRequestNotificationCount);
  pageContext.setAttribute("totalUnreadNotificationCount", totalUnreadNotificationCount);
  pageContext.setAttribute("unreadAnnouncements", unreadAnnouncements);
  pageContext.setAttribute("unreadRequestNotifications", unreadRequestNotifications);
%>

<c:set var="showEmployees"   value="${userPermissions.contains('USER_VIEW_LIST') or userPermissions.contains('USER_CREATE')}" />
<c:set var="showDepartments" value="${userPermissions.contains('DEPARTMENT_VIEW_LIST') or userPermissions.contains('DEPARTMENT_CREATE')}" />
<c:set var="showPositions"   value="${userPermissions.contains('POSITION_VIEW_LIST') or userPermissions.contains('POSITION_CREATE')}" />
<c:set var="showRoles"       value="${userPermissions.contains('ROLE_VIEW_LIST') or userPermissions.contains('ROLE_CREATE')}" />
<c:set var="showContracts"   value="${userPermissions.contains('CONTRACT_VIEW_LIST') or userPermissions.contains('CONTRACT_VIEW_OWN') or userPermissions.contains('CONTRACT_CREATE')}" />
<c:set var="showAttendance"  value="${userPermissions.contains('ATTENDANCE_VIEW_OWN') or userPermissions.contains('ATTENDANCE_VIEW_DEPARTMENT') or userPermissions.contains('ATTENDANCE_VIEW_ALL') or userPermissions.contains('ATTENDANCE_UPDATE') or userPermissions.contains('ATTENDANCE_EXPORT_REPORT')}" />
<c:set var="showTasks"       value="${userPermissions.contains('TASK_VIEW')}" />
<c:set var="showRequests"    value="${userPermissions.contains('VIEW_MY_REQUEST') or userPermissions.contains('VIEW_ALL_REQUEST') or userPermissions.contains('CREATE_REQUEST')}" />
<c:set var="showPayroll"     value="${userPermissions.contains('PAYROLL_VIEW_OWN') or userPermissions.contains('PAYROLL_VIEW_LIST') or userPermissions.contains('PAYROLL_GENERATE') or userPermissions.contains('PAYROLL_EXPORT_REPORT')}" />
<c:set var="showAnnouncements" value="${userPermissions.contains('ANNOUNCEMENT_VIEW_LIST') or userPermissions.contains('ANNOUNCEMENT_CREATE')}" />

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
  .sidebar-menu-label {
    flex: 1;
    min-width: 0;
  }
  .sidebar-unread-dot {
    width: 8px;
    height: 8px;
    border-radius: 999px;
    background: #ef4444;
    flex-shrink: 0;
  }
  .notification-wrapper {
    position: relative;
  }
  .notification-badge {
    position: absolute;
    top: -4px;
    right: -4px;
    min-width: 18px;
    height: 18px;
    padding: 0 5px;
    border-radius: 999px;
    background: #ef4444;
    color: #fff;
    font-size: 11px;
    line-height: 18px;
    text-align: center;
  }
  .notification-dropdown {
    position: absolute;
    top: 42px;
    right: 0;
    width: 340px;
    max-width: calc(100vw - 32px);
    background: #fff;
    border: 1px solid #e5e7eb;
    border-radius: 8px;
    box-shadow: 0 16px 40px rgba(15, 23, 42, 0.14);
    display: none;
    z-index: 100;
  }
  .notification-dropdown.show {
    display: block;
  }
  .notification-header {
    padding: 12px 14px;
    border-bottom: 1px solid #e5e7eb;
    font-weight: 700;
    color: #111827;
  }
  .notification-list {
    max-height: 320px;
    overflow-y: auto;
  }
  .notification-section-title {
    padding: 10px 14px 6px;
    color: #6b7280;
    font-size: 12px;
    font-weight: 700;
    text-transform: uppercase;
  }
  .notification-item {
    display: block;
    padding: 12px 14px;
    border-bottom: 1px solid #f3f4f6;
    text-decoration: none;
    color: #111827;
  }
  .notification-item:hover {
    background: #f9fafb;
  }
  .notification-title {
    margin: 0 0 4px;
    font-weight: 600;
  }
  .notification-meta {
    margin: 0;
    font-size: 12px;
    color: #6b7280;
  }
  .notification-source {
    color: #374151;
    font-weight: 600;
  }
  .notification-empty {
    padding: 18px 14px;
    color: #6b7280;
    text-align: center;
  }
  .notification-footer {
    display: block;
    padding: 11px 14px;
    text-align: center;
    text-decoration: none;
    color: #2563eb;
    font-weight: 600;
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

        <!-- Announcements Group -->
                <c:if test="${showAnnouncements}">
                <c:set var="announcementActive" value="${currentPath.startsWith(ctx.concat('/announcements'))}" />
                <div class="nav-group">
                    <button class="nav-item nav-toggle ${announcementActive ? 'open' : ''}">
                        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                            <path d="M4 22V4a2 2 0 0 1 2-2h12a2 2 0 0 1 2 2v18l-4-2-4 2-4-2-4 2z"/>
                            <path d="M8 6h8"/>
                            <path d="M8 10h8"/>
                            <path d="M8 14h5"/>
                        </svg>
                        <span class="sidebar-menu-label">Announcements</span>
                        <c:if test="${unreadAnnouncementCount > 0}">
                            <span class="sidebar-unread-dot" title="Unread announcements"></span>
                        </c:if>
                        <svg class="chevron" xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                            <polyline points="6 9 12 15 18 9"/>
                        </svg>
                    </button>
                    <div class="submenu">
                        <c:if test="${userPermissions.contains('ANNOUNCEMENT_VIEW_LIST')}">
                            <a href="${ctx}/announcements" class="submenu-item ${currentPath == ctx.concat('/announcements') ? 'active' : ''}">View announcements</a>
                        </c:if>
                        <c:if test="${userPermissions.contains('ANNOUNCEMENT_CREATE')}">
                            <a href="${ctx}/announcements/add" class="submenu-item ${currentPath == ctx.concat('/announcements/add') ? 'active' : ''}">Create announcement</a>
                        </c:if>
                    </div>
                </div>
                </c:if>

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
        <!-- Organization Group -->
        <c:if test="${userPermissions.contains('DEPARTMENT_VIEW_LIST')}">
        <c:set var="orgActive" value="${currentPath.startsWith(ctx.concat('/admin/company-structure'))}" />
        <div class="nav-group">
            <button class="nav-item nav-toggle ${orgActive ? 'open' : ''}">
                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <rect x="3" y="3" width="7" height="5" rx="1"></rect>
                    <rect x="14" y="3" width="7" height="5" rx="1"></rect>
                    <rect x="8" y="14" width="8" height="5" rx="1"></rect>
                    <line x1="8" y1="8" x2="12" y2="14"></line>
                    <line x1="16" y1="8" x2="12" y2="14"></line>
                </svg>
                <span>Organization</span>
                <svg class="chevron" xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <polyline points="6 9 12 15 18 9"/>
                </svg>
            </button>
            <div class="submenu">
                <a href="${ctx}/admin/company-structure"
                   class="submenu-item ${currentPath == ctx.concat('/admin/company-structure') ? 'active' : ''}">
                   View Company Organization
                </a>
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
                    <a href="${ctx}/attendance/my"
                       class="submenu-item ${currentPath == ctx.concat('/attendance/my') ? 'active' : ''}">
                       My Attendance
                    </a>
                </c:if>
                <c:if test="${userPermissions.contains('ATTENDANCE_VIEW_DEPARTMENT')}">
                    <a href="${ctx}/attendance/department" class="submenu-item ${currentPath == ctx.concat('/attendance/department') ? 'active' : ''}">Department Attendance</a>
                </c:if>
                <c:if test="${userPermissions.contains('ATTENDANCE_VIEW_ALL')}">
                    <a href="${ctx}/attendance/view_all" class="submenu-item ${currentPath == ctx.concat('/attendance/view_all') ? 'active' : ''}">All Attendance (View)</a>
                    <a href="${ctx}/attendance/work-hours" class="submenu-item ${currentPath == ctx.concat('/attendance/work-hours') ? 'active' : ''}">Work Hours Summary</a>
                </c:if>
                <c:if test="${userPermissions.contains('ATTENDANCE_UPDATE')}">
                    <a href="${ctx}/attendance/records" class="submenu-item ${currentPath == ctx.concat('/attendance/records') || currentPath.startsWith(ctx.concat('/attendance/update')) ? 'active' : ''}">Update Attendance</a>
                    <a href="${ctx}/admin/attendance/import" class="submenu-item ${currentPath == ctx.concat('/admin/attendance/import') ? 'active' : ''}">Import Attendance</a>
                </c:if>
                <c:if test="${userPermissions.contains('ATTENDANCE_EXPORT_REPORT')}">
                    <a href="${ctx}/attendance/export" class="submenu-item ${currentPath == ctx.concat('/attendance/export') ? 'active' : ''}">Export Report</a>
                </c:if>
                <c:if test="${userPermissions.contains('ATTENDANCE_CONFIRM_DEPT') || userPermissions.contains('ATTENDANCE_SEND_TO_BUSINESS') || userPermissions.contains('ATTENDANCE_APPROVE_BUSINESS')}">
                    <a href="${ctx}/attendance/confirm" class="submenu-item ${currentPath == ctx.concat('/attendance/confirm') ? 'active' : ''}">Confirm Attendance</a>
                    <a href="${ctx}/attendance/confirm-list" class="submenu-item ${currentPath.startsWith(ctx.concat('/attendance/confirm-list')) or currentPath.startsWith(ctx.concat('/attendance/confirm-detail')) ? 'active' : ''}">Confirmed Attendance List</a>
                </c:if>
            </div>
        </div>
        </c:if>
        
        <!-- Reports Group -->
        <c:set var="showReports" value="${userPermissions.contains('ATTENDANCE_VIEW_ALL') or userPermissions.contains('ATTENDANCE_VIEW_DEPARTMENT') or userPermissions.contains('ATTENDANCE_EXPORT_REPORT')}" />
        <c:if test="${showReports}">
        <c:set var="reportActive" value="${currentPath.startsWith(ctx.concat('/reports'))}" />
        <div class="nav-group">
            <button class="nav-item nav-toggle ${reportActive ? 'open' : ''}">
                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="width: 20px; height: 20px;">
                    <line x1="18" y1="20" x2="18" y2="10"></line>
                    <line x1="12" y1="20" x2="12" y2="4"></line>
                    <line x1="6" y1="20" x2="6" y2="14"></line>
                </svg>
                <span>Reports</span>
                <svg class="chevron" xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <polyline points="6 9 12 15 18 9"/>
                </svg>
            </button>
            <div class="submenu" style="${reportActive ? 'display: flex !important;' : ''}">
                <a href="${ctx}/reports/attendance" class="submenu-item ${currentPath == ctx.concat('/reports/attendance') ? 'active' : ''}">Attendance Report</a>
                <a href="${ctx}/reports/hr" class="submenu-item ${currentPath == ctx.concat('/reports/hr') ? 'active' : ''}">HR Report</a>
            </div>
        </div>
        </c:if>

        <!-- Request Group -->
        <c:if test="${showRequests}">
            <c:set var="reqActive" value="${currentPath.startsWith(ctx.concat('/view_my_request')) ||
                                    currentPath.startsWith(ctx.concat('/view_pending_request')) ||
                                    currentPath.startsWith(ctx.concat('/view_observed_request')) ||
                                    currentPath.startsWith(ctx.concat('/view_handled_request')) ||
                                    currentPath.startsWith(ctx.concat('/view_all_request')) ||
                                    currentPath.startsWith(ctx.concat('/create_request')) ||
                                    currentPath.startsWith(ctx.concat('/request_detail'))}" />

            <div class="nav-group">
                <button class="nav-item nav-toggle ${reqActive ? 'open' : ''}">
                    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path>
                        <polyline points="14 2 14 8 20 8"></polyline>
                        <line x1="16" y1="13" x2="8" y2="13"></line>
                        <line x1="16" y1="17" x2="8" y2="17"></line>
                        <polyline points="10 9 9 9 8 9"></polyline>
                    </svg>
                    <span class="sidebar-menu-label">Requests</span>
                    <c:if test="${unreadRequestNotificationCount > 0}">
                        <span class="sidebar-unread-dot" title="Unread request notifications"></span>
                    </c:if>
                    <svg class="chevron" xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <polyline points="6 9 12 15 18 9"/>
                    </svg>
                </button>
                <div class="submenu" style="${reqActive ? 'display: flex !important;' : ''}">
                    <c:if test="${userPermissions.contains('VIEW_MY_REQUEST')}">
                        <a href="${ctx}/view_my_request" class="submenu-item ${currentPath == ctx.concat('/view_my_request') ? 'active' : ''}">View my requests</a>
                    </c:if>
                    <!-- Mọi người dùng đăng nhập đều có thể được làm approver, nên cứ hiển thị menu Pending Approvals -->
                    <a href="${ctx}/view_pending_request" class="submenu-item ${currentPath == ctx.concat('/view_pending_request') ? 'active' : ''}">Pending Approvals</a>
                    <c:if test="${userPermissions.contains('VIEW_ALL_REQUEST')}">
                        <a href="${ctx}/view_all_request" class="submenu-item ${currentPath == ctx.concat('/view_all_request') ? 'active' : ''}">View all requests</a>
                    </c:if>
                    <c:if test="${userPermissions.contains('CREATE_REQUEST')}">
                        <a href="${ctx}/create_request" class="submenu-item ${currentPath == ctx.concat('/create_request') ? 'active' : ''}">Create request</a>
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
                    <a href="${ctx}/payroll/department" class="submenu-item ${currentPath == ctx.concat('/payroll/department') ? 'active' : ''}">Payroll Department</a>
                </c:if>
                <c:if test="${userPermissions.contains('PAYROLL_GENERATE')}">
                    <a href="${ctx}/payroll/generate" class="submenu-item ${currentPath == ctx.concat('/payroll/generate') ? 'active' : ''}">Generate Payroll</a>
                </c:if>
                <c:if test="${userPermissions.contains('PAYROLL_UPDATE_COMPONENT')}">
                    <a href="${ctx}/payroll/update_component" class="submenu-item ${currentPath == ctx.concat('/payroll/update_component') ? 'active' : ''}">Payroll Component</a>
                </c:if>
                <c:if test="${userPermissions.contains('PAYROLL_EXPORT_REPORT')}">
                    <a href="${ctx}/payroll/export" class="submenu-item ${currentPath == ctx.concat('/payroll/export') ? 'active' : ''}">Export Payrolls</a>
                </c:if>
            </div>
        </div>
        </c:if>

        <!-- Task Group -->
        <c:if test="${showTasks}">
        <c:set var="taskActive" value="${currentPath.startsWith(ctx.concat('/tasks'))}" />
        <div class="nav-group">
            <button class="nav-item nav-toggle ${taskActive ? 'open' : ''}">
                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <path d="M9 11l3 3L22 4"/>
                    <path d="M21 12v7a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11"/>
                </svg>
                <span>Tasks</span>
                <svg class="chevron" xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <polyline points="6 9 12 15 18 9"/>
                </svg>
            </button>
            <div class="submenu" style="${taskActive ? 'display: flex !important;' : ''}">
                <a href="${ctx}/tasks" class="submenu-item ${currentPath == ctx.concat('/tasks') ? 'active' : ''}">View tasks</a>
                <c:if test="${userPermissions.contains('TASK_VIEW_ALL')}">
                    <a href="${ctx}/tasks/all" class="submenu-item ${currentPath == ctx.concat('/tasks/all') ? 'active' : ''}">View all tasks</a>
                </c:if>
                <c:if test="${userPermissions.contains('TASK_CREATE')}">
                    <a href="${ctx}/tasks/create" class="submenu-item ${currentPath == ctx.concat('/tasks/create') ? 'active' : ''}">Create task</a>
                </c:if>
            </div>
        </div>
        </c:if>

        <!-- Others Group -->
        <c:if test="${userPermissions.contains('PASSWORD_RESET_REQUEST_VIEW')}">
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
    <c:if test="${userPermissions.contains('ANNOUNCEMENT_VIEW_LIST')
                  or userPermissions.contains('VIEW_MY_REQUEST')
                  or userPermissions.contains('VIEW_ALL_REQUEST')
                  or userPermissions.contains('PROCESS_REQUEST')
                  or userPermissions.contains('CREATE_REQUEST')}">
      <div class="notification-wrapper">
        <button type="button" class="header-icon" aria-label="Notifications" data-notification-toggle>
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9"></path>
            <path d="M13.73 21a2 2 0 0 1-3.46 0"></path>
          </svg>
          <c:if test="${totalUnreadNotificationCount > 0}">
            <span class="notification-badge">
              <c:choose>
                <c:when test="${totalUnreadNotificationCount > 99}">99+</c:when>
                <c:otherwise>${totalUnreadNotificationCount}</c:otherwise>
              </c:choose>
            </span>
          </c:if>
        </button>
        <div class="notification-dropdown" data-notification-dropdown>
          <div class="notification-header">Notifications</div>
          <div class="notification-list">
            <c:choose>
              <c:when test="${empty unreadAnnouncements and empty unreadRequestNotifications}">
                <div class="notification-empty">No unread notifications.</div>
              </c:when>
              <c:otherwise>
                <c:if test="${not empty unreadRequestNotifications}">
                  <div class="notification-section-title">Requests</div>
                  <c:forEach items="${unreadRequestNotifications}" var="notification">
                    <a class="notification-item" href="${ctx}/request_detail?id=${notification.requestId}">
                      <p class="notification-title"><c:out value="${notification.message}"/></p>
                      <p class="notification-meta">
                        <c:if test="${not empty notification.actorName}">
                          <span class="notification-source">By <c:out value="${notification.actorName}"/></span> -
                        </c:if>
                        <fmt:formatDate value="${notification.createdAt}" pattern="dd/MM/yyyy HH:mm"/>
                      </p>
                    </a>
                  </c:forEach>
                </c:if>
                <c:if test="${not empty unreadAnnouncements}">
                  <div class="notification-section-title">Announcements</div>
                  <c:forEach items="${unreadAnnouncements}" var="notification">
                    <a class="notification-item" href="${ctx}/announcements/detail?id=${notification.id}">
                      <p class="notification-title"><c:out value="${notification.title}"/></p>
                      <p class="notification-meta"><c:out value="${notification.targetDisplay}"/> - ${notification.publishDateDisplay}</p>
                    </a>
                  </c:forEach>
                </c:if>
              </c:otherwise>
            </c:choose>
          </div>
          <c:if test="${userPermissions.contains('VIEW_MY_REQUEST')}">
            <a href="${ctx}/view_my_request" class="notification-footer">View requests</a>
          </c:if>
          <c:if test="${userPermissions.contains('ANNOUNCEMENT_VIEW_LIST')}">
            <a href="${ctx}/announcements?readStatus=UNREAD" class="notification-footer">View announcements</a>
          </c:if>
        </div>
      </div>
    </c:if>

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
      const notificationToggle = document.querySelector('[data-notification-toggle]');
      const notificationDropdown = document.querySelector('[data-notification-dropdown]');

      if (notificationToggle && notificationDropdown) {
        notificationToggle.addEventListener('click', function(e) {
          e.stopPropagation();
          notificationDropdown.classList.toggle('show');
          if (profileDropdown) {
            profileDropdown.classList.remove('show');
          }
        });
      }

      if (profileToggle && profileDropdown) {
        profileToggle.addEventListener('click', function(e) {
          e.stopPropagation();
          profileDropdown.classList.toggle('show');
          if (notificationDropdown) {
            notificationDropdown.classList.remove('show');
          }
        });

        document.addEventListener('click', function() {
          profileDropdown.classList.remove('show');
          if (notificationDropdown) {
            notificationDropdown.classList.remove('show');
          }
        });
      }
    });
  })();
</script>
