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
import java.util.regex.Pattern;

@WebServlet("/forgot-password")
public class ForgotPasswordServlet extends HttpServlet {
    private static final int MAX_EMAIL_LENGTH = 100;
    private static final int MAX_REASON_LENGTH = 255;
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

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

        email = email == null ? "" : email.trim().toLowerCase();
        reason = reason == null ? "" : reason.trim();

        request.setAttribute("email", email);
        request.setAttribute("reason", reason);

        if (email.isEmpty()) {
            forwardWithError(request, response, "Email is required.");
            return;
        }

        if (email.length() > MAX_EMAIL_LENGTH || !EMAIL_PATTERN.matcher(email).matches()) {
            forwardWithError(request, response, "Invalid email address.");
            return;
        }

        if (reason.length() > MAX_REASON_LENGTH) {
            forwardWithError(request, response, "Reason must not exceed 255 characters.");
            return;
        }

        User user = userDAO.findByEmail(email);

        if (user == null || !user.isActive()) {
            forwardWithError(request, response, "Active account with this email was not found.");
            return;
        }

        if (requestDAO.hasPendingRequest(user.getId())) {
            forwardWithError(request, response, "A pending reset request already exists for this account.");
            return;
        }

        boolean created = requestDAO.createRequest(
                user.getId(),
                user.getEmail(),
                reason.isEmpty() ? null : reason
        );

        if (created) {
            request.setAttribute("success", "Your password reset request has been sent to admin.");
            request.setAttribute("email", "");
            request.setAttribute("reason", "");
        } else {
            request.setAttribute("error", "Cannot create password reset request.");
        }

        request.getRequestDispatcher("/WEB-INF/views/auth/forgot_password.jsp")
                .forward(request, response);
    }

    private void forwardWithError(HttpServletRequest request, HttpServletResponse response, String message)
            throws ServletException, IOException {
        request.setAttribute("error", message);
        request.getRequestDispatcher("/WEB-INF/views/auth/forgot_password.jsp")
                .forward(request, response);
    }
}
