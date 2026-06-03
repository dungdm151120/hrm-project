package controller.role;

import dao.RoleDAO;
import model.Role;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/admin/roles/toggle_status")
public class ChangeRoleStatusServlet extends HttpServlet {

    private final RoleDAO roleDAO = new RoleDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idParam = request.getParameter("roleId");
        if (idParam == null || idParam.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/admin/roles");
            return;
        }

        int roleId;
        try {
            roleId = Integer.parseInt(idParam);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/roles");
            return;
        }

        Role role = roleDAO.getRoleById(roleId);
        if (role == null) {
            response.sendRedirect(request.getContextPath() + "/admin/roles");
            return;
        }

        // Đổi ngược trạng thái hiện tại
        boolean newStatus = !role.isActive();
        roleDAO.toggleStatus(roleId, newStatus);

        response.sendRedirect(request.getContextPath() + "/admin/roles");
    }
}
