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
        String status = request.getParameter("status");
        String sort = request.getParameter("sort");

        String cleanKeyword = (keyword != null) ? keyword.trim() : "";
        String cleanStatus = ("all".equals(status) || status == null) ? "" : status.trim();

        PositionDAO dao = new PositionDAO();
        List<Position> positionList = dao.findPositionsAdvanced(cleanKeyword, cleanStatus, sort);

        request.setAttribute("keyword", keyword);
        request.setAttribute("status", status);
        request.setAttribute("sort", sort);
        request.setAttribute("positionList", positionList);
        request.getRequestDispatcher("/WEB-INF/views/admin/position_list.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}