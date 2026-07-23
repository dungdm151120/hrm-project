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
import java.util.regex.Pattern;

@WebServlet("/login")
public class   LoginServlet extends HttpServlet {

    private static final int MAX_EMAIL_LENGTH = 100;
    private static final int MAX_PASSWORD_LENGTH = 72;
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

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

        email = email == null ? "" : email.trim().toLowerCase();
        request.setAttribute("email", email);

        String validationError = validateLoginInput(email, password);
        if (validationError != null) {
            request.setAttribute("error", validationError);
            request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp")
                    .forward(request, response);
            return;
        }

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

    private String validateLoginInput(String email, String password) {
        if (email.isEmpty()) {
            return "Vui lòng nhập email.";
        }
        if (email.length() > MAX_EMAIL_LENGTH || !EMAIL_PATTERN.matcher(email).matches()) {
            return "Email không hợp lệ.";
        }
        if (password == null || password.isEmpty()) {
            return "Vui lòng nhập mật khẩu.";
        }
        if (password.length() > MAX_PASSWORD_LENGTH) {
            return "Mật khẩu không được vượt quá 72 ký tự.";
        }
        return null;
    }
}
