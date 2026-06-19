package controller.role;

import dao.PermissionDAO;
import dao.RoleDAO;
import model.Permission;
import model.Role;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.*;

@WebServlet("/admin/roles/edit_permissions")
public class EditRolePermissionServlet extends HttpServlet {

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

        // Chặn sửa permissions của BUSINESS ADMIN
        if ("BUSINESS ADMIN".equals(role.getName())) {
            response.sendRedirect(request.getContextPath() + "/admin/roles?error=Cannot modify permissions of BUSINESS ADMIN");
            return;
        }

        List<Permission> allPermissions = permissionDAO.getAllPermissions();
        List<Integer> assignedPermissionIds = roleDAO.getPermissionIdsByRoleId(roleId);

        Map<String, List<Permission>> moduleMap = new LinkedHashMap<>();
        String[] moduleOrder = {
                "HOMEPAGE", "PROFILE", "AUTH", "USER", "ROLE", "DEPARTMENT", "POSITION",
                "CONTRACT", "ATTENDANCE", "PAYROLL", "ANNOUNCEMENT", "REQUEST", "TASK"
        };
        for (String mod : moduleOrder) {
            moduleMap.put(mod, new ArrayList<>());
        }

        for (Permission p : allPermissions) {
            String code = p.getCode();
            if (code.contains("REQUEST")) {
                moduleMap.get("REQUEST").add(p);
                continue;
            }
            int idx = code.indexOf('_');
            if (idx > 0) {
                String prefix = code.substring(0, idx);
                if (moduleMap.containsKey(prefix)) {
                    moduleMap.get(prefix).add(p);
                } else {
                    moduleMap.computeIfAbsent(prefix, k -> new ArrayList<>()).add(p);
                }
            }
        }

        request.setAttribute("role", role);
        request.setAttribute("allPermissions", allPermissions);
        request.setAttribute("assignedPermissionIds", assignedPermissionIds);
        request.setAttribute("moduleMap", moduleMap);

        request.getRequestDispatcher("/WEB-INF/views/role/edit_role_permission.jsp")
                .forward(request, response);
    }

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

        // Chặn sửa permissions của BUSINESS ADMIN
        if ("BUSINESS ADMIN".equals(role.getName())) {
            response.sendRedirect(request.getContextPath() + "/admin/roles?error=Cannot modify permissions of BUSINESS ADMIN");
            return;
        }

        String[] selectedIds = request.getParameterValues("permissionIds");
        List<Integer> permissionIds = new ArrayList<>();
        if (selectedIds != null) {
            for (String s : selectedIds) {
                try {
                    permissionIds.add(Integer.parseInt(s));
                } catch (NumberFormatException ignored) {}
            }
        }

        roleDAO.deleteRolePermissions(roleId);
        roleDAO.insertRolePermissions(roleId, permissionIds);

        response.sendRedirect(request.getContextPath()
                + "/admin/roles/permissions?roleId=" + roleId
                + "&success=Edit successfully");
    }
}