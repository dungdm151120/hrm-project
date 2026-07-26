package controller.admin;

import dao.DepartmentDAO;
import dao.PositionDAO;
import dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Department;
import model.Position;
import model.User;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@WebServlet("/position/update")
public class UpdatePositionServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idParam = request.getParameter("id");
        if (idParam == null || idParam.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/position/list");
            return;
        }

        int id = Integer.parseInt(idParam);
        PositionDAO dao = new PositionDAO();
        Position position = dao.findById(id);

        DepartmentDAO deptDao = new DepartmentDAO();
        List<Department> departments = deptDao.getAllDepartments();

        request.setAttribute("departments", departments);
        request.setAttribute("position", position);
        request.getRequestDispatcher("/WEB-INF/views/admin/update_position.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            req.setCharacterEncoding("UTF-8");

            int id = Integer.parseInt(req.getParameter("id"));
            int departmentId = Integer.parseInt(req.getParameter("departmentId"));
            String name = req.getParameter("name");
            String description = req.getParameter("description");

            Position updatedPost = new Position();
            updatedPost.setId(id);
            updatedPost.setDepartmentId(departmentId);
            updatedPost.setName(name);
            updatedPost.setDescription(description);
            updatedPost.setUpdatedAt(LocalDateTime.now());

            PositionDAO dao = new PositionDAO();
            DepartmentDAO deptDao = new DepartmentDAO();

            if (dao.isPositionNameExistsForUpdate(name, id)) {
                req.setAttribute("departments", deptDao.getAllDepartments());
                req.setAttribute("error", "Position name already exists!");
                req.setAttribute("position", updatedPost);
                req.getRequestDispatcher("/WEB-INF/views/admin/update_position.jsp").forward(req, resp);
                return;
            }

            boolean isSuccess = dao.updatePosition(updatedPost);

            if (isSuccess) {
                resp.sendRedirect(req.getContextPath() + "/position/list");
            } else {
                req.setAttribute("departments", deptDao.getAllDepartments());
                req.setAttribute("error", "Update failed in database!");
                req.setAttribute("position", updatedPost);
                req.getRequestDispatcher("/WEB-INF/views/admin/update_position.jsp").forward(req, resp);
            }

        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("error", "Invalid data input format! " + e.getMessage());
            doGet(req, resp);
        }
    }
}