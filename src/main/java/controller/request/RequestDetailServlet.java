package controller.request;

import dao.RequestDAO;
import model.Request;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebServlet("/request_detail")
public class RequestDetailServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String from = request.getParameter("from");
        String idParam = request.getParameter("id");

        if (idParam == null || idParam.isEmpty()) {
            response.sendRedirect("view_my_request");
            return;
        }

        int id = Integer.parseInt(idParam);
        RequestDAO dao = new RequestDAO();

        Request req = dao.getRequestById(id);

        if (req != null) {
            request.setAttribute("request", req);
            request.setAttribute("from", from);
            request.getRequestDispatcher("/WEB-INF/views/request/request_detail.jsp").forward(request, response);
        } else {
            response.sendRedirect("view_my_request");
        }
    }
}
