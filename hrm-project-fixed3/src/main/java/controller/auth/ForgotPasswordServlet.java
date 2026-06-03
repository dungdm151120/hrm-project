package controller.auth;

import dao.PasswordResetRequestDAO;
import dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.User;

import java.io.IOException;

@WebServlet("/forgot-password")
public class ForgotPasswordServlet extends HttpServlet {
    private final UserDAO userDAO = new UserDAO();
    private final PasswordResetRequestDAO requestDAO = new PasswordResetRequestDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/auth/forgot_password.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String email = request.getParameter("email");
        String reason = request.getParameter("reason");

        if (email == null || email.trim().isEmpty()) {
            request.setAttribute("error", "Email is required.");
            request.getRequestDispatcher("/WEB-INF/views/auth/forgot_password.jsp")
                    .forward(request, response);
            return;
        }

        User user = userDAO.findByEmail(email.trim());

        if (user == null || !user.isActive()) {
            request.setAttribute("error", "Active account with this email was not found.");
            request.getRequestDispatcher("/WEB-INF/views/auth/forgot_password.jsp")
                    .forward(request, response);
            return;
        }

        if (requestDAO.hasPendingRequest(user.getId())) {
            request.setAttribute("error", "A pending reset request already exists for this account.");
            request.getRequestDispatcher("/WEB-INF/views/auth/forgot_password.jsp")
                    .forward(request, response);
            return;
        }

        boolean created = requestDAO.createRequest(user.getId(), user.getEmail(), reason);

        if (created) {
            request.setAttribute("success", "Your password reset request has been sent to admin.");
        } else {
            request.setAttribute("error", "Cannot create password reset request.");
        }

        request.getRequestDispatcher("/WEB-INF/views/auth/forgot_password.jsp")
                .forward(request, response);
    }
}
