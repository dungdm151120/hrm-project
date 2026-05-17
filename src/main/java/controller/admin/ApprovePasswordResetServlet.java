package controller.admin;

import dao.PasswordResetRequestDAO;
import dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.PasswordResetRequest;
import util.EmailUtil;
import util.PasswordUtil;

import java.io.IOException;

@WebServlet("/admin/password-reset/approve")
public class ApprovePasswordResetServlet extends HttpServlet {
    private final PasswordResetRequestDAO requestDAO = new PasswordResetRequestDAO();
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Integer adminId = getCurrentUserId(request);

        if (adminId == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        int requestId = Integer.parseInt(request.getParameter("id"));
        PasswordResetRequest resetRequest = requestDAO.findById(requestId);

        if (resetRequest == null || !"PENDING".equals(resetRequest.getStatus())) {
            response.sendRedirect(request.getContextPath() + "/admin/password-reset-requests");
            return;
        }

        String newPassword = PasswordUtil.generateRandomPassword(10);
        boolean passwordUpdated = userDAO.updatePassword(resetRequest.getUserId(), newPassword);

        if (passwordUpdated) {
            boolean mailSent = EmailUtil.sendResetPasswordEmail(resetRequest.getEmail(), newPassword);
            String adminNote = mailSent ? "Password reset email sent." : "Password reset, but mail is not configured or failed.";
            requestDAO.markDone(requestId, newPassword, adminId, adminNote);
        }

        response.sendRedirect(request.getContextPath() + "/admin/password-reset-requests");
    }

    private Integer getCurrentUserId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("userId") == null) {
            return null;
        }

        return (Integer) session.getAttribute("userId");
    }
}
