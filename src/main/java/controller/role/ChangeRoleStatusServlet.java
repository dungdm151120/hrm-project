package controller.role;

import dao.RoleDAO;
import dao.UserDAO;
import model.Role;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/admin/roles/toggle_status")
public class ChangeRoleStatusServlet extends HttpServlet {

    private final RoleDAO roleDAO = new RoleDAO();
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idParam = request.getParameter("roleId");
        String redirectURL = request.getContextPath() + "/admin/roles";

        if (idParam == null || idParam.isEmpty()) {
            response.sendRedirect(redirectURL);
            return;
        }

        int roleId;
        try {
            roleId = Integer.parseInt(idParam);
        } catch (NumberFormatException e) {
            response.sendRedirect(redirectURL);
            return;
        }

        Role role = roleDAO.getRoleById(roleId);
        if (role == null) {
            response.sendRedirect(redirectURL);
            return;
        }

        boolean newStatus = !role.isActive();

        if (!newStatus) {
            int userCount = userDAO.countUsersByRole(roleId);
            if (userCount > 0) {
                response.sendRedirect(redirectURL + "?error=Cannot deactivate role with assigned users");
                return;
            }
        }

        roleDAO.toggleStatus(roleId, newStatus);
        response.sendRedirect(redirectURL + "?success=Role status changed successfully");
    }
}