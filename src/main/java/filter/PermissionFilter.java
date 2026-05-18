package filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Set;

/**
 * PermissionFilter: Kiểm tra quyền truy cập dựa trên permission của role.
 *
 * Mỗi URL được map sang một permission code cụ thể.
 * Danh sách permission codes của user được load vào session sau khi đăng nhập
 * dưới dạng Set<String> với key "userPermissions".
 *
 * Thứ tự filter trong web.xml: AuthFilter -> AdminFilter -> PermissionFilter
 */
@WebFilter("/*")
public class PermissionFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String path = req.getServletPath();
        String method = req.getMethod();

        // Lấy permission code yêu cầu cho URL + method này
        String requiredPermission = resolveRequiredPermission(path, method);

        // Nếu URL không yêu cầu permission cụ thể -> cho đi qua
        if (requiredPermission == null) {
            chain.doFilter(request, response);
            return;
        }

        // Lấy danh sách permissions của user từ session
        HttpSession session = req.getSession(false);
        if (session == null) {
            res.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        @SuppressWarnings("unchecked")
        Set<String> userPermissions = (Set<String>) session.getAttribute("userPermissions");

        if (userPermissions == null || !userPermissions.contains(requiredPermission)) {
            // Không có quyền -> trả về trang 403
            res.setStatus(HttpServletResponse.SC_FORBIDDEN);
            req.setAttribute("permissionDenied", requiredPermission);
            req.getRequestDispatcher("/WEB-INF/views/common/403.jsp").forward(req, res);
            return;
        }

        chain.doFilter(request, response);
    }

    /**
     * Map từng URL + HTTP method sang permission code tương ứng.
     *
     * Permission codes (định nghĩa trong database):
     *   HOMEPAGE_VIEW, AUTH_LOGIN, AUTH_LOGOUT, AUTH_FORGOT_PASSWORD,
     *   PROFILE_VIEW, PROFILE_CHANGE_PASSWORD,
     *   USER_VIEW_LIST, USER_VIEW_DETAIL, USER_CREATE, USER_TOGGLE_STATUS, USER_UPDATE,
     *   ROLE_VIEW_LIST, ROLE_VIEW_PERMISSION, ROLE_UPDATE, ROLE_TOGGLE_STATUS, ROLE_EDIT_PERMISSION
     *
     * Lưu ý URL mapping thực tế của các Servlet:
     *   UserListServlet   -> /user_list
     *   UserDetailServlet -> /user_detail
     *   AddUserServlet    -> /admin/users/add
     *   UpdateUserServlet -> /users/update
     *   ChangeUserStatus  -> /users/toggle-status
     *
     * @return permission code yêu cầu, hoặc null nếu URL không cần kiểm tra permission
     */
    private String resolveRequiredPermission(String path, String method) {

        // ===== AUTH =====
        if (path.equals("/login"))           return null; // public
        if (path.equals("/logout"))          return null; // public
        if (path.equals("/forgot-password")) return null; // public
        if (path.equals("/reset-password"))  return null; // public

        // ===== HOMEPAGE =====
        if (path.equals("/home"))            return "HOMEPAGE_VIEW";

        // ===== PROFILE =====
        if (path.equals("/profile"))         return "PROFILE_VIEW";
        if (path.equals("/change-password")) return "PROFILE_CHANGE_PASSWORD";

        // ===== USER MANAGEMENT =====
        // GET /user_list             -> xem danh sách user
        // [SỬA LỖI] Sửa từ "/admin/users" thành "/user_list" để khớp với
        // @WebServlet("/user_list") trong UserListServlet và redirect sau AddUser
        if (path.equals("/user_list") && "GET".equals(method))
            return "USER_VIEW_LIST";

        // GET /user_detail           -> xem chi tiết user
        // [SỬA LỖI] Sửa từ "/admin/user_detail" thành "/user_detail" để khớp
        // với @WebServlet("/user_detail") trong UserDetailServlet
        if (path.equals("/user_detail") && "GET".equals(method))
            return "USER_VIEW_DETAIL";

        // GET /admin/users/add       -> form thêm user
        // POST /admin/users/add      -> lưu user mới
        if (path.equals("/admin/users/add"))
            return "USER_CREATE";

        // POST /admin/users/toggle_status -> bật/tắt trạng thái user
        if (path.equals("/users/toggle-status") && "POST".equals(method))
            return "USER_TOGGLE_STATUS";

        // GET  /admin/users/update   -> form cập nhật user
        // POST /admin/users/update   -> lưu cập nhật user
        if (path.equals("/users/update"))
            return "USER_UPDATE";

        // ===== ROLE MANAGEMENT =====
        // GET /admin/roles           -> xem danh sách role
        if (path.equals("/admin/roles") && "GET".equals(method))
            return "ROLE_VIEW_LIST";

        // GET /admin/roles/permissions -> xem permissions của role
        if (path.equals("/admin/roles/permissions") && "GET".equals(method))
            return "ROLE_VIEW_PERMISSION";

        // GET  /admin/roles/update   -> form cập nhật thông tin role
        // POST /admin/roles/update   -> lưu cập nhật thông tin role
        if (path.equals("/admin/roles/update"))
            return "ROLE_UPDATE";

        // POST /admin/roles/toggle_status -> bật/tắt trạng thái role
        if (path.equals("/admin/roles/toggle_status") && "POST".equals(method))
            return "ROLE_TOGGLE_STATUS";

        // GET  /admin/roles/edit_permissions -> form chỉnh sửa permissions của role
        // POST /admin/roles/edit_permissions -> lưu permissions của role
        if (path.equals("/admin/roles/edit_permissions"))
            return "ROLE_EDIT_PERMISSION";

        // Các URL khác không yêu cầu permission cụ thể (tài nguyên tĩnh, v.v.)
        return null;
    }
}
