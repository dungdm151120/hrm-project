package controller.admin;

import dao.PositionDAO;
import dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Position;
import model.User;

import java.io.IOException;

@WebServlet("/position/update")
public class UpdatePositionServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idParam = request.getParameter("id");
        if (idParam == null || idParam.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/position/list");
            return;
        }

        int id = Integer.parseInt(idParam);
        PositionDAO dao = new PositionDAO();
        Position position = dao.findById(id);

        request.setAttribute("position", position);
        request.getRequestDispatcher("/WEB-INF/views/admin/update_position.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            req.setCharacterEncoding("UTF-8");

            int id = Integer.parseInt(req.getParameter("id"));
            String name = req.getParameter("name");
            String description = req.getParameter("description");

            Position updatedPost = new Position();
            updatedPost.setId(id);
            updatedPost.setName(name);
            updatedPost.setDescription(description);

            PositionDAO dao = new PositionDAO();
            boolean isSuccess = dao.updatePosition(updatedPost);

            if (isSuccess) {
                resp.sendRedirect(req.getContextPath() + "/position/list");
            } else {
                req.setAttribute("error", "Update failed in database!");
                req.setAttribute("position", updatedPost);
                req.getRequestDispatcher("/WEB-INF/views/admin/update_position.jsp").forward(req, resp);
            }

        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("error", "Invalid data input format! " + e.getMessage());
            doGet(req, resp);
        }
    }
}