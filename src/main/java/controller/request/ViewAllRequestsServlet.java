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

@WebServlet("/view_all_request")
public class ViewAllRequestsServlet extends HttpServlet {
    private final RequestDAO dao = new RequestDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        HttpSession session = request.getSession();
        Object roleObj = session.getAttribute("role");
        String role = (roleObj != null) ? roleObj.toString() : "";

        // Kiểm tra đăng nhập
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        // 1. Đọc các tham số filter, sort từ URL request
        String status = request.getParameter("status");
        String type = request.getParameter("type");
        String sort = request.getParameter("sort");

        // 2. Xử lý phân trang (Paging)
        int page = 1;
        try {
            String pageParam = request.getParameter("page");
            if (pageParam != null && !pageParam.isEmpty()) {
                page = Integer.parseInt(pageParam);
            }
        } catch (NumberFormatException e) {
            page = 1;
        }

        int limit = 10;
        int offset = (page - 1) * limit;

        // 3. Lấy danh sách dữ liệu và đếm tổng số dòng cho All Requests
        List<Request> allRequests = dao.getAllRequest(status, type, sort, offset, limit);
        int totalRecords = dao.countAllRequest(status, type);

        int totalPages = (int) Math.ceil((double) totalRecords / limit);
        if (totalPages == 0) {
            totalPages = 1;
        }

        // 4. Đẩy dữ liệu ra thuộc tính của request để JSP sử dụng
        request.setAttribute("requestList", allRequests);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);

        request.setAttribute("selectedStatus", status);
        request.setAttribute("selectedType", type);
        request.setAttribute("selectedSort", sort);

        request.getRequestDispatcher("/WEB-INF/views/request/view_all_request.jsp")
                .forward(request, response);
    }
}