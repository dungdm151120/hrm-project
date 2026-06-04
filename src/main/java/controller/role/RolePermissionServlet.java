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
import java.util.stream.Collectors;

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

        List<Permission> allPermissions = permissionDAO.getAllPermissions();
        List<Permission> rolePermissions = permissionDAO.getPermissionsByRoleId(roleId);
        Set<String> assignedPermissionIds = rolePermissions.stream()
                .map(Permission::getCode)
                .collect(Collectors.toSet());

        // Nhóm permissions theo module
        Map<String, List<Permission>> moduleMap = new LinkedHashMap<>();
        String[] moduleOrder = {"HOMEPAGE","PROFILE","USER","ROLE","DEPARTMENT","POSITION","CONTRACT","ATTENDANCE","PAYROLL"};
        for (String mod : moduleOrder) {
            moduleMap.put(mod, new ArrayList<>());
        }
        for (Permission p : allPermissions) {
            String code = p.getCode();
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
        request.setAttribute("rolePermissions", rolePermissions);
        request.setAttribute("assignedPermissionIds", assignedPermissionIds);
        request.setAttribute("moduleMap", moduleMap);

        request.getRequestDispatcher("/WEB-INF/views/role/role_permission.jsp")
                .forward(request, response);
    }
}