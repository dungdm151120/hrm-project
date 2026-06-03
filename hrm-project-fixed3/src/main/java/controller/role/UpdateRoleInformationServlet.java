package controller.role;

import dao.RoleDAO;
import model.Role;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/admin/roles/update")
public class UpdateRoleInformationServlet extends HttpServlet {

    private final RoleDAO roleDAO = new RoleDAO();

    // GET: Load role hiện tại lên form
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idParam = request.getParameter("roleId");
        if (idParam == null || idParam.trim().isEmpty()) {
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

        request.setAttribute("role", role);
        request.getRequestDispatcher("/WEB-INF/views/role/update_role.jsp")
                .forward(request, response);
    }

    // POST: Xử lý cập nhật thông tin role
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idParam = request.getParameter("roleId");
        String name = request.getParameter("name");
        String description = request.getParameter("description");

        // Validate roleId
        if (idParam == null || idParam.trim().isEmpty()) {
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

        // Validate name
        if (name == null || name.trim().isEmpty()) {
            Role role = roleDAO.getRoleById(roleId);
            request.setAttribute("role", role);
            request.setAttribute("error", "Tên role không được để trống.");
            request.getRequestDispatcher("/WEB-INF/views/role/update_role.jsp")
                    .forward(request, response);
            return;
        }

        name = name.trim().toUpperCase();
        description = (description != null) ? description.trim() : "";

        boolean success = roleDAO.updateRoleInfo(roleId, name, description);

        if (success) {
            response.sendRedirect(request.getContextPath()
                    + "/admin/roles");
        } else {
            Role role = roleDAO.getRoleById(roleId);
            request.setAttribute("role", role);
            request.setAttribute("error", "Cập nhật thất bại. Tên role có thể đã tồn tại.");
            request.getRequestDispatcher("/WEB-INF/views/role/update_role.jsp")
                    .forward(request, response);
        }
    }
}
