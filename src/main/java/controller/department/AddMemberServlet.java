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

        int deptId = Integer.parseInt(request.getParameter("deptId"));
        String[] userIds = request.getParameterValues("userIds");

        if (userIds != null) {
            for (String idStr : userIds) {
                int userId = Integer.parseInt(idStr);
                userDAO.updateDepartment(userId, deptId, true);
            }
        }

        response.sendRedirect(request.getContextPath() + "/department_members?deptId=" + deptId);
    }
}
