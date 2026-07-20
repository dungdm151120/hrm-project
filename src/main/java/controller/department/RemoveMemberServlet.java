package controller.department;

import dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "RemoveMemberServlet", value = "/remove_member")
public class RemoveMemberServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String userIdStr = request.getParameter("userId");
        String deptIdStr = request.getParameter("deptId");

        try {
            int userId = Integer.parseInt(userIdStr);
            UserDAO dao = new UserDAO();
            String result = dao.removeMemberFromDepartment2(userId);

            if ("SUCCESS".equals(result)) {
                response.sendRedirect(request.getContextPath() + "/admin/departments/employees?id=" + deptIdStr + "&msg=remove_success");
            } else if ("ERROR_IS_MANAGER".equals(result)) {
                response.sendRedirect(request.getContextPath() + "/admin/departments/employees?id=" + deptIdStr + "&error=cannot_remove_manager");
            } else {
                response.sendRedirect(request.getContextPath() + "/admin/departments/employees?id=" + deptIdStr + "&error=remove_failed");
            }
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/admin/departments/employees?id=" + deptIdStr + "&error=invalid_data");
        }
    }
}