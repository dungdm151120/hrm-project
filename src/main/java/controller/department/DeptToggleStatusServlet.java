package controller.department;

import dao.DepartmentDAO;
import dao.UserDAO;
import model.Department;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import model.User;

import java.io.IOException;
import java.util.List;

@WebServlet("/admin/departments/toggle-status")
public class DeptToggleStatusServlet extends HttpServlet {

    private final DepartmentDAO departmentDAO = new DepartmentDAO();
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idParam = request.getParameter("id");
        String redirectURL = request.getContextPath() + "/admin/departments";

        if (idParam == null || idParam.trim().isEmpty()) {
            response.sendRedirect(redirectURL + "?error=Missing department ID");
            return;
        }

        int id;
        try {
            id = Integer.parseInt(idParam);
        } catch (NumberFormatException e) {
            response.sendRedirect(redirectURL + "?error=Invalid department ID");
            return;
        }

        Department dept = departmentDAO.getDepartmentById(id);
        if (dept == null) {
            response.sendRedirect(redirectURL + "?error=Department not found");
            return;
        }

        boolean newStatus = !dept.isActive();

        if (!newStatus) {
            int memberCount = userDAO.countUsersByDepartment(id);
            Integer managerId = dept.getManagerUserId();

            if (memberCount > 1 || (memberCount == 1 && (managerId == null || !isUserTheOnlyMember(id, managerId)))) {
                response.sendRedirect(redirectURL + "?error=Cannot deactivate department. It must be empty or have only the department manager.");
                return;
            }
        }

        boolean updated = departmentDAO.toggleStatus(id, newStatus);

        if (updated) {
            if (!newStatus) {
                Integer managerId = dept.getManagerUserId();
                departmentDAO.removeManager(id);
                if (managerId != null) {
                    userDAO.clearDepartmentAndPosition(managerId);
                }
            }
            String msg = newStatus
                    ? "Department activated successfully"
                    : "Department deactivated successfully. Manager cleared and removed from department.";
            response.sendRedirect(redirectURL + "?success=" + java.net.URLEncoder.encode(msg, "UTF-8"));
        } else {
            response.sendRedirect(redirectURL + "?error=Toggle failed");
        }
    }

    private boolean isUserTheOnlyMember(int departmentId, int userId) {
        List<User> users = userDAO.findUsersByDeptId(departmentId);
        return users.size() == 1 && users.get(0).getId() == userId;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + "/admin/departments?error=Invalid request method");
    }
}