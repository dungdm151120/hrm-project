package controller.request;

import service.OvertimeService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/confirm_overtime")
public class ConfirmOvertimeServlet extends HttpServlet {
    private final OvertimeService overtimeService = new OvertimeService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        model.User sessionUser = (model.User) session.getAttribute("currentUser");
        dao.UserDAO userDAO = new dao.UserDAO();
        model.User dbUser = userDAO.findById(sessionUser.getId());
        
        if (dbUser == null || (!"HR Manager".equals(dbUser.getPositionName()) && !dbUser.getRoleName().contains("HR"))) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Only HR Manager can confirm overtime.");
            return;
        }

        String requestIdStr = request.getParameter("requestId");
        if (requestIdStr != null && !requestIdStr.isEmpty()) {
            try {
                int requestId = Integer.parseInt(requestIdStr);
                
                boolean updated = overtimeService.confirmOvertime(requestId);
                if (updated) {
                    response.sendRedirect("request_detail?id=" + requestId + "&success=confirm_success");
                } else {
                    response.sendRedirect("request_detail?id=" + requestId + "&error=confirm_failed");
                }
            } catch (Exception e) {
                e.printStackTrace();
                response.sendRedirect("request_detail?id=" + requestIdStr + "&error=system_error");
            }
        } else {
            response.sendRedirect("view_my_request?error=invalid_request");
        }
    }
}
