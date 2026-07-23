package controller.request;

import dao.UserDAO;
import model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

@WebServlet("/create_overtime_request")
public class CreateOvertimeRequestServlet extends HttpServlet {
    private static final int MAX_REASON_LENGTH = 500;
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        User currentUser = (User) session.getAttribute("currentUser");

        try {
            if (currentUser.getDepartmentId() == null || currentUser.getDepartmentId() == 0) {
                response.sendRedirect("create_request?error=missing_department");
                return;
            }

            String overtimeDateStr = request.getParameter("overtimeDate");
            if (overtimeDateStr == null || overtimeDateStr.trim().isEmpty()) {
                response.sendRedirect("create_request?error=missing_date");
                return;
            }

            LocalDate overtimeDate = LocalDate.parse(overtimeDateStr);
            if (overtimeDate.isBefore(LocalDate.now())) {
                response.sendRedirect("create_request?error=date_past");
                return;
            }

            DayOfWeek dayOfWeek = overtimeDate.getDayOfWeek();
            if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
                response.sendRedirect("create_request?error=date_weekend");
                return;
            }

            dao.HolidayDAO holidayDAO = new dao.HolidayDAO();
            if (holidayDAO.isHoliday(overtimeDate)) {
                response.sendRedirect("create_request?error=overtime_date_holiday");
                return;
            }

            String reason = request.getParameter("reason");
            if (reason == null || reason.trim().isEmpty()) {
                response.sendRedirect("create_request?error=missing_reason");
                return;
            }
            reason = reason.trim();
            if (reason.length() > MAX_REASON_LENGTH) {
                response.sendRedirect("create_request?error=reason_too_long");
                return;
            }

            String approverIdParam = request.getParameter("approverId");
            if (approverIdParam == null || approverIdParam.trim().isEmpty()) {
                response.sendRedirect("create_request?error=missing_approver");
                return;
            }
            int approverId;
            try {
                approverId = Integer.parseInt(approverIdParam);
            } catch (NumberFormatException e) {
                response.sendRedirect("create_request?error=invalid_approver");
                return;
            }
            boolean validApprover = userDAO.getUserByPosition("HR Manager").stream()
                    .anyMatch(manager -> manager.getId() == approverId);
            if (!validApprover) {
                response.sendRedirect("create_request?error=invalid_approver");
                return;
            }

            String[] employeeIds = request.getParameterValues("employeeIds");
            if (employeeIds == null || employeeIds.length == 0) {
                response.sendRedirect("create_request?error=missing_employees");
                return;
            }

            service.OvertimeService overtimeService = new service.OvertimeService();
            Set<Integer> uniqueEmployeeIds = new LinkedHashSet<>();
            for (String empIdStr : employeeIds) {
                int empId;
                try {
                    empId = Integer.parseInt(empIdStr);
                } catch (NumberFormatException e) {
                    response.sendRedirect("create_request?error=invalid_employee");
                    return;
                }
                User employee = userDAO.findById(empId);
                if (employee == null
                        || !employee.isActive()
                        || employee.getDepartmentId() == null
                        || !employee.getDepartmentId().equals(currentUser.getDepartmentId())) {
                    response.sendRedirect("create_request?error=invalid_employee");
                    return;
                }
                if (overtimeService.checkDuplicateOvertime(empId, overtimeDate)) {
                    response.sendRedirect("create_request?error=duplicate_overtime");
                    return;
                }
                uniqueEmployeeIds.add(empId);
            }
            String[] validatedEmployeeIds = uniqueEmployeeIds.stream()
                    .map(String::valueOf)
                    .toArray(String[]::new);

            String[] observerIds = request.getParameterValues("observerIds");
            Set<Integer> uniqueObsIds = new LinkedHashSet<>();
            if (observerIds != null) {
                for (String id : observerIds) {
                    if (id != null && !id.trim().isEmpty()) {
                        uniqueObsIds.add(Integer.parseInt(id));
                    }
                }
            }

            boolean success = overtimeService.createOvertimeRequest(
                currentUser, overtimeDate, reason, validatedEmployeeIds,
                new ArrayList<>(uniqueObsIds), approverId
            );

            if (success) {
                response.sendRedirect("view_my_request?success=true");
            } else {
                response.sendRedirect("create_request?error=system_error");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("create_request?error=system_error");
        }
    }
}
