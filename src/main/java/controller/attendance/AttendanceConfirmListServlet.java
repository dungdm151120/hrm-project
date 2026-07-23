package controller.attendance;

import dao.AttendanceConfirmDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.AttendanceConfirmedSummaryDTO;
import model.User;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@WebServlet("/attendance/confirm_list")
public class AttendanceConfirmListServlet extends HttpServlet {
    private final AttendanceConfirmDAO confirmedDAO = new AttendanceConfirmDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        @SuppressWarnings("unchecked")
        Set<String> permissions = (Set<String>) session.getAttribute("userPermissions");
        boolean canViewAll = canViewAllFinalizedAttendance(permissions);
        boolean canViewDepartment = canViewDepartmentFinalizedAttendance(permissions);
        if (!canViewAll && !canViewDepartment) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "You do not have permission to access this page.");
            return;
        }

        Integer departmentIdFilter = canViewAll ? null : currentUser.getDepartmentId();
        if (!canViewAll && departmentIdFilter == null) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "No department assigned.");
            return;
        }

        int currentYear = LocalDate.now().getYear();
        String yearParam = request.getParameter("year");
        int year = currentYear;
        if (yearParam != null && !yearParam.isEmpty()) {
            try {
                year = Integer.parseInt(yearParam);
            } catch (NumberFormatException e) {
                year = currentYear;
            }
        }

        List<AttendanceConfirmedSummaryDTO> confirmedList = confirmedDAO.getConfirmedMonths(year, departmentIdFilter);

        request.setAttribute("confirmedList", confirmedList);
        request.setAttribute("selectedYear", year);
        request.setAttribute("currentYear", currentYear);
        
        request.getRequestDispatcher("/WEB-INF/views/attendance/attendance_confirm_list.jsp").forward(request, response);
    }

    private boolean canViewAllFinalizedAttendance(Set<String> permissions) {
        return permissions != null && (permissions.contains("ATTENDANCE_VIEW_ALL")
                || permissions.contains("ATTENDANCE_FINALIZE_HR")
                || permissions.contains("PAYROLL_VIEW_LIST"));
    }

    private boolean canViewDepartmentFinalizedAttendance(Set<String> permissions) {
        return permissions != null && (permissions.contains("ATTENDANCE_VIEW_DEPARTMENT")
                || permissions.contains("ATTENDANCE_CONFIRM_DEPT")
                || permissions.contains("PAYROLL_VIEW_DEPARTMENT"));
    }
}
