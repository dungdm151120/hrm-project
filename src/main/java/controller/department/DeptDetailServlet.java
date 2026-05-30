package controller.department;

import dao.DepartmentDAO;
import model.Department;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/admin/departments/detail")
public class DeptDetailServlet extends HttpServlet {

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
        request.getRequestDispatcher("/WEB-INF/views/department/dept_detail.jsp")
                .forward(request, response);
    }
}