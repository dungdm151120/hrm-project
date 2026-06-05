package controller.admin;

import dao.LaborContractDAO;
import dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/users/toggle-status")
public class ChangeUserStatusServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String idParam = req.getParameter("id");
        String actionParam = req.getParameter("action");

        if (idParam == null || actionParam == null) {
            resp.sendRedirect(req.getContextPath() + "/user_list");
            return;
        }

        try {
            int userId = Integer.parseInt(idParam);

            boolean newStatus = "activate".equalsIgnoreCase(actionParam);

            if (!newStatus) {
                LaborContractDAO contractDAO = new LaborContractDAO();
                if (!contractDAO.canDeactivateUser(userId)) {
                    session.setAttribute("error", "User can only be deactivated when their contract is expired or terminated.");
                    resp.sendRedirect(req.getContextPath() + "/user_list");
                    return;
                }
            }

            UserDAO dao = new UserDAO();
            boolean isUpdated = dao.updateUserStatus(userId, newStatus);

            if (isUpdated) {
                session.setAttribute("message", "Status changed successfully!");
            } else {
                session.setAttribute("error", "Status change failed!");
            }

        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        resp.sendRedirect(req.getContextPath() + "/user_list");
    }
}
