package controller.admin;

import dao.PasswordResetRequestDAO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/admin/password-reset/reject")
public class RejectPasswordResetServlet extends HttpServlet {
    private final PasswordResetRequestDAO requestDAO = new PasswordResetRequestDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        Integer adminId = getCurrentUserId(request);

        if (adminId == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        int requestId = Integer.parseInt(request.getParameter("id"));
        String adminNote = request.getParameter("adminNote");
        requestDAO.reject(requestId, adminId, adminNote);

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
