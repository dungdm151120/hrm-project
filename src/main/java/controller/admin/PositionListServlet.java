package controller.admin;

import dao.PositionDAO;
import dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Position;
import model.User;

import java.io.IOException;
import java.util.List;

@WebServlet("/position/list")
public class PositionListServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String keyword = request.getParameter("search");
        String statusParam = request.getParameter("status");

        String cleanKeyword = (keyword != null) ? keyword.trim() : "";
        String cleanStatus = ("all".equals(statusParam) || statusParam == null) ? "" : statusParam.trim();

        PositionDAO dao = new PositionDAO();
        List<Position> list = dao.findPositionsAdvanced(cleanKeyword, cleanStatus);

        request.setAttribute("oldKeyword", keyword);
        request.setAttribute("status", statusParam);
        request.setAttribute("positionList", list);
        request.getRequestDispatcher("/WEB-INF/views/admin/position_list.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}