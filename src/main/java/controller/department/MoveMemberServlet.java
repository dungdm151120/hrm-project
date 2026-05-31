package controller.department;

import dao.DepartmentDAO;
import dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "MoveMemberServlet", value = "/move_member")
public class MoveMemberServlet extends HttpServlet {
    private final UserDAO userDAO = new UserDAO();
    private final DepartmentDAO deptDAO = new DepartmentDAO();

    // Hiển thị form chọn phòng ban
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int userId = Integer.parseInt(request.getParameter("userId"));

        request.setAttribute("user", userDAO.findById(userId));
        request.setAttribute("deptList", deptDAO.getAllDepartments());

        request.getRequestDispatcher("/WEB-INF/views/department/move_member.jsp").forward(request, response);
    }

    // Lưu phòng ban mới
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String userIdStr = request.getParameter("userId");
        String newDeptIdStr = request.getParameter("newDeptId");

        System.out.println("DEBUG: userId=" + userIdStr + ", newDeptId=" + newDeptIdStr); // Kiểm tra log này

        if (newDeptIdStr != null && !newDeptIdStr.isEmpty()) {
            int userId = Integer.parseInt(userIdStr);
            int newDeptId = Integer.parseInt(newDeptIdStr);
            userDAO.updateDepartmentMember(userId, newDeptId, true);

            response.sendRedirect(request.getContextPath() + "/admin/departments/employees?id=" + newDeptId);
        } else {
            response.sendRedirect(request.getContextPath() + "/admin/departments?error=InvalidID");
        }
    }
}
