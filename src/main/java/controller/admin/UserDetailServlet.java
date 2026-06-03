package controller.admin;

import dao.UserDAO;
import jakarta.servlet.annotation.WebServlet;
import model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "UserDetailServlet", value = "/user_detail")
public class UserDetailServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idString = request.getParameter("id");

        if (idString != null && !idString.isEmpty()) {
            try {
                int id = Integer.parseInt(idString);

                UserDAO dao = new UserDAO();
                User user = dao.findById(id);

                if (user != null) {
                    request.setAttribute("user", user);
                    request.getRequestDispatcher("/WEB-INF/views/admin/user_detail.jsp").forward(request, response);
                } else {
                    response.sendRedirect("user_list");
                }
            } catch (NumberFormatException e) {
                response.sendRedirect("user_list");
            }
        } else {
            response.sendRedirect("user_list");
        }
    }
}
