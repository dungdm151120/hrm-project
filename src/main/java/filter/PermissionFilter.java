package filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
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


        String requiredPermission = resolveRequiredPermission(path, method);


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

        if (userPermissions == null || !userPermissions.contains(requiredPermission)) {

            res.setStatus(HttpServletResponse.SC_FORBIDDEN);
            req.setAttribute("permissionDenied", requiredPermission);
            req.getRequestDispatcher("/WEB-INF/views/common/403.jsp").forward(req, res);
            return;
        }

        chain.doFilter(request, response);
    }


    private String resolveRequiredPermission(String path, String method) {


        if (path.equals("/login"))           return null; // public
        if (path.equals("/logout"))          return null; // public
        if (path.equals("/forgot-password")) return null; // public
        if (path.equals("/reset-password"))  return null; // public
        if (path.equals("/home"))            return "HOMEPAGE_VIEW";
        if (path.equals("/profile"))         return "PROFILE_VIEW";
        if (path.equals("/change-password")) return "PROFILE_CHANGE_PASSWORD";
        if (path.equals("/user_list") && "GET".equals(method))
            return "USER_VIEW_LIST";
        if (path.equals("/user_detail") && "GET".equals(method))
            return "USER_VIEW_DETAIL";
        if (path.equals("/admin/users/add"))
            return "USER_CREATE";
        if (path.equals("/users/toggle-status") && "POST".equals(method))
            return "USER_TOGGLE_STATUS";
        if (path.equals("/users/update"))
            return "USER_UPDATE";
        if (path.equals("/admin/roles") && "GET".equals(method))
            return "ROLE_VIEW_LIST";
        if (path.equals("/admin/roles/permissions") && "GET".equals(method))
            return "ROLE_VIEW_PERMISSION";
        if (path.equals("/admin/roles/update"))
            return "ROLE_UPDATE";
        if (path.equals("/admin/roles/toggle_status") && "POST".equals(method))
            return "ROLE_TOGGLE_STATUS";
        if (path.equals("/admin/roles/edit_permissions"))
            return "ROLE_EDIT_PERMISSION";
        return null;
    }
}
