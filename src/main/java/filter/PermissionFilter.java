package filter;

import dao.UserDAO;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.User;

import java.io.IOException;
import java.util.Set;

@WebFilter("/*")
public class PermissionFilter implements Filter {
    private final UserDAO userDAO = new UserDAO();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String path = req.getServletPath();
        String method = req.getMethod();

        String requiredPermission = resolveRequiredPermission(req, path, method);

        if (requiredPermission == null) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = req.getSession(false);
        if (session == null) {
            res.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        @SuppressWarnings("unchecked")
        Set<String> userPermissions = (Set<String>) session.getAttribute("userPermissions");

        boolean permitted = userPermissions != null
                && ("ATTENDANCE_VIEW_SUMMARY".equals(requiredPermission)
                ? canViewAttendanceSummary(req, session, userPermissions)
                : ("ATTENDANCE_CONFIRM_ACCESS".equals(requiredPermission)
                ? (userPermissions.contains("ATTENDANCE_CONFIRM_DEPT") || userPermissions.contains("ATTENDANCE_SEND_TO_BUSINESS") || userPermissions.contains("ATTENDANCE_APPROVE_BUSINESS"))
                : ("ATTENDANCE_REPORT_ACCESS".equals(requiredPermission)
                ? (userPermissions.contains("ATTENDANCE_VIEW_ALL") || userPermissions.contains("ATTENDANCE_VIEW_DEPARTMENT") || userPermissions.contains("ATTENDANCE_EXPORT_REPORT"))
                : userPermissions.contains(requiredPermission))));

        if (!permitted) {
            res.setStatus(HttpServletResponse.SC_FORBIDDEN);
            req.setAttribute("permissionDenied", requiredPermission);
            req.getRequestDispatcher("/WEB-INF/views/common/403.jsp").forward(req, res);
            return;
        }

        chain.doFilter(request, response);
    }

