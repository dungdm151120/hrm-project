package controller.department;

import dao.DepartmentDAO;
import dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.User;

import java.io.IOException;

@WebServlet(name = "MoveMemberServlet", value = "/move_member")
public class MoveMemberServlet extends HttpServlet {

    @Override
    // Hiển thị trang jsp
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String userIdStr = request.getParameter("userId");

        if (userIdStr == null || userIdStr.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/admin/departments/list");
            return;
        }

        int userId = Integer.parseInt(userIdStr);

        UserDAO userDAO = new UserDAO();
        DepartmentDAO deptDAO = new DepartmentDAO();

        User user = userDAO.findById(userId);

        request.setAttribute("user", user);
        request.setAttribute("deptList", deptDAO.getAllDepartments()); // Lấy toàn bộ phòng ban

        request.getRequestDispatcher("/WEB-INF/views/department/move_member.jsp").forward(request, response);
    }

    // Thực hiện move
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String userIdStr = request.getParameter("userId");
        String newDeptIdStr = request.getParameter("newDeptId");
        String currentDeptIdStr = request.getParameter("currentDeptId");

        if (userIdStr == null || newDeptIdStr == null || currentDeptIdStr == null) {
            response.sendRedirect(request.getContextPath() + "/admin/departments/list?error=invalid_access");
            return;
        }

        try {
            int userId = Integer.parseInt(userIdStr);
            int newDeptId = Integer.parseInt(newDeptIdStr);
            int currentDeptId = Integer.parseInt(currentDeptIdStr);

            UserDAO dao = new UserDAO();
            String result = dao.moveDepartmentMember(userId, newDeptId);

            if ("SUCCESS".equals(result)) {
                response.sendRedirect(request.getContextPath() + "/admin/departments/employees?id=" + newDeptId + "&msg=move_success");
            } else if ("ERROR_IS_MANAGER".equals(result)) {
                response.sendRedirect(request.getContextPath() + "/admin/departments/employees?id=" + currentDeptId + "&error=is_manager");
            } else {
                response.sendRedirect(request.getContextPath() + "/admin/departments/employees?id=" + currentDeptId + "&error=failed");
            }
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/departments/list?error=invalid_id");
        }
    }
}
