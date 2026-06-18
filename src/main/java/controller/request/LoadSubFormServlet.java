package controller.request;

import dao.UserDAO;
import model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/load_sub_form")
public class LoadSubFormServlet extends HttpServlet {
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Must be logged in");
            return;
        }

        User user = (User) session.getAttribute("currentUser");
        String type = request.getParameter("type");
        String jspPath = "/WEB-INF/views/request/subforms/default.jsp";

        if ("POSITION_HANDOVER".equals(type)) {
            String position = userDAO.getPositionNameByUserId(user.getId());
            boolean isManager = (position != null && position.contains("Manager"));
            boolean isSysAdmin = (position != null && position.contains("Admin"));

            // Nếu không phải Manager/Admin thì chặn
            if (!isManager && !isSysAdmin) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Bạn không có quyền tạo loại đơn này.");
                return;
            }
        }

        // --- ĐỔ DỮ LIỆU VÀO REQUEST SCOPE CHO FORM CON SỬ DỤNG ---

        // 1. Danh sách Business Admin (Cho Approver)
        request.setAttribute("businessAdminList", userDAO.getUserByRole("BUSINESS ADMIN"));

        // 2. Danh sách HR Manager (Cho Handler / Observer)
        List<User> hrManagers = userDAO.getUserByPosition("HR Manager");
        request.setAttribute("hrManagers", hrManagers);

        // 3. Danh sách nhân viên trong phòng ban (Cho Handover Observer)
        int deptId = (user.getDepartmentId() != null) ? user.getDepartmentId() : 0;
        List<User> deptEmployees = userDAO.getAllEmployeesByDepartment(deptId);
        List<User> deptEmployeesFiltered = new ArrayList<>();
        if (deptEmployees != null) {
            for (User emp : deptEmployees) {
                if (emp.getId() != user.getId()) {
                    deptEmployeesFiltered.add(emp);
                }
            }
        }
        request.setAttribute("deptEmployees", deptEmployeesFiltered);

        // 4. Danh sách tất cả Observers tổng hợp (Cho Move/Remove Observer)
        List<User> allObservers = new ArrayList<>();
        List<User> sysAdmins = userDAO.getUserByPosition("System Administrator");
        List<User> payrollManagers = userDAO.getUserByPosition("Payroll Manager");
        List<User> deptManagers = userDAO.getAllDeptManager();
        if (sysAdmins != null) allObservers.addAll(sysAdmins);
        if (hrManagers != null) allObservers.addAll(hrManagers);
        if (payrollManagers != null) allObservers.addAll(payrollManagers);
        if (deptManagers != null) allObservers.addAll(deptManagers);
        request.setAttribute("allObservers", allObservers);


        if ("POSITION_HANDOVER".equals(type)) {
            jspPath = "/WEB-INF/views/request/subforms/position_handover.jsp";
        } else if ("EMP_MOVE_REMOVE".equals(type)) {
            jspPath = "/WEB-INF/views/request/subforms/move_remove.jsp";
        }

        try {
            request.getRequestDispatcher(jspPath).forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}