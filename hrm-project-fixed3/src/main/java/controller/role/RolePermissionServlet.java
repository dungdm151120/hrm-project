package controller.role;

import dao.PermissionDAO;
import dao.RoleDAO;
import model.Permission;
import model.Role;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/admin/roles/permissions")
public class RolePermissionServlet extends HttpServlet {

    private final RoleDAO roleDAO = new RoleDAO();
    private final PermissionDAO permissionDAO = new PermissionDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
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

        List<Permission> rolePermissions = permissionDAO.getPermissionsByRoleId(roleId);

        request.setAttribute("role", role);
        request.setAttribute("rolePermissions", rolePermissions);

        request.getRequestDispatcher("/WEB-INF/views/role/role_permission.jsp")
                .forward(request, response);
    }
}
