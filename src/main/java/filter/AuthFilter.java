package filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;


@WebFilter("/*")
public class AuthFilter implements Filter {

    // Các URL không cần đăng nhập
    private static final String[] PUBLIC_URLS = {
            "/login",
            "/logout",
            "/forgot-password",
            "/reset-password"
    };

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String path = req.getServletPath();


        for (String pub : PUBLIC_URLS) {
            if (path.equals(pub)) {
                chain.doFilter(request, response);
                return;
            }
        }


        if (path.startsWith("/assets/") || path.startsWith("/static/")) {
            chain.doFilter(request, response);
            return;
        }


        HttpSession session = req.getSession(false);
        boolean loggedIn = (session != null && session.getAttribute("currentUser") != null);

        if (!loggedIn) {
            res.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        chain.doFilter(request, response);
    }
}
