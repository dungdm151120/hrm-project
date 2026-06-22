package controller.attendance;

import model.OvertimeDetail;
import model.User;
import service.OvertimeService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDate;

@WebServlet("/get_overtime_detail")
public class GetOvertimeDetailServlet extends HttpServlet {
    private final OvertimeService overtimeService = new OvertimeService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User currentUser = session != null ? (User) session.getAttribute("currentUser") : null;
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            int userId = Integer.parseInt(request.getParameter("userId"));
            LocalDate workDate = LocalDate.parse(request.getParameter("workDate"));

            OvertimeDetail detail = overtimeService.getOvertimeDetailByUserAndDate(userId, workDate);

            if (detail == null) {
                // If there's no OT detail, redirect back to attendance with a message
                session.setAttribute("errorMessage", "There is no Overtime record for this day.");
                response.sendRedirect(request.getContextPath() + "/admin/attendance/my");
                return;
            }

            request.setAttribute("detail", detail);
            request.getRequestDispatcher("/WEB-INF/views/attendance/overtime_detail.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("errorMessage", "Invalid parameters.");
            response.sendRedirect(request.getContextPath() + "/admin/attendance/my");
        }
    }
}
