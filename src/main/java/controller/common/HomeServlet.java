package controller.common;

import dao.DepartmentDAO;
import dao.PositionDAO;
import dao.RoleDAO;
import dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/home")
public class HomeServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();
    private final DepartmentDAO departmentDAO = new DepartmentDAO();
    private final PositionDAO positionDAO = new PositionDAO();
    private final RoleDAO roleDAO = new RoleDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("currentUser") != null) {
            request.setAttribute("currentUser", session.getAttribute("currentUser"));
        }

        // --- Users ---
        int activeUsers = userDAO.countActiveUsers();
        int totalUsers = userDAO.countAllUsers();
        request.setAttribute("activeUsers", activeUsers);
        request.setAttribute("totalUsers", totalUsers);

        // --- Departments ---
        int totalDepartments = departmentDAO.getAllDepartments().size();
        long activeDepartments = departmentDAO.getAllDepartments()
                .stream()
                .filter(d -> d.isActive())
                .count();
        request.setAttribute("activeDepartments", (int) activeDepartments);
        request.setAttribute("totalDepartments", totalDepartments);

        // --- Positions ---
        int totalPositions = positionDAO.findAllPositions().size();
        long activePositions = positionDAO.findAllPositions()
                .stream()
                .filter(p -> p.isActive())
                .count();
        request.setAttribute("activePositions", (int) activePositions);
        request.setAttribute("totalPositions", totalPositions);

        // --- Roles ---
        int totalRoles = roleDAO.getAllRoles().size();
        long activeRoles = roleDAO.getAllRoles()
                .stream()
                .filter(r -> r.isActive())
                .count();
        request.setAttribute("activeRoles", (int) activeRoles);
        request.setAttribute("totalRoles", totalRoles);

        request.getRequestDispatcher("/WEB-INF/views/common/homepage.jsp").forward(request, response);
    }
}