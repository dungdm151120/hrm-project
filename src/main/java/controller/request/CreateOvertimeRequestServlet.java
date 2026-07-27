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
    private static final int MAX_REASON_LENGTH = 1000;
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
                redirectToOvertimeForm(request, response, "missing_department");
                return;
            }

            String overtimeDateStr = request.getParameter("overtimeDate");
            if (overtimeDateStr == null || overtimeDateStr.trim().isEmpty()) {
                redirectToOvertimeForm(request, response, "missing_date");
                return;
            }

            LocalDate overtimeDate;
            try {
                overtimeDate = LocalDate.parse(overtimeDateStr);
            } catch (Exception e) {
                redirectToOvertimeForm(request, response, "missing_date");
                return;
            }
            if (overtimeDate.isBefore(LocalDate.now())) {
                redirectToOvertimeForm(request, response, "date_past");
                return;
            }

            DayOfWeek dayOfWeek = overtimeDate.getDayOfWeek();
            if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
                redirectToOvertimeForm(request, response, "date_weekend");
                return;
            }

            dao.HolidayDAO holidayDAO = new dao.HolidayDAO();
            if (holidayDAO.isHoliday(overtimeDate)) {
                redirectToOvertimeForm(request, response, "overtime_date_holiday");
                return;
            }

            String reason = request.getParameter("reason");
            if (reason == null || reason.trim().isEmpty()) {
                redirectToOvertimeForm(request, response, "missing_reason");
                return;
            }
            reason = reason.trim();
            if (reason.length() > MAX_REASON_LENGTH) {
                redirectToOvertimeForm(request, response, "reason_too_long");
                return;
            }

            String approverIdParam = request.getParameter("approverId");
            if (approverIdParam == null || approverIdParam.trim().isEmpty()) {
                redirectToOvertimeForm(request, response, "missing_approver");
                return;
            }
            int approverId;
            try {
                approverId = Integer.parseInt(approverIdParam);
            } catch (NumberFormatException e) {
                redirectToOvertimeForm(request, response, "invalid_approver");
                return;
            }
            boolean validApprover = userDAO.getUserByPosition("HR Manager").stream()
                    .anyMatch(manager -> manager.getId() == approverId);
            if (!validApprover) {
                redirectToOvertimeForm(request, response, "invalid_approver");
                return;
            }

            String[] employeeIds = request.getParameterValues("employeeIds");
            if (employeeIds == null || employeeIds.length == 0) {
                redirectToOvertimeForm(request, response, "missing_employees");
                return;
            }

            service.OvertimeService overtimeService = new service.OvertimeService();
            Set<Integer> uniqueEmployeeIds = new LinkedHashSet<>();
            for (String empIdStr : employeeIds) {
                int empId;
                try {
                    empId = Integer.parseInt(empIdStr);
                } catch (NumberFormatException e) {
                    redirectToOvertimeForm(request, response, "invalid_employee");
                    return;
                }
                User employee = userDAO.findById(empId);
                if (employee == null
                        || !employee.isActive()
                        || employee.getDepartmentId() == null
                        || !employee.getDepartmentId().equals(currentUser.getDepartmentId())) {
                    redirectToOvertimeForm(request, response, "invalid_employee");
                    return;
                }
                if (overtimeService.checkDuplicateOvertime(empId, overtimeDate)) {
                    redirectToOvertimeForm(request, response, "duplicate_overtime");
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
                redirectToOvertimeForm(request, response, "system_error");
            }

        } catch (Exception e) {
            e.printStackTrace();
            redirectToOvertimeForm(request, response, "system_error");
        }
    }

    private void redirectToOvertimeForm(HttpServletRequest request, HttpServletResponse response, String error)
            throws IOException {
        response.sendRedirect(request.getContextPath() + "/create_request?type=OVERTIME&error=" + error);
    }
}
