package controller.admin;

import dao.PasswordResetRequestDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.PasswordResetRequest;
import service.PasswordResetService;
import util.EmailUtil;
import util.PasswordUtil;

import java.io.IOException;

@WebServlet("/admin/password-reset/approve")
public class ApprovePasswordResetServlet extends HttpServlet {
    private final PasswordResetRequestDAO requestDAO = new PasswordResetRequestDAO();
    private final PasswordResetService passwordResetService = new PasswordResetService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Integer adminId = getCurrentUserId(request);

        if (adminId == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        Integer requestId = parseRequestId(request.getParameter("id"));
        if (requestId == null) {
            setFlash(request, "resetError", "Invalid password reset request.");
            redirectToList(request, response);
            return;
        }

        PasswordResetRequest resetRequest = requestDAO.findById(requestId);

        if (resetRequest == null || !"PENDING".equals(resetRequest.getStatus())) {
            setFlash(request, "resetError", "Password reset request is missing or has already been handled.");
            redirectToList(request, response);
            return;
        }

        String newPassword = PasswordUtil.generateRandomPassword(10);
        boolean mailSent = EmailUtil.sendResetPasswordEmail(resetRequest.getEmail(), newPassword);

        if (!mailSent) {
            setFlash(request, "resetError",
                    "Cannot send reset password email. Password was not changed; the request remains pending.");
            redirectToList(request, response);
            return;
        }

        String passwordHash = PasswordUtil.hashPassword(newPassword);
        boolean completed = passwordResetService.completeReset(
                requestId,
                resetRequest.getUserId(),
                passwordHash,
                adminId,
                "Password reset email sent."
        );

        if (completed) {
            setFlash(request, "resetSuccess", "Password reset successfully and the email was sent.");
        } else {
            setFlash(request, "resetError",
                    "The email was sent, but the password reset could not be completed. Please process the request again.");
        }

        redirectToList(request, response);
    }

    private Integer getCurrentUserId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("userId") == null) {
            return null;
        }

        return (Integer) session.getAttribute("userId");
    }

    private Integer parseRequestId(String value) {
        try {
            int id = Integer.parseInt(value);
            return id > 0 ? id : null;
        } catch (Exception e) {
            return null;
        }
    }

    private void setFlash(HttpServletRequest request, String name, String message) {
        request.getSession().setAttribute(name, message);
    }

    private void redirectToList(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.sendRedirect(request.getContextPath() + "/admin/password-reset-requests");
    }
}
