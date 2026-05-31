package controller.admin;

import dao.PositionDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/position/toggle-status")
public class ChangePositionStatusServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String idParam = request.getParameter("id");
        String actionParam = request.getParameter("action");

        if (idParam == null || actionParam == null) {
            response.sendRedirect(request.getContextPath() + "/position/list");
            return;
        }

        try {
            int positionId = Integer.parseInt(idParam);

            boolean newStatus = "activate".equalsIgnoreCase(actionParam);

            PositionDAO dao = new PositionDAO();
            boolean isUpdated = dao.updatePositionStatus(positionId, newStatus);

            if (isUpdated) {
                session.setAttribute("message", "Status changed successfully!");
            } else {
                session.setAttribute("error", "Status changed successfully!");
            }

        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        response.sendRedirect(request.getContextPath() + "/position/list");
    }

}