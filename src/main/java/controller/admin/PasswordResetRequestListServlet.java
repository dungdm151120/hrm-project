package controller.admin;

import dao.PasswordResetRequestDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/admin/password-reset-requests")
public class PasswordResetRequestListServlet extends HttpServlet {
    private final PasswordResetRequestDAO requestDAO = new PasswordResetRequestDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("resetRequests", requestDAO.findAll());
        request.getRequestDispatcher("/WEB-INF/views/admin/password_reset_requests.jsp")
                .forward(request, response);
    }
}
