package controller.request;

import dao.RequestDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Request;
import model.User;

import java.io.IOException;
import java.util.List;

@WebServlet("/view_department_request")
public class ViewDepartmentRequestServlet extends HttpServlet {
    private final RequestDAO dao = new RequestDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("currentUser");

        if (user == null) {
            response.sendRedirect("login");
            return;
        }

        // Xác định phòng ban cần hiển thị dữ liệu
        String deptParam = request.getParameter("deptId");
        int targetDeptId;
        try {
            if ("BUSINESS ADMIN".equals(user.getRoleName()) && deptParam != null && !deptParam.isEmpty()) {
                targetDeptId = Integer.parseInt(deptParam);
            } else {
                targetDeptId = user.getDepartmentId();
            }
        } catch (NumberFormatException e) {
            targetDeptId = user.getDepartmentId();
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

        // 3. Gọi hàm DAO mới để lấy danh sách dữ liệu và đếm tổng số dòng cho Department Requests
        List<Request> list = dao.getRequestByDepartment(targetDeptId, status, type, sort, offset, limit);
        int totalRecords = dao.countRequestByDepartment(targetDeptId, status, type);

        int totalPages = (int) Math.ceil((double) totalRecords / limit);
        if (totalPages == 0) {
            totalPages = 1;
        }

        // 4. Đẩy dữ liệu ra thuộc tính của request để tệp JSP sử dụng
        request.setAttribute("requestList", list);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("selectedDeptId", targetDeptId);

        // Gửi ngược lại các bộ lọc hiện tại để JSP hiển thị trạng thái "selected" trên UI
        request.setAttribute("selectedStatus", status);
        request.setAttribute("selectedType", type);
        request.setAttribute("selectedSort", sort);

        request.getRequestDispatcher("/WEB-INF/views/request/view_department_request.jsp").forward(request, response);
    }
}