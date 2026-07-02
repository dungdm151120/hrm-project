package controller.attendance;

import dao.AttendanceConfirmDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.AttendanceConfirmedDetailDTO;
import model.AttendanceConfirmedMonthOverviewDTO;
import model.User;

import java.io.IOException;
import java.util.List;

@WebServlet("/attendance/confirm-detail")
public class AttendanceConfirmDetailServlet extends HttpServlet {
    private final AttendanceConfirmDAO confirmedDAO = new AttendanceConfirmDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String role = currentUser.getRoleName() != null ? currentUser.getRoleName().toUpperCase() : "";
        boolean isHRorAdmin = role.contains("ADMIN") || role.contains("HR_") || role.contains("PAYROLL_");
        boolean isManager = currentUser.isManager();

        if (!isHRorAdmin && !isManager) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "You do not have permission to access this page.");
            return;
        }

        Integer departmentIdFilter = null;
        if (isManager && !isHRorAdmin) {
            departmentIdFilter = currentUser.getDepartmentId();
        }

        int month;
        int year;
        try {
            month = Integer.parseInt(request.getParameter("month"));
            year = Integer.parseInt(request.getParameter("year"));
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/attendance/confirm-list");
            return;
        }

        String searchQuery = request.getParameter("search");
        
        int page = 1;
        int pageSize = 10;
        try {
            String pageParam = request.getParameter("page");
            if (pageParam != null) {
                page = Integer.parseInt(pageParam);
                if (page < 1) page = 1;
            }
        } catch (NumberFormatException e) {
            page = 1;
        }

        int offset = (page - 1) * pageSize;

        // Get Overview (Summary Cards)
        AttendanceConfirmedMonthOverviewDTO overview = confirmedDAO.getConfirmedMonthOverview(month, year, departmentIdFilter);
        
        // Get Total Records for Pagination
        int totalRecords = confirmedDAO.getConfirmedDetailsCount(month, year, searchQuery, departmentIdFilter);
        int totalPages = (int) Math.ceil((double) totalRecords / pageSize);

        // Get Details
        List<AttendanceConfirmedDetailDTO> details = confirmedDAO.getConfirmedDetails(month, year, searchQuery, departmentIdFilter, offset, pageSize);

        request.setAttribute("month", month);
        request.setAttribute("year", year);
        request.setAttribute("searchQuery", searchQuery);
        request.setAttribute("overview", overview);
        request.setAttribute("details", details);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalRecords", totalRecords);
        
        request.getRequestDispatcher("/WEB-INF/views/attendance/attendance_confirm_detail.jsp").forward(request, response);
    }
}
