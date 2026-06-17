package controller.request;

import dao.RequestDAO;
import model.Request;
import model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.*;

@WebServlet("/request_detail")
public class RequestDetailServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idParam = request.getParameter("id");

        if (idParam == null || idParam.isEmpty()) {
            response.sendRedirect("view_my_request");
            return;
        }

        String cleanId = idParam.split("&")[0].split("\\?")[0];
        int id = Integer.parseInt(cleanId);
        RequestDAO dao = new RequestDAO();

        Request req = dao.getRequestById(id);

        if (req != null) {
            List<User> observers = dao.getObserversByRequestId(id);

            req.setObserver(observers);

            request.setAttribute("request", req);
            request.getRequestDispatcher("/WEB-INF/views/request/request_detail.jsp").forward(request, response);
        } else {
            response.sendRedirect("view_my_request");
        }
    }
}
