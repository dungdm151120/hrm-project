package controller.admin;

import dao.PositionDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Position;

import java.io.IOException;
import java.time.LocalDateTime;

@WebServlet("/position/add")
public class AddPositionServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/admin/add_position.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            request.setCharacterEncoding("UTF-8");
            String  name = request.getParameter("name");
            String description = request.getParameter("description");
            boolean active = Boolean.parseBoolean(request.getParameter("active"));

            Position position = new Position();
            position.setName(name);
            position.setDescription(description);
            position.setActive(active);
            position.setCreatedAt(LocalDateTime.now());

            PositionDAO dao = new PositionDAO();
            boolean isSuccess = dao.addPosition(position);

            if (isSuccess) {
                response.sendRedirect(request.getContextPath() + "/position/list");
            } else {
                request.setAttribute("error", "Add new position failed in database!");
                request.setAttribute("newPosition", position);
                request.getRequestDispatcher("/WEB-INF/views/admin/add_position.jsp").forward(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}