package controller.department;

import dao.DepartmentDAO;
import dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.URLEncoder;

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

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int userId = Integer.parseInt(request.getParameter("userId"));
        int newDeptId = Integer.parseInt(request.getParameter("newDeptId"));

        // Kiểm tra trước khi thực hiện
        if (deptDAO.isManager(userId)) {
            // Chặn lại và quay về trang danh sách với thông báo lỗi
            String errorMessage = "Không thể chuyển phòng ban vì nhân viên này đang là Quản lý!";
            response.sendRedirect(request.getContextPath() + "/admin/departments/employees?id="
                    + request.getParameter("currentDeptId") + "&error=" + URLEncoder.encode(errorMessage, "UTF-8"));
            return;
        }

        // Nếu qua được bước kiểm tra, mới thực hiện move
        userDAO.updateDepartmentMember(userId, newDeptId, true);
        response.sendRedirect(request.getContextPath() + "/admin/departments/employees?id=" + newDeptId + "&success=Chuyển nhân viên thành công!");
    }
}

