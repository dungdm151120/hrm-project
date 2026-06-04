package controller.department;

import dao.DepartmentDAO;
import dao.PositionDAO;
import dao.UserDAO;
import model.Department;
import model.Position;
import model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/admin/departments/assign-positions")
public class AssignPositionServlet extends HttpServlet {

    private final DepartmentDAO departmentDAO = new DepartmentDAO();
    private final UserDAO userDAO = new UserDAO();
    private final PositionDAO positionDAO = new PositionDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idParam = request.getParameter("id");
        if (idParam == null || idParam.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/admin/departments");
            return;
        }

        int deptId;
        try {
            deptId = Integer.parseInt(idParam);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/departments");
            return;
        }

        Department department = departmentDAO.getDepartmentById(deptId);
        if (department == null || !department.isActive()) {
            HttpSession session = request.getSession();
            session.setAttribute("error", "Phòng ban không tồn tại hoặc đã bị vô hiệu.");
            response.sendRedirect(request.getContextPath() + "/admin/departments");
            return;
        }

        List<User> employees = userDAO.findActiveByDepartmentId(deptId);
        List<Position> assignablePositions = positionDAO.getAssignablePositionsByDepartment(deptId);

        request.setAttribute("department", department);
        request.setAttribute("employees", employees);
        request.setAttribute("assignablePositions", assignablePositions);
        request.getRequestDispatcher("/WEB-INF/views/department/assign_positions.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        String departmentIdParam = request.getParameter("departmentId");
        String userIdParam = request.getParameter("userId");
        String positionIdParam = request.getParameter("positionId");

        HttpSession session = request.getSession();

        if (departmentIdParam == null || userIdParam == null || positionIdParam == null) {
            session.setAttribute("error", "Dữ liệu không hợp lệ.");
            response.sendRedirect(request.getContextPath() + "/admin/departments");
            return;
        }

        int deptId;
        int userId;
        int posId;
        try {
            deptId = Integer.parseInt(departmentIdParam);
            userId = Integer.parseInt(userIdParam);
            posId = Integer.parseInt(positionIdParam);
        } catch (NumberFormatException e) {
            session.setAttribute("error", "Dữ liệu không hợp lệ.");
            response.sendRedirect(request.getContextPath() + "/admin/departments");
            return;
        }

        Department department = departmentDAO.getDepartmentById(deptId);
        if (department == null || !department.isActive()) {
            session.setAttribute("error", "Phòng ban không tồn tại hoặc đã bị vô hiệu.");
            response.sendRedirect(request.getContextPath() + "/admin/departments");
            return;
        }


        User currentUser = userDAO.findById(userId);
        if (currentUser != null && currentUser.getPositionName() != null) {
            String posName = currentUser.getPositionName();
            if ("HR Manager".equals(posName) || "System Administrator".equals(posName) || "Department Manager".equals(posName)) {
                session.setAttribute("error", "Không thể thay đổi vị trí của nhân viên đang giữ vai trò quản lý chủ chốt.");
                response.sendRedirect(request.getContextPath() + "/admin/departments/assign-positions?id=" + deptId);
                return;
            }
        }


        if (posId > 0) {
            List<Position> allowed = positionDAO.getAssignablePositionsByDepartment(deptId);
            boolean valid = allowed.stream().anyMatch(p -> p.getId() == posId);
            if (!valid) {
                session.setAttribute("error", "Vị trí được chọn không được phép.");
                response.sendRedirect(request.getContextPath() + "/admin/departments/assign-positions?id=" + deptId);
                return;
            }
        }

        Integer newPositionId = (posId <= 0) ? null : posId;
        boolean updated = userDAO.updateUserPosition(userId, newPositionId);

        if (updated) {
            session.setAttribute("successMessage", "Cập nhật vị trí thành công!");
        } else {
            session.setAttribute("error", "Có lỗi xảy ra khi cập nhật vị trí.");
        }
        response.sendRedirect(request.getContextPath() + "/admin/departments/assign-positions?id=" + deptId);
    }
}