package controller.profile;

import service.UserService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;

@WebServlet("/change_password")
public class ChangePasswordServlet extends HttpServlet {

    private final UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        request.getRequestDispatcher("/WEB-INF/views/profile/change_password.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        int userId = (int) session.getAttribute("userId");
        Map<String, String> passwordErrors = userService.changePassword(
                userId,
                request.getParameter("oldPassword"),
                request.getParameter("newPassword"),
                request.getParameter("confirmPassword"));

        if (!passwordErrors.isEmpty()) {
            request.setAttribute("passwordErrors", passwordErrors);
            request.getRequestDispatcher("/WEB-INF/views/profile/change_password.jsp")
                    .forward(request, response);
            return;
        }

        session.setAttribute("profileSuccess", "Password changed successfully.");
        response.sendRedirect(request.getContextPath() + "/profile");
    }
}
