package controller.request;

import dao.RequestDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.User;

import java.io.IOException;

@WebServlet("/view_pending_request")
public class ViewPendingRequestServlet extends HttpServlet {
    private final RequestDAO dao = new RequestDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("currentUser") : null;

        if (currentUser == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        int limit = 10;
        String type = request.getParameter("type");
        String sort = request.getParameter("sort");
        int page = getPageParam(request.getParameter("page"));

        request.setAttribute("requestList", dao.getPendingApprovals(currentUser.getId(), type, sort, (page - 1) * limit, limit));
        request.setAttribute("totalPages", (int) Math.ceil((double) dao.countPendingApprovals(currentUser.getId(), type) / limit));

        request.setAttribute("currentPage", page);
        request.setAttribute("selectedType", type);
        request.setAttribute("selectedSort", sort);

        request.getRequestDispatcher("/WEB-INF/views/request/view_pending_request.jsp").forward(request, response);
    }

    private int getPageParam(String pageParam) {
        try {
            return (pageParam != null && !pageParam.isEmpty()) ? Integer.parseInt(pageParam) : 1;
        } catch (NumberFormatException e) {
            return 1;
        }
    }
}
