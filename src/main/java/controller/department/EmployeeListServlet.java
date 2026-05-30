package controller.department;

import dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.User;

import java.io.IOException;
import java.util.List;

@WebServlet("/admin/departments/employees")
public class EmployeeListServlet extends HttpServlet {
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idParam = request.getParameter("id");

        List<User> userList;
        if (idParam == null || idParam.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/admin/departments?error=Missing department ID");
            return;
        }

        int id;
        try {
            id = Integer.parseInt(idParam);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/departments?error=Invalid department ID");
            return;
        }
        String keyword = request.getParameter("search");
        userList = userDAO.findByDepartmentId(id, keyword);

        request.setAttribute("userList", userList);
        request.setAttribute("search", keyword);

        request.getRequestDispatcher("/WEB-INF/views/department/employee_list.jsp")
                .forward(request, response);
    }
}
