package controller.department;

import dao.DepartmentDAO;
import model.Department;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/admin/departments/update")
public class DeptInfoUpdateServlet extends HttpServlet {

    private final DepartmentDAO departmentDAO = new DepartmentDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idParam = request.getParameter("id");
        if (idParam == null || idParam.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/admin/departments?error=Missing department ID");
            return;
        }
        int id;
        try {
            id = Integer.parseInt(idParam);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/departments?error=Invalid department ID");
            return;
        }

        Department department = departmentDAO.getDepartmentByIdWithManager(id);
        if (department == null) {
            response.sendRedirect(request.getContextPath() + "/admin/departments?error=Department not found");
            return;
        }

        request.setAttribute("department", department);
        request.getRequestDispatcher("/WEB-INF/views/department/dept_update.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        String idParam = request.getParameter("id");
        String name = request.getParameter("name");
        String description = request.getParameter("description");
        String activeParam = request.getParameter("active");

        if (idParam == null || idParam.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/admin/departments?error=Missing department ID");
            return;
        }
        int id;
        try {
            id = Integer.parseInt(idParam);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/departments?error=Invalid department ID");
            return;
        }

        StringBuilder errors = new StringBuilder();
        if (name == null || name.trim().isEmpty()) {
            errors.append("Name cannot be empty.<br/>");
        }
        if (description == null || description.trim().isEmpty()) {
            errors.append("Description cannot be empty.<br/>");
        }

        boolean active = activeParam != null; // checkbox: có check -> true, không check -> false

        if (errors.length() > 0) {
            request.setAttribute("error", errors.toString());
            Department dept = new Department();
            dept.setId(id);
            dept.setName(name);
            dept.setDescription(description);
            dept.setActive(active);
            request.setAttribute("department", dept);
            request.getRequestDispatcher("/WEB-INF/views/department/dept_update.jsp").forward(request, response);
            return;
        }

        boolean updated = departmentDAO.updateDepartment(id, name.trim(), description.trim(), active);

        if (updated) {
            response.sendRedirect(request.getContextPath() + "/admin/departments/detail?id=" + id + "&success=Department updated successfully");
        } else {
            request.setAttribute("error", "Update failed. Please try again.");
            Department dept = new Department();
            dept.setId(id);
            dept.setName(name.trim());
            dept.setDescription(description.trim());
            dept.setActive(active);
            request.setAttribute("department", dept);
            request.getRequestDispatcher("/WEB-INF/views/department/dept_update.jsp").forward(request, response);
        }
    }
}