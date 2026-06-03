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
    private static final int PAGE_SIZE = 10;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String search = request.getParameter("search");
        String statusParam = request.getParameter("status");
        String sort = request.getParameter("sort");
        String pageParam = request.getParameter("page");

        Boolean active = null;
        if (statusParam != null && !statusParam.isEmpty() && !statusParam.equals("all")) {
            active = Boolean.parseBoolean(statusParam);
        }

        int currentPage = 1;
        if (pageParam != null && !pageParam.isEmpty()) {
            try {
                currentPage = Integer.parseInt(pageParam);
                if (currentPage < 1) currentPage = 1;
            } catch (NumberFormatException ignored) {}
        }

        String sortBy = "id";
        String sortOrder = "asc";
        if ("name_asc".equals(sort)) {
            sortBy = "name";
            sortOrder = "asc";
        } else if ("name_desc".equals(sort)) {
            sortBy = "name";
            sortOrder = "desc";
        }

        int totalItems = userDAO.countUsers(search, active);
        int totalPages = (int) Math.ceil((double) totalItems / PAGE_SIZE);
        if (currentPage > totalPages && totalPages > 0) currentPage = totalPages;

        int offset = (currentPage - 1) * PAGE_SIZE;
        List<User> userList = userDAO.getUsersWithPaging(search, active, sortBy, sortOrder, offset, PAGE_SIZE);

        request.setAttribute("userList", userList);
        request.setAttribute("oldKeyword", search);
        request.setAttribute("status", statusParam);
        request.setAttribute("sort", sort);
        request.setAttribute("currentPage", currentPage);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalItems", totalItems);

        request.getRequestDispatcher("/WEB-INF/views/admin/user_list.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
