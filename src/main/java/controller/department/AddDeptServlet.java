package controller.department;

import dao.DepartmentDAO;
import model.Department;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/admin/departments/add")
public class AddDeptServlet extends HttpServlet {

    private final DepartmentDAO departmentDAO = new DepartmentDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/department/dept_add.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        String name = request.getParameter("name");
        String description = request.getParameter("description");
        String activeParam = request.getParameter("active");

        StringBuilder errors = new StringBuilder();
        if (name == null || name.trim().isEmpty()) {
            errors.append("Department name cannot be empty.<br/>");
        }
        if (description == null || description.trim().isEmpty()) {
            errors.append("Description cannot be empty.<br/>");
        }

        boolean active = activeParam != null && activeParam.equals("true");

        if (errors.length() > 0) {
            request.setAttribute("error", errors.toString());
            request.setAttribute("name", name);
            request.setAttribute("description", description);
            request.setAttribute("active", active);
            request.getRequestDispatcher("/WEB-INF/views/department/dept_add.jsp").forward(request, response);
            return;
        }

        Department dept = new Department();
        dept.setName(name.trim());
        dept.setDescription(description.trim());
        dept.setActive(active);

        int newId = departmentDAO.addDepartment(dept);
        if (newId != -1) {

            boolean positionAssigned = departmentDAO.assignDefaultEmployeePosition(newId);
            if (!positionAssigned) {
                System.err.println("Could not assign default 'Employee' position for department ID " + newId);
            }

            HttpSession session = request.getSession();
            session.setAttribute("successMessage", "Department added successfully (ID: " + newId + ")");
            response.sendRedirect(request.getContextPath() + "/admin/departments");
        } else {
            request.setAttribute("error", "Failed to add department. Department name may already exist.");
            request.setAttribute("name", name);
            request.setAttribute("description", description);
            request.setAttribute("active", active);
            request.getRequestDispatcher("/WEB-INF/views/department/dept_add.jsp").forward(request, response);
        }
    }
}