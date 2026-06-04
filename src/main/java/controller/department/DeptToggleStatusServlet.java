package controller.department;

import dao.DepartmentDAO;
import dao.UserDAO;
import model.Department;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

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
        boolean updated = departmentDAO.toggleStatus(id, newStatus);

        if (updated) {
            if (!newStatus) {
                // Khi deactive phòng ban: đá toàn bộ user ra khỏi phòng ban (set department_id = NULL)
                userDAO.removeDepartmentFromUsers(id);
                // Đồng thời xóa manager của phòng ban (nếu có)
                departmentDAO.updateManager(id, 0); // hoặc dùng method set null, tôi sẽ thêm bên dưới
            }
            String msg = newStatus ? "Department activated successfully" : "Department deactivated successfully. All members have been removed from the department.";
            response.sendRedirect(redirectURL + "?success=" + java.net.URLEncoder.encode(msg, "UTF-8"));
        } else {
            response.sendRedirect(redirectURL + "?error=Toggle failed");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + "/admin/departments?error=Invalid request method");
    }
}