package controller.department;

import dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "AddMemberServlet", value = "/add_member")
public class AddMemberServlet extends HttpServlet {
    private final UserDAO userDAO = new UserDAO();

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String deptId = request.getParameter("deptId");

        // Lấy danh sách nhân viên chưa có phòng ban
        request.setAttribute("unassignedUsers", userDAO.findUnassignedUsers());
        request.setAttribute("deptId", deptId);

        request.getRequestDispatcher("/WEB-INF/views/department/add_member.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String deptIdStr = request.getParameter("deptId");

        System.out.println("Gia tri deptId nhan duoc: " + deptIdStr);

        if (deptIdStr != null && !deptIdStr.isEmpty()) {
            int deptId = Integer.parseInt(deptIdStr);
            String[] userIds = request.getParameterValues("userIds");

            if (userIds != null) {
                for (String idStr : userIds) {
                    int userId = Integer.parseInt(idStr);
                    userDAO.updateDepartmentMember(userId, deptId, true);
                }
            }
            // Redirect về đúng tên tham số 'id' mà trang Employee List đang đợi
            response.sendRedirect(request.getContextPath() + "/admin/departments/employees?id=" + deptId);
        } else {
            System.out.println("Loi: deptId bi null");
            // Redirect về danh sách phòng ban nếu deptId bị lỗi
            response.sendRedirect(request.getContextPath() + "/admin/departments");
        }
    }
}
