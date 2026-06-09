package controller.department;

import dao.DepartmentDAO;
import dao.PositionDAO;
import dao.RoleDAO;
import dao.UserDAO;
import model.Department;
import model.Position;
import model.Role;
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
    private final RoleDAO roleDAO = new RoleDAO();

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

        List<User> employees = userDAO.findActiveManagerCandidates(deptId);

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

        Integer currentManagerId = department.getManagerUserId();
        if (currentManagerId != null && currentManagerId == newManagerId) {
            session.setAttribute("error", "Nhân viên này đã là trưởng phòng của phòng ban này.");
            response.sendRedirect("assign-manager?id=" + deptId);
            return;
        }

        boolean isHR = "Human Resources".equalsIgnoreCase(department.getName());
        boolean isIT = "Information Technology".equalsIgnoreCase(department.getName());
        boolean isFI = "Finance".equalsIgnoreCase(department.getName());

        Integer oldManagerPositionId = null;
        if (currentManagerId != null) {
            String oldPositionName = null;
            if (isHR) {
                oldPositionName = "HR Staff";
            } else if (isFI) {
                oldPositionName = "Payroll Staff";
            } else {
                oldPositionName = "Employee";
            }
            Position oldPosition = positionDAO.findByName(oldPositionName);
            if (oldPosition != null && oldPosition.isActive()) {
                oldManagerPositionId = oldPosition.getId();
            }
        }

        String newPositionName;
        if (isHR) {
            newPositionName = "HR Manager";
        } else if (isIT) {
            newPositionName = "System Administrator";
        } else if (isFI) {
            newPositionName = "Payroll Manager";
        } else {
            newPositionName = "Department Manager";
        }
        Position newPosition = positionDAO.findByName(newPositionName);

        if (newPosition == null) {
            session.setAttribute("error", "Không tìm thấy vị trí '" + newPositionName + "' trong hệ thống.");
            response.sendRedirect("assign-manager?id=" + deptId);
            return;
        }

        if (!newPosition.isActive()) {
            session.setAttribute("error", "Vị trí '" + newPositionName + "' hiện không khả dụng (đã bị vô hiệu).");
            response.sendRedirect("assign-manager?id=" + deptId);
            return;
        }

        boolean assigned = departmentDAO.assignManager(
                deptId,
                newManagerId,
                currentManagerId,
                oldManagerPositionId,
                newPosition.getId()
        );

        if (assigned) {
            String roleName = null;
            if (isHR) roleName = "HR_MANAGER";
            else if (isIT) roleName = "SYSTEM ADMIN";
            else if (isFI) roleName = "PAYROLL_MANAGER";
            else roleName = "DEPARTMENT_MANAGER";

            Role role = roleDAO.findByName(roleName);
            if (role != null) {
                userDAO.updateUserRole(newManagerId, role.getId());
            }

            session.setAttribute("successMessage", "Đã phân công trưởng phòng thành công!");
        } else {
            session.setAttribute("error", "Có lỗi xảy ra khi cập nhật. Vui lòng thử lại.");
        }
        response.sendRedirect("detail?id=" + deptId);
    }
}