package controller.role;

import dao.PermissionDAO;
import dao.RoleDAO;
import model.Permission;
import model.Role;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/admin/roles/edit_permissions")
public class EditRolePermissionServlet extends HttpServlet {

    private final RoleDAO roleDAO = new RoleDAO();
    private final PermissionDAO permissionDAO = new PermissionDAO();

    // GET: hiển thị form chỉnh sửa permissions
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

        List<Permission> allPermissions = permissionDAO.getAllPermissions();
        List<Integer> assignedPermissionIds = roleDAO.getPermissionIdsByRoleId(roleId);

        request.setAttribute("role", role);
        request.setAttribute("allPermissions", allPermissions);
        request.setAttribute("assignedPermissionIds", assignedPermissionIds);

        request.getRequestDispatcher("/WEB-INF/views/role/edit_role_permission.jsp")
                .forward(request, response);
    }

    // POST: lưu permissions đã chọn
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

        // Lấy danh sách permission được chọn từ form (checkbox)
        String[] selectedIds = request.getParameterValues("permissionIds");
        List<Integer> permissionIds = new ArrayList<>();
        if (selectedIds != null) {
            for (String s : selectedIds) {
                try {
                    permissionIds.add(Integer.parseInt(s));
                } catch (NumberFormatException ignored) {}
            }
        }

        // Xoá cũ -> thêm mới
        roleDAO.deleteRolePermissions(roleId);
        roleDAO.insertRolePermissions(roleId, permissionIds);

        response.sendRedirect(request.getContextPath()
                + "/admin/roles/permissions?roleId=" + roleId
                + "&success=Edit successfully");

    }
}
