package controller.role;

import dao.RoleDAO;
import model.Role;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/admin/roles")
public class RoleListServlet extends HttpServlet {

    private final RoleDAO roleDAO = new RoleDAO();
    private static final int PAGE_SIZE = 5;

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

        List<Role> roles;
        int totalItems;
        int totalPages;

        boolean hasSearchOrFilter = (search != null && !search.trim().isEmpty()) || active != null;

        if (!hasSearchOrFilter && ("perm_desc".equals(sort) || "perm_asc".equals(sort))) {
            List<Role> allList;
            if ("perm_desc".equals(sort)) {
                allList = roleDAO.getRolesSortedByPermissionCount(true);
            } else {
                allList = roleDAO.getRolesSortedByPermissionCount(false);
            }
            totalItems = allList.size();
            totalPages = (int) Math.ceil((double) totalItems / PAGE_SIZE);
            if (currentPage > totalPages && totalPages > 0) currentPage = totalPages;
            int offset = (currentPage - 1) * PAGE_SIZE;
            int end = Math.min(offset + PAGE_SIZE, totalItems);
            roles = allList.subList(offset, end);
        } else {
            String sortBy = "id";
            String sortOrder = "asc";
            if ("name_asc".equals(sort)) {
                sortBy = "name";
                sortOrder = "asc";
            } else if ("name_desc".equals(sort)) {
                sortBy = "name";
                sortOrder = "desc";
            }
            totalItems = roleDAO.getTotalRoles(search, active);
            totalPages = (int) Math.ceil((double) totalItems / PAGE_SIZE);
            if (currentPage > totalPages && totalPages > 0) currentPage = totalPages;
            int offset = (currentPage - 1) * PAGE_SIZE;
            roles = roleDAO.getRolesWithPaging(search, active, sortBy, sortOrder, offset, PAGE_SIZE);
        }

        request.setAttribute("roles", roles);
        request.setAttribute("search", search);
        request.setAttribute("status", statusParam);
        request.setAttribute("sort", sort);
        request.setAttribute("currentPage", currentPage);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalItems", totalItems);

        request.getRequestDispatcher("/WEB-INF/views/role/role_list.jsp").forward(request, response);
    }
}