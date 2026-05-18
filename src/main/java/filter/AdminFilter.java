package filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * AdminFilter: Kiểm tra người dùng đã đăng nhập trước khi truy cập /admin/*.
 *
 * [SỬA LỖI] Đã bỏ kiểm tra role cứng "ADMIN". Trước đây filter này chặn
 * toàn bộ non-ADMIN khỏi /admin/*, khiến HR (có permission USER_CREATE
 * trong database) không thể truy cập /admin/users/add dù được cấp quyền.
 *
 * Trách nhiệm phân quyền chi tiết (dựa trên permission) được chuyển hoàn
 * toàn sang PermissionFilter. AdminFilter chỉ đảm bảo user đã xác thực.
 *
 * Thứ tự filter: AuthFilter -> AdminFilter -> PermissionFilter
 */
@WebFilter("/admin/*")
public class AdminFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        HttpSession session = req.getSession(false);

        // Chỉ kiểm tra đã đăng nhập, không kiểm tra role cứng
        if (session == null || session.getAttribute("currentUser") == null) {
            res.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        // Để PermissionFilter xử lý kiểm tra quyền chi tiết
        chain.doFilter(request, response);
    }
}
