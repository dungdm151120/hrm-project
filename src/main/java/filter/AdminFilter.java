package filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * AdminFilter: Kiểm tra người dùng có phải ADMIN không.
 * Được áp dụng cho tất cả URL bắt đầu bằng /admin/*
 * (Lưu ý: PermissionFilter sẽ kiểm tra permission chi tiết hơn sau filter này)
 */
@WebFilter("/admin/*")
public class AdminFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        HttpSession session = req.getSession(false);

        if (session == null || session.getAttribute("currentUser") == null) {
            res.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String roleName = (String) session.getAttribute("roleName");

        if (!"ADMIN".equals(roleName)) {
            res.sendRedirect(req.getContextPath() + "/home");
            return;
        }

        chain.doFilter(request, response);
    }
}
