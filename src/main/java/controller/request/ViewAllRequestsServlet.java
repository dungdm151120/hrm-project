package controller.request;

import dao.RequestDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Request;

import java.io.IOException;
import java.util.List;

@WebServlet("/view_all_requests")
public class ViewAllRequestsServlet extends HttpServlet {
    private final RequestDAO dao = new RequestDAO();

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        HttpSession session = request.getSession();

        Object roleObj = session.getAttribute("role");
        String role = (roleObj != null) ? roleObj.toString() : "";

        List<Request> allRequests = dao.getAllRequests();
        request.setAttribute("requestList", allRequests);
        request.getRequestDispatcher("/WEB-INF/views/request/view_all_requests.jsp")
                .forward(request, response);
    }
}