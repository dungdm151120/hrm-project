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

@WebServlet("/attendance/confirm-list")
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

        String role = currentUser.getRoleName() != null ? currentUser.getRoleName().toUpperCase() : "";
        boolean isHRManager = "HR_MANAGER".equals(role);
        boolean isManager = currentUser.isManager();
        boolean isPayrollRole = isPayrollRole(currentUser);

        if (!isHRManager && !isManager && !isPayrollRole) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "You do not have permission to access this page.");
            return;
        }

        Integer departmentIdFilter = null;
        if (isManager && !isHRManager && !isPayrollRole) {
            departmentIdFilter = currentUser.getDepartmentId();
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

    private boolean isPayrollRole(User user) {
        String roleName = user.getRoleName();
        return "PAYROLL_MANAGER".equalsIgnoreCase(roleName)
                || "PAYROLL_STAFF".equalsIgnoreCase(roleName);
    }
}