    private String resolveRequiredPermission(HttpServletRequest request, String path, String method) {
        // Public
        if (path.equals("/login") || path.equals("/logout") || path.equals("/forgot-password") || path.equals("/reset-password")) {
            return null;
        }

        // Home
        if (path.equals("/home")) return "HOMEPAGE_VIEW";

        // Profile
        if (path.equals("/profile")) return "PROFILE_VIEW";
        if (path.equals("/change-password")) return "PROFILE_CHANGE_PASSWORD";

        // User
        if (path.equals("/user_list") && "GET".equals(method)) return "USER_VIEW_LIST";
        if (path.equals("/user_detail") && "GET".equals(method)) return "USER_VIEW_DETAIL";
        if (path.equals("/admin/users/add")) return "USER_CREATE";
        if (path.equals("/users/update")) return "USER_UPDATE";
        if (path.equals("/users/toggle-status") && "POST".equals(method)) return "USER_TOGGLE_STATUS";
        if (path.equals("/admin/password-reset-requests") && "GET".equals(method)) return "PASSWORD_RESET_REQUEST_VIEW";
        if (path.equals("/admin/password-reset/approve") && "POST".equals(method)) return "PASSWORD_RESET_REQUEST_PROCESS";
        if (path.equals("/admin/password-reset/reject") && "POST".equals(method)) return "PASSWORD_RESET_REQUEST_PROCESS";

        // Role
        if (path.equals("/admin/roles") && "GET".equals(method)) return "ROLE_VIEW_LIST";
        if (path.equals("/admin/roles/permissions") && "GET".equals(method)) return "ROLE_VIEW_PERMISSION";
        if (path.equals("/admin/roles/update")) return "ROLE_UPDATE";
        if (path.equals("/admin/roles/toggle_status") && "POST".equals(method)) return "ROLE_TOGGLE_STATUS";
        if (path.equals("/admin/roles/edit_permissions")) return "ROLE_EDIT_PERMISSION";
        if (path.equals("/admin/roles/add")) return "ROLE_CREATE";

        // Department
        if (path.equals("/admin/departments") && "GET".equals(method)) return "DEPARTMENT_VIEW_LIST";
        if (path.equals("/admin/departments/detail") && "GET".equals(method)) return "DEPARTMENT_VIEW_DETAIL";
        if (path.equals("/admin/departments/add")) return "DEPARTMENT_CREATE";
        if (path.equals("/admin/departments/update")) return "DEPARTMENT_UPDATE";
        if (path.equals("/admin/departments/toggle-status") && "POST".equals(method)) return "DEPARTMENT_TOGGLE_STATUS";
        if (path.equals("/add_member")) return "DEPARTMENT_MOVE_MEMBER";
        if (path.equals("/remove_member")) return "DEPARTMENT_MOVE_MEMBER";
        if (path.equals("/move_member")) return "DEPARTMENT_MOVE_MEMBER";
        if (path.equals("/admin/departments/assign-manager")) return "DEPARTMENT_ASSIGN_MANAGER";
        if (path.equals("/admin/departments/unassign-manager")) return "DEPARTMENT_ASSIGN_MANAGER";
        if (path.equals("/admin/departments/assign-positions")) return "DEPARTMENT_ASSIGN_POSITION";
        if (path.equals("/admin/departments/employees")) return "DEPARTMENT_VIEW_EMPLOYEES";

        // Position
        if (path.equals("/position/list") && "GET".equals(method)) return "POSITION_VIEW_LIST";
        if (path.equals("/position/add")) return "POSITION_CREATE";
        if (path.equals("/position/update")) return "POSITION_UPDATE";
        if (path.equals("/position/toggle-status") && "POST".equals(method)) return "POSITION_TOGGLE_STATUS";

        // Contract
        if (path.equals("/contracts") && "GET".equals(method)) return "CONTRACT_VIEW_LIST";
        if (path.equals("/contracts/detail") && "GET".equals(method)) return "CONTRACT_VIEW_DETAIL";
        if (path.equals("/my-contract") && "GET".equals(method)) return "CONTRACT_VIEW_OWN";
        if (path.equals("/my-contract/detail") && "GET".equals(method)) return "CONTRACT_VIEW_OWN";
        if (path.equals("/contracts/add")) return "CONTRACT_CREATE";
        if (path.equals("/contracts/update")) return "CONTRACT_UPDATE";
        if (path.equals("/contracts/terminate")) return "CONTRACT_TERMINATE";

        // Attendance
        if (path.equals("/attendance/check-in") && "POST".equals(method)) return "ATTENDANCE_CHECK_IN";
        if (path.equals("/attendance/check-out") && "POST".equals(method)) return "ATTENDANCE_CHECK_OUT";
        if (path.equals("/attendance/summary") && "GET".equals(method)) return "ATTENDANCE_VIEW_SUMMARY";
        if (path.equals("/attendance/records") && "GET".equals(method)) return "ATTENDANCE_VIEW_ALL";
        if (path.equals("/attendance/department") && "GET".equals(method)) return "ATTENDANCE_VIEW_DEPARTMENT";
        if (path.equals("/attendance/view_all") && "GET".equals(method)) return "ATTENDANCE_VIEW_ALL";
        if (path.equals("/attendance/work-hours") && "GET".equals(method)) return "ATTENDANCE_VIEW_ALL";
        if (path.equals("/attendance/all") && "GET".equals(method)) return "ATTENDANCE_VIEW_ALL";
        if (path.equals("/attendance/update")) return "ATTENDANCE_UPDATE";
        if (path.equals("/attendance/export")) return "ATTENDANCE_EXPORT_REPORT";
        if (path.equals("/attendance/confirm")) return "ATTENDANCE_CONFIRM_ACCESS";
        if (path.equals("/reports/attendance")) return "ATTENDANCE_REPORT_ACCESS";

        // Payroll
        if (path.equals("/payroll/my") && "GET".equals(method)) return "PAYROLL_VIEW_OWN";
        if (path.equals("/payroll/department")) return "PAYROLL_VIEW_DEPARTMENT";
        if (path.equals("/payroll/list")) return "PAYROLL_VIEW_LIST";
        if (path.equals("/payroll/detail")) return "PAYROLL_VIEW_DETAIL";
        if (path.equals("/payroll/confirm")) return "PAYROLL_CONFIRM";
        if (path.equals("/payroll/generate")) return "PAYROLL_GENERATE";
        if (path.equals("/payroll/update-component")) return "PAYROLL_UPDATE_COMPONENT";
        if (path.equals("/payroll/export")) return "PAYROLL_EXPORT_REPORT";

        // Announcement
        if (path.equals("/announcements") && "GET".equals(method)) return "ANNOUNCEMENT_VIEW_LIST";
        if (path.equals("/announcements/detail") && "GET".equals(method)) return "ANNOUNCEMENT_VIEW_DETAIL";
        if (path.equals("/announcements/add")) return "ANNOUNCEMENT_CREATE";

        // Request
        if (path.equals("/view_my_request") && "GET".equals(method)) return "VIEW_MY_REQUEST";
        if (path.equals("/view_all_request") && "GET".equals(method)) return "VIEW_ALL_REQUEST";
        if (path.equals("/view_department_request") && "GET".equals(method)) return "VIEW_DEPARTMENT_REQUESTS";
        if (path.equals("/request_detail") && "GET".equals(method)) return "VIEW_REQUEST_DETAIL";
        if (path.equals("/process_request") && "POST".equals(method)) return "PROCESS_REQUEST";
        if (path.equals("/create_request")) return "CREATE_REQUEST";
        // Task
        if (path.equals("/tasks") && "GET".equals(method)) return "TASK_VIEW";
        if (path.equals("/tasks/all") && "GET".equals(method)) return "TASK_VIEW_ALL";
        if (path.equals("/tasks/detail") && "GET".equals(method)) return "TASK_VIEW";
        if (path.equals("/tasks/comment") && "POST".equals(method)) return "TASK_VIEW";
        if (path.equals("/tasks/checklist/toggle")) return "TASK_VIEW";
        if (path.equals("/tasks/create")) return "TASK_CREATE";
        if (path.equals("/tasks/edit")) return "TASK_UPDATE";
        if (path.equals("/tasks/delete") && "POST".equals(method)) return "TASK_DELETE";
        if (path.equals("/tasks/checklist/add") && "POST".equals(method)) return "TASK_MANAGE_CHECKLIST";
        if (path.equals("/tasks/checklist/assign") && "POST".equals(method)) return "TASK_MANAGE_CHECKLIST";
        if (path.equals("/tasks/checklist/delete") && "POST".equals(method)) return "TASK_MANAGE_CHECKLIST";
        if (path.equals("/tasks/status") && "POST".equals(method)) return "TASK_UPDATE_STATUS";
        return null;
    }

    private boolean canViewAttendanceSummary(
            HttpServletRequest request,
            HttpSession session,
            Set<String> userPermissions
    ) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return false;
        }

        String userIdParameter = request.getParameter("userId");
        if (userIdParameter == null || userIdParameter.isBlank()) {
            return userPermissions.contains("ATTENDANCE_VIEW_OWN");
        }

        try {
            int requestedUserId = Integer.parseInt(userIdParameter);
            if (requestedUserId == currentUser.getId()) {
                return userPermissions.contains("ATTENDANCE_VIEW_OWN");
            }
            if (userPermissions.contains("ATTENDANCE_VIEW_ALL")) {
                return true;
            }
            if (!userPermissions.contains("ATTENDANCE_VIEW_DEPARTMENT")
                    || currentUser.getDepartmentId() == null) {
                return false;
            }

            User requestedUser = userDAO.findById(requestedUserId);
            return requestedUser != null
                    && requestedUser.getDepartmentId() != null
                    && requestedUser.getDepartmentId().equals(currentUser.getDepartmentId());
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
