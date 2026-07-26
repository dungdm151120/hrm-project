package controller.admin;

import dao.DepartmentDAO;
import dao.PositionDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Department;
import model.Position;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@WebServlet("/position/add")
public class AddPositionServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        DepartmentDAO dao = new DepartmentDAO();
        List<Department> departments = dao.getAllDepartments();
        request.setAttribute("departments", departments);
        request.getRequestDispatcher("/WEB-INF/views/admin/add_position.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            request.setCharacterEncoding("UTF-8");
            int departmentId = Integer.parseInt(request.getParameter("departmentId"));
            String  name = request.getParameter("name");
            String description = request.getParameter("description");
            boolean active = Boolean.parseBoolean(request.getParameter("active"));

            Position position = new Position();
            position.setDepartmentId(departmentId);
            position.setName(name);
            position.setDescription(description);
            position.setActive(active);
            position.setCreatedAt(LocalDateTime.now());

            PositionDAO dao = new PositionDAO();
            DepartmentDAO deptDAO = new DepartmentDAO();

            if (dao.isPositionNameExists(name)) {
                request.setAttribute("departments", deptDAO.getAllDepartments());
                request.setAttribute("error", "Position name already exists!");
                request.setAttribute("newPosition", position);
                request.getRequestDispatcher("/WEB-INF/views/admin/add_position.jsp").forward(request, response);
                return;
            }

            boolean isSuccess = dao.addPosition(position);

            if (isSuccess) {
                response.sendRedirect(request.getContextPath() + "/position/list");
            } else {
                request.setAttribute("departments", deptDAO.getAllDepartments());
                request.setAttribute("error", "Add new position failed in database!");
                request.setAttribute("newPosition", position);
                request.getRequestDispatcher("/WEB-INF/views/admin/add_position.jsp").forward(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}