package controller.request;

import dao.HolidayDAO;
import dao.OvertimeRequestDAO;
import dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.User;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/overtime/available-employees")
public class AvailableOvertimeEmployeesServlet extends HttpServlet {
    private final UserDAO userDAO = new UserDAO();
    private final OvertimeRequestDAO overtimeRequestDAO = new OvertimeRequestDAO();
    private final HolidayDAO holidayDAO = new HolidayDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User currentUser = session == null ? null : (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            sendError(response, HttpServletResponse.SC_UNAUTHORIZED, "You must be logged in.");
            return;
        }
        if (currentUser.getDepartmentId() == null) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, "No department assigned.");
            return;
        }

        LocalDate overtimeDate;
        try {
            overtimeDate = LocalDate.parse(request.getParameter("date"));
        } catch (Exception e) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid overtime date.");
            return;
        }

        if (overtimeDate.isBefore(LocalDate.now())) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Overtime date cannot be in the past.");
            return;
        }
        DayOfWeek day = overtimeDate.getDayOfWeek();
        if (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Overtime date must be from Monday to Friday.");
            return;
        }
        if (holidayDAO.isHoliday(overtimeDate)) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Overtime date cannot be a holiday.");
            return;
        }

        List<User> departmentEmployees =
                userDAO.getAllEmployeesByDepartment(currentUser.getDepartmentId());
        List<User> availableEmployees = new ArrayList<>();
        for (User employee : departmentEmployees) {
            if (!employee.isActive()
                    || overtimeRequestDAO.checkDuplicateOvertime(employee.getId(), overtimeDate)) {
                continue;
            }
            availableEmployees.add(employee);
        }

        request.setAttribute("availableEmployees", availableEmployees);
        request.getRequestDispatcher("/WEB-INF/views/request/subforms/overtime_employee_list.jsp")
                .forward(request, response);
    }

    private void sendError(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("text/plain;charset=UTF-8");
        response.getWriter().write(message);
    }
}
