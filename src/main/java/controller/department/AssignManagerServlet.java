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

@WebServlet("/admin/departments/assign-manager")
public class AssignManagerServlet extends HttpServlet {

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
        if (department == null) {
            response.sendRedirect(request.getContextPath() + "/admin/departments");
            return;
        }

        // Lấy danh sách nhân viên active trong phòng ban
        List<User> employees = userDAO.findActiveByDepartmentId(deptId);

        request.setAttribute("department", department);
        request.setAttribute("employees", employees);
        request.getRequestDispatcher("/WEB-INF/views/department/assign_manager.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        String departmentIdParam = request.getParameter("departmentId");
        String userIdParam = request.getParameter("userId");

        HttpSession session = request.getSession();

        // Validate input
        if (departmentIdParam == null || departmentIdParam.trim().isEmpty()
                || userIdParam == null || userIdParam.trim().isEmpty()) {
            session.setAttribute("error", "Vui lòng chọn một nhân viên.");
            response.sendRedirect(request.getContextPath() + "/admin/departments/assign-manager?id=" + departmentIdParam);
            return;
        }

        int deptId;
        int newManagerId;
        try {
            deptId = Integer.parseInt(departmentIdParam);
            newManagerId = Integer.parseInt(userIdParam);
        } catch (NumberFormatException e) {
            session.setAttribute("error", "Dữ liệu không hợp lệ.");
            response.sendRedirect(request.getContextPath() + "/admin/departments");
            return;
        }

        Department department = departmentDAO.getDepartmentById(deptId);
        if (department == null) {
            session.setAttribute("error", "Phòng ban không tồn tại.");
            response.sendRedirect(request.getContextPath() + "/admin/departments");
            return;
        }

        // Kiểm tra xem người được chọn có phải là trưởng phòng hiện tại không
        Integer currentManagerId = department.getManagerUserId();
        if (currentManagerId != null && currentManagerId == newManagerId) {
            session.setAttribute("error", "Nhân viên này đã là trưởng phòng của phòng ban này.");
            response.sendRedirect("assign-manager?id=" + deptId);
            return;
        }

        // Xác định phòng ban có phải Human Resources không
        boolean isHR = "Human Resources".equalsIgnoreCase(department.getName());

        // 1. Xử lý trưởng phòng cũ (nếu có)
        if (currentManagerId != null) {
            String oldPositionName;
            if (isHR) {
                oldPositionName = "HR Staff";
            } else {
                oldPositionName = "Employee";
            }

            Position oldPosition = positionDAO.findByName(oldPositionName);
            if (oldPosition != null) {
                userDAO.updateUserPosition(currentManagerId, oldPosition.getId());
            } else {
                session.setAttribute("error", "Không tìm thấy vị trí '" + oldPositionName + "' trong hệ thống.");
                response.sendRedirect("assign-manager?id=" + deptId);
                return;
            }
        }

        // 2. Xác định vị trí mới cho trưởng phòng mới
        String newPositionName;
        if (isHR) {
            newPositionName = "HR Manager";
        } else {
            newPositionName = "Department Manager";
        }

        Position newPosition = positionDAO.findByName(newPositionName);
        if (newPosition == null) {
            session.setAttribute("error", "Không tìm thấy vị trí '" + newPositionName + "' trong hệ thống.");
            response.sendRedirect("assign-manager?id=" + deptId);
            return;
        }

        // 3. Cập nhật position cho người được chọn
        boolean updatedPosition = userDAO.updateUserPosition(newManagerId, newPosition.getId());

        // 4. Cập nhật manager cho phòng ban
        boolean updatedManager = departmentDAO.updateManager(deptId, newManagerId);

        if (updatedPosition && updatedManager) {
            session.setAttribute("successMessage", "Đã phân công trưởng phòng thành công!");
        } else {
            session.setAttribute("error", "Có lỗi xảy ra khi cập nhật. Vui lòng thử lại.");
        }
        response.sendRedirect("detail?id=" + deptId);
    }
}