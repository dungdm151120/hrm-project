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
    private final RequestDAO dao = new RequestDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) { response.sendRedirect("login.jsp"); return; }

        int limit = 5;

        // MY REQUESTS
        String status = request.getParameter("status");
        String type = request.getParameter("type");
        String sort = request.getParameter("sort");
        int page = getPageParam(request.getParameter("page"));

        request.setAttribute("myRequests", dao.getRequestByUserId(userId, status, type, sort, (page - 1) * limit, limit));
        request.setAttribute("totalPages", (int) Math.ceil((double) dao.countRequestByUserId(userId, status, type) / limit));
        request.setAttribute("currentPage", page);
        request.setAttribute("selectedStatus", status);
        request.setAttribute("selectedType", type);
        request.setAttribute("selectedSort", sort);

        request.getRequestDispatcher("/WEB-INF/views/request/view_my_request.jsp").forward(request, response);
    }

    private int getPageParam(String pageParam) {
        try {
            return (pageParam != null && !pageParam.isEmpty()) ? Integer.parseInt(pageParam) : 1;
        } catch (NumberFormatException e) {
            return 1;
        }
    }
}