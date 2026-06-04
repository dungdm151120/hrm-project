package controller.admin;

import dao.UserDAO;
import model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "UserListServlet", value = "/user_list")
public class UserListServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();
    private static final int PAGE_SIZE = 5; // Đồng bộ PAGE_SIZE = 5

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Search
        String keyword = request.getParameter("keyword");
        if (keyword == null) keyword = request.getParameter("search");
        if (keyword == null) keyword = "";

        // Status
        String status = request.getParameter("status");
        if (status == null || status.trim().isEmpty()) status = "all";
        if (!"active".equals(status) && !"inactive".equals(status)) status = "all";

        // Sort
        String sort = request.getParameter("sort");
        if (sort == null || sort.trim().isEmpty()) sort = "name_asc";
        if (!"name_desc".equals(sort)) sort = "name_asc";

        // Xử lý trang hiện tại
        int currentPage = 1;
        String pageParam = request.getParameter("page");
        if (pageParam != null && !pageParam.trim().isEmpty()) {
            try {
                currentPage = Integer.parseInt(pageParam);
                if (currentPage < 1) currentPage = 1;
            } catch (NumberFormatException ignored) {
                currentPage = 1;
            }
        }

        Boolean active = null;
        if ("active".equals(status)) active = true;
        else if ("inactive".equals(status)) active = false;

        // Chuyển đổi sort chuỗi thành sortBy/sortOrder cho câu SQL
        String sortBy = "id";
        String sortOrder = "asc";
        if ("name_asc".equals(sort)) {
            sortBy = "name";
            sortOrder = "asc";
        } else if ("name_desc".equals(sort)) {
            sortBy = "name";
            sortOrder = "desc";
        }

        // Tính toán phân trang
        int totalItems = userDAO.countUsers(keyword, active);
        int totalPages = (int) Math.ceil((double) totalItems / PAGE_SIZE);
        if (totalPages > 0 && currentPage > totalPages) currentPage = totalPages;

        int offset = (currentPage - 1) * PAGE_SIZE;
        List<User> userList = userDAO.getUsersWithPaging(keyword, active, sortBy, sortOrder, offset, PAGE_SIZE);

        request.setAttribute("userList", userList);
        request.setAttribute("keyword", keyword);
        request.setAttribute("search", keyword);
        request.setAttribute("status", status);
        request.setAttribute("sort", sort);
        request.setAttribute("currentPage", currentPage);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalItems", totalItems);
        request.setAttribute("pageSize", PAGE_SIZE);

        request.getRequestDispatcher("/WEB-INF/views/admin/user_list.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}