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
            errors.append("Tên phòng ban không được để trống.<br/>");
        }
        if (description == null || description.trim().isEmpty()) {
            errors.append("Mô tả không được để trống.<br/>");
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
                System.err.println("Không thể gán position 'Employee' mặc định cho department ID " + newId);
            }

            HttpSession session = request.getSession();
            session.setAttribute("successMessage", "Thêm phòng ban thành công (ID: " + newId + ")");
            response.sendRedirect(request.getContextPath() + "/admin/departments");
        } else {
            request.setAttribute("error", "Thêm phòng ban thất bại. Có thể tên phòng ban đã tồn tại.");
            request.setAttribute("name", name);
            request.setAttribute("description", description);
            request.setAttribute("active", active);
            request.getRequestDispatcher("/WEB-INF/views/department/dept_add.jsp").forward(request, response);
        }
    }
}