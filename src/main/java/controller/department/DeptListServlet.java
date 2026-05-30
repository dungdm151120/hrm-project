package controller.department;

import dao.DepartmentDAO;
import model.Department;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/admin/departments")
public class DeptListServlet extends HttpServlet {

    private final DepartmentDAO departmentDAO = new DepartmentDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String search = request.getParameter("search");
        String statusParam = request.getParameter("status");

        Boolean active = null;
        if (statusParam != null && !statusParam.isEmpty() && !statusParam.equals("all")) {
            active = Boolean.parseBoolean(statusParam);
        }

        List<Department> departmentList;
        if (search == null && active == null) {
            departmentList = departmentDAO.getAllDepartmentsWithManager();
        } else {
            departmentList = departmentDAO.searchDepartmentsWithManager(search, active);
        }

        request.setAttribute("departmentList", departmentList);
        request.setAttribute("search", search);
        request.setAttribute("status", statusParam);

        request.getRequestDispatcher("/WEB-INF/views/department/dept_list.jsp")
                .forward(request, response);
    }
}