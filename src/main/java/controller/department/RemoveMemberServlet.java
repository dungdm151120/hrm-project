package controller.department;

import dao.UserDAO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "RemoveMemberServlet", value = "/remove_member")
public class RemoveMemberServlet extends HttpServlet {
    private final UserDAO userDAO = new UserDAO();

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String userIdStr = request.getParameter("userId");
        String deptIdStr = request.getParameter("deptId");

        if (userIdStr != null) {
            userDAO.updateDepartment(Integer.parseInt(userIdStr), null, false);
        }

        response.sendRedirect(request.getContextPath() + "/department_members?deptId=" + deptIdStr);
    }
}
