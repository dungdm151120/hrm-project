package controller.role;

import dao.RoleDAO;
import model.Role;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/admin/roles")
public class RoleListServlet extends HttpServlet {

    private final RoleDAO roleDAO = new RoleDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<Role> roles = roleDAO.getAllRoles();
        request.setAttribute("roles", roles);

        request.getRequestDispatcher("/WEB-INF/views/role/role_list.jsp")
                .forward(request, response);
    }
}
