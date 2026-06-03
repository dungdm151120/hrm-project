package controller.department;

import dao.DepartmentDAO;
import dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Department;
import model.User;

import java.io.IOException;
import java.util.List;

@WebServlet("/admin/departments/employees")
public class EmployeeListServlet extends HttpServlet {
    private final UserDAO userDAO = new UserDAO();
    private final DepartmentDAO departmentDAO = new DepartmentDAO();
    private static final int PAGE_SIZE = 10;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idParam = request.getParameter("departmentId");
        if (idParam == null || idParam.trim().isEmpty()) {
            idParam = request.getParameter("id");
        }

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

        String keyword = request.getParameter("keyword");
        if (keyword == null) keyword = request.getParameter("search");
        if (keyword == null) keyword = "";

        String status = request.getParameter("status");
        if (status == null || status.trim().isEmpty()) status = "all";
        if (!"active".equals(status) && !"inactive".equals(status)) status = "all";

        String sort = request.getParameter("sort");
        if (sort == null || sort.trim().isEmpty()) sort = "name_asc";
        if (!"name_desc".equals(sort)) sort = "name_asc";

        int currentPage = 1;
        String pageParam = request.getParameter("page");
        if (pageParam != null && !pageParam.trim().isEmpty()) {
            try {
                currentPage = Integer.parseInt(pageParam);
                if (currentPage < 1) currentPage = 1;
            } catch (NumberFormatException ignored) {
                currentPage = 1;
            }
        }

        Department department = departmentDAO.getDepartmentById(id);
        int totalEmployees = userDAO.countEmployeesByDepartment(id, keyword, status);
        int totalPages = (int) Math.ceil((double) totalEmployees / PAGE_SIZE);
        if (totalPages > 0 && currentPage > totalPages) currentPage = totalPages;

        List<User> employees = userDAO.getEmployeesByDepartment(id, keyword, status, sort, currentPage, PAGE_SIZE);

        for (User u : employees) {
            boolean isManager = departmentDAO.isManager(u.getId());
            u.setManager(isManager);
        }

        request.setAttribute("department", department);
        request.setAttribute("employees", employees);
        request.setAttribute("userList", employees);
        request.setAttribute("keyword", keyword);
        request.setAttribute("search", keyword);
        request.setAttribute("status", status);
        request.setAttribute("sort", sort);
        request.setAttribute("currentPage", currentPage);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("pageSize", PAGE_SIZE);
        request.setAttribute("departmentId", id);

        request.getRequestDispatcher("/WEB-INF/views/department/employee_list.jsp")
                .forward(request, response);
    }
}
