package controller.role;

import dao.RoleDAO;
import model.Role;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
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

        request.getRequestDispatcher("/WEB-INF/views/role/role-list.jsp")
               .forward(request, response);
    }
}
