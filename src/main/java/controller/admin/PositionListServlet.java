package controller.admin;

import dao.PositionDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Position;

import java.io.IOException;
import java.util.List;

@WebServlet("/position/list")
public class PositionListServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String keyword = request.getParameter("search");
        String statusParam = request.getParameter("status");
        String sort = request.getParameter("sort");
        String pageParam = request.getParameter("page");

        // Mặc định sắp xếp cũ nhất trước nếu không có tham số sort
        if (sort == null || sort.trim().isEmpty()) {
            sort = "id_asc";
        }

        Boolean active = null;
        if (statusParam != null && !statusParam.isEmpty() && !"all".equals(statusParam)) {
            active = Boolean.parseBoolean(statusParam);
        }

        int currentPage = 1;
        int pageSize = 5;
        if (pageParam != null && !pageParam.isEmpty()) {
            try {
                currentPage = Integer.parseInt(pageParam);
                if (currentPage < 1) currentPage = 1;
            } catch (NumberFormatException ignored) {}
        }

        PositionDAO dao = new PositionDAO();

        int totalRows = dao.countPositions(keyword, active);
        int totalPages = (int) Math.ceil((double) totalRows / pageSize);

        if (currentPage > totalPages && totalPages > 0) {
            currentPage = totalPages;
        }

        int offset = (currentPage - 1) * pageSize;
        List<Position> positionList = dao.findPositionsAdvanced(keyword, active, sort, offset, pageSize);

        request.setAttribute("positionList", positionList);
        request.setAttribute("currentPage", currentPage);
        request.setAttribute("totalPage", totalPages);

        request.setAttribute("keyword", keyword);
        request.setAttribute("status", statusParam);
        request.setAttribute("sort", sort);

        request.getRequestDispatcher("/WEB-INF/views/admin/position_list.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}