package controller.department;

import dao.DepartmentDAO;
import dao.UserDAO;
import model.Department;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/admin/departments")
public class DeptListServlet extends HttpServlet {

    private final DepartmentDAO departmentDAO = new DepartmentDAO();
    private final UserDAO userDAO = new UserDAO();
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

        List<Department> departmentList;
        int totalItems;
        int totalPages;

        boolean hasSearchOrFilter = (search != null && !search.trim().isEmpty()) || active != null;

        if (!hasSearchOrFilter && ("members_desc".equals(sort) || "members_asc".equals(sort))) {
            List<Department> allList;
            if ("members_desc".equals(sort)) {
                allList = departmentDAO.getDepartmentsSortedByMemberCount(true);
            } else {
                allList = departmentDAO.getDepartmentsSortedByMemberCount(false);
            }
            totalItems = allList.size();
            totalPages = (int) Math.ceil((double) totalItems / PAGE_SIZE);
            if (currentPage > totalPages && totalPages > 0) currentPage = totalPages;
            int offset = (currentPage - 1) * PAGE_SIZE;
            int end = Math.min(offset + PAGE_SIZE, totalItems);
            departmentList = allList.subList(offset, end);
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
            totalItems = departmentDAO.getTotalDepartments(search, active);
            totalPages = (int) Math.ceil((double) totalItems / PAGE_SIZE);
            if (currentPage > totalPages && totalPages > 0) currentPage = totalPages;
            int offset = (currentPage - 1) * PAGE_SIZE;
            departmentList = departmentDAO.getDepartmentsWithPaging(search, active, sortBy, sortOrder, offset, PAGE_SIZE);
        }

        Map<Integer, Integer> memberCountMap = new HashMap<>();
        for (Department dept : departmentList) {
            memberCountMap.put(dept.getId(), userDAO.countUsersByDepartment(dept.getId()));
        }

        request.setAttribute("departmentList", departmentList);
        request.setAttribute("memberCountMap", memberCountMap);
        request.setAttribute("search", search);
        request.setAttribute("status", statusParam);
        request.setAttribute("sort", sort);
        request.setAttribute("currentPage", currentPage);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalItems", totalItems);

        request.getRequestDispatcher("/WEB-INF/views/department/dept_list.jsp").forward(request, response);
    }
}