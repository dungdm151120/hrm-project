package controller.profile;

import model.User;
import service.UserService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/change-password")
public class ChangePasswordServlet extends HttpServlet {

    private final UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {


        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("currentUser");


        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }


        request.setAttribute("user", user);


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

        String oldPassword = request.getParameter("oldPassword");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        String error = userService.changePassword(userId, oldPassword, newPassword, confirmPassword);

        if (error != null) {
            request.setAttribute("error", error);
        } else {
            request.setAttribute("success", "Đổi mật khẩu thành công");
        }

        request.getRequestDispatcher("/WEB-INF/views/profile/change_password.jsp")
                .forward(request, response);
    }
}