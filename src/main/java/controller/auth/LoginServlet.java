package controller.auth;

import dao.PermissionDAO;
import model.Permission;
import model.User;
import service.AuthService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@WebServlet("/login")
public class   LoginServlet extends HttpServlet {

    private final AuthService authService = new AuthService();
    private final PermissionDAO permissionDAO = new PermissionDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        User user = authService.login(email, password);

        if (user != null) {
            HttpSession session = request.getSession();

            session.setAttribute("currentUser", user);
            session.setAttribute("userId", user.getId());
            session.setAttribute("roleName", user.getRoleName());
            session.setAttribute("fullName", user.getFullName());
            session.setAttribute("departmentId", user.getDepartmentId());
            session.setAttribute("role", user.getRoleName());


            List<Permission> permissions = permissionDAO.getPermissionsByRoleId(user.getRoleId());
            Set<String> permissionCodes = new HashSet<>();
            for (Permission p : permissions) {
                permissionCodes.add(p.getCode());
            }
            session.setAttribute("userPermissions", permissionCodes);

            response.sendRedirect(request.getContextPath() + "/home");
        } else {
            request.setAttribute("error", "Email hoặc mật khẩu không đúng, hoặc tài khoản đã bị khóa");
            request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp")
                    .forward(request, response);
        }
    }
}
