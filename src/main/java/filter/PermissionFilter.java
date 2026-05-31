package filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Arrays;
import java.util.Set;


@WebFilter("/*")
public class PermissionFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String path = req.getServletPath();
        String method = req.getMethod();

        String[] requiredPermissions = resolveRequiredPermissions(path, method);

        if (requiredPermissions.length == 0) {
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

        if (userPermissions == null || Arrays.stream(requiredPermissions).noneMatch(userPermissions::contains)) {
            res.setStatus(HttpServletResponse.SC_FORBIDDEN);
            req.setAttribute("permissionDenied", String.join(" or ", requiredPermissions));
            req.getRequestDispatcher("/WEB-INF/views/common/403.jsp").forward(req, res);
            return;
        }

        chain.doFilter(request, response);
    }


    private String[] resolveRequiredPermissions(String path, String method) {
        // Public
        if (path.equals("/login") || path.equals("/logout") || path.equals("/forgot-password") || path.equals("/reset-password")) {
            return noPermissionRequired();
        }

        // Home
        if (path.equals("/home")) return permissions("HOMEPAGE_VIEW");

        // Profile
        if (path.equals("/profile")) return permissions("PROFILE_VIEW");
        if (path.equals("/change-password")) return permissions("PROFILE_CHANGE_PASSWORD");

        // User
        if (path.equals("/user_list") && "GET".equals(method)) return permissions("USER_VIEW_LIST");
        if (path.equals("/user_detail") && "GET".equals(method)) return permissions("USER_VIEW_DETAIL");
        if (path.equals("/admin/users/add")) return permissions("USER_CREATE");
        if (path.equals("/users/update")) return permissions("USER_UPDATE");
        if (path.equals("/users/toggle-status") && "GET".equals(method)) return permissions("USER_TOGGLE_STATUS");
        if (path.equals("/admin/password-reset-requests") && "GET".equals(method)) return permissions("USER_VIEW_LIST");
        if (path.equals("/admin/password-reset/approve") && "POST".equals(method)) return permissions("USER_UPDATE");
        if (path.equals("/admin/password-reset/reject") && "POST".equals(method)) return permissions("USER_UPDATE");

        // Role
        if (path.equals("/admin/roles") && "GET".equals(method)) return permissions("ROLE_VIEW_LIST");
        if (path.equals("/admin/roles/permissions") && "GET".equals(method)) return permissions("ROLE_VIEW_PERMISSION");
        if (path.equals("/admin/roles/update")) return permissions("ROLE_UPDATE");
        if (path.equals("/admin/roles/toggle_status") && "POST".equals(method)) return permissions("ROLE_TOGGLE_STATUS");
        if (path.equals("/admin/roles/edit_permissions")) return permissions("ROLE_EDIT_PERMISSION");
        if (path.equals("/admin/roles/add")) return permissions("ROLE_CREATE");

        // Department
        if (path.equals("/admin/departments") && "GET".equals(method)) return permissions("DEPARTMENT_VIEW_LIST");
        if (path.equals("/admin/departments/detail") && "GET".equals(method)) return permissions("DEPARTMENT_VIEW_DETAIL");
        if (path.equals("/admin/departments/add")) return permissions("DEPARTMENT_CREATE");
        if (path.equals("/admin/departments/update")) return permissions("DEPARTMENT_UPDATE");
        if (path.equals("/admin/departments/toggle-status") && "POST".equals(method)) return permissions("DEPARTMENT_TOGGLE_STATUS");
        if (path.equals("/admin/departments/assign-manager")) return permissions("DEPARTMENT_ASSIGN_MANAGER");
        if (path.equals("/admin/departments/employees")) return permissions("DEPARTMENT_VIEW_EMPLOYEES");
        if (path.equals("/add_member")) return permissions("DEPARTMENT_UPDATE");
        if (path.equals("/move_member")) return permissions("DEPARTMENT_UPDATE");
        if (path.equals("/remove_member") && "GET".equals(method)) return permissions("DEPARTMENT_UPDATE");

        // Position
        if (path.equals("/position/list") && "GET".equals(method)) return permissions("POSITION_VIEW_LIST");
        if (path.equals("/position/add")) return permissions("POSITION_CREATE");
        if (path.equals("/position/update")) return permissions("POSITION_UPDATE");
        if (path.equals("/position/toggle-status") && "GET".equals(method)) return permissions("POSITION_TOGGLE_STATUS");

        // Contract
        if (path.equals("/contracts") && "GET".equals(method)) return permissions("CONTRACT_VIEW_OWN", "CONTRACT_VIEW_LIST");
        if (path.equals("/contracts/detail") && "GET".equals(method)) return permissions("CONTRACT_VIEW_OWN", "CONTRACT_VIEW_DETAIL");
        if (path.equals("/contracts/add")) return permissions("CONTRACT_CREATE");
        if (path.equals("/contracts/update")) return permissions("CONTRACT_UPDATE");
        if (path.equals("/contracts/terminate") && "POST".equals(method)) return permissions("CONTRACT_TERMINATE");

        // Attendance
        if (path.equals("/attendance/check-in") && "POST".equals(method)) return permissions("ATTENDANCE_CHECK_IN");
        if (path.equals("/attendance/check-out") && "POST".equals(method)) return permissions("ATTENDANCE_CHECK_OUT");
        if (path.equals("/attendance/my") && "GET".equals(method)) return permissions("ATTENDANCE_VIEW_OWN");
        if (path.equals("/attendance/department") && "GET".equals(method)) return permissions("ATTENDANCE_VIEW_DEPARTMENT");
        if (path.equals("/attendance/all") && "GET".equals(method)) return permissions("ATTENDANCE_VIEW_ALL");
        if (path.equals("/attendance/update")) return permissions("ATTENDANCE_UPDATE");
        if (path.equals("/attendance/export")) return permissions("ATTENDANCE_EXPORT_REPORT");

        // Payroll
        if (path.equals("/payroll/my") && "GET".equals(method)) return permissions("PAYROLL_VIEW_OWN");
        if (path.equals("/payroll/list") && "GET".equals(method)) return permissions("PAYROLL_VIEW_LIST");
        if (path.equals("/payroll/detail") && "GET".equals(method)) return permissions("PAYROLL_VIEW_DETAIL");
        if (path.equals("/payroll/generate")) return permissions("PAYROLL_GENERATE");
        if (path.equals("/payroll/update-component")) return permissions("PAYROLL_UPDATE_COMPONENT");
        if (path.equals("/payroll/confirm")) return permissions("PAYROLL_CONFIRM");
        if (path.equals("/payroll/export")) return permissions("PAYROLL_EXPORT_REPORT");

        return noPermissionRequired();
    }

    private String[] permissions(String... permissionCodes) {
        return permissionCodes;
    }

    private String[] noPermissionRequired() {
        return new String[0];
    }
}
