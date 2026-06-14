package controller.request;

import dao.RequestDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/view_my_request")
public class ViewMyRequestServlet extends HttpServlet {
    private RequestDAO dao = new RequestDAO();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("userId");

        if (userId == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        request.setAttribute("myRequests", dao.getRequestsByUserId(userId));
        request.getRequestDispatcher("/WEB-INF/views/request/view_my_request.jsp").forward(request, response);
    }
}
