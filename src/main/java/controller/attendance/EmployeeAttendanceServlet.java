package controller.attendance;

import dao.AttendanceDAO;
import dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.AttendanceRecord;
import model.AttendanceRecordDTO;
import model.AttendanceSummary;
import model.User;
import util.DBConnection;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;

@WebServlet("/attendance/employee")
public class EmployeeAttendanceServlet extends HttpServlet {
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter DAY_FORMAT = DateTimeFormatter.ofPattern("dd/MM");

    private final AttendanceDAO attendanceDAO = new AttendanceDAO();
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User currentUser = session != null ? (User) session.getAttribute("currentUser") : null;
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        Integer targetUserId = parsePositiveInteger(request.getParameter("userId"));
        if (targetUserId == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing or invalid employee ID");
            return;
        }

        if (!canViewEmployee(session, currentUser, targetUserId)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        User employee = userDAO.findById(targetUserId);
        if (employee == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Employee not found");
            return;
        }

        String empCode = getEmployeeCode(employee.getId());
        request.setAttribute("employeeCode", empCode != null ? empCode : "--");

        LocalDate today = LocalDate.now();
        int selectedYear = parseIntInRange(request.getParameter("year"), today.getYear(), 2000, 2100);
        int selectedMonth = parseIntInRange(request.getParameter("month"), today.getMonthValue(), 1, 12);
        YearMonth selectedPeriod = YearMonth.of(selectedYear, selectedMonth);

        List<AttendanceRecordDTO> recordList = attendanceDAO.getAttendanceDetailByUserAndMonth(
                employee.getId(),
                selectedMonth,
                selectedYear
        );

        Map<String, AttendanceRecordDTO> attendanceMap = new LinkedHashMap<>();
        for (AttendanceRecordDTO dto : recordList) {
            dto.setCssClass(resolveCssClass(dto.getStatus()));
            attendanceMap.put(dto.getUserId() + "_" + dto.getWorkDate(), dto);
        }

        AttendanceSummary summary = attendanceDAO.getSummaryByUser(
                employee.getId(),
                selectedPeriod.atDay(1),
                selectedPeriod.atEndOfMonth()
        );
        summary.setExpectedWorkHours(countWeekdays(selectedPeriod) * 8.0);

        List<LocalDate> daysInMonth = new ArrayList<>();
        List<String> dayLabels = new ArrayList<>();
        for (int day = 1; day <= selectedPeriod.lengthOfMonth(); day++) {
            LocalDate date = selectedPeriod.atDay(day);
            daysInMonth.add(date);
            dayLabels.add(vietnameseDayOfWeek(date.getDayOfWeek()) + " - " + date.format(DAY_FORMAT));
        }

        request.setAttribute("displayUser", employee);
        request.setAttribute("selectedYear", selectedYear);
        request.setAttribute("selectedMonth", selectedMonth);
        request.setAttribute("years", buildYearOptions(today.getYear(), selectedYear));
        request.setAttribute("daysInMonth", daysInMonth);
        request.setAttribute("dayLabels", dayLabels);
        request.setAttribute("attendanceMap", attendanceMap);
        request.setAttribute("summary", summary);
        request.setAttribute("summaryAction", request.getContextPath() + "/attendance/employee");
        request.setAttribute("summaryUserId", employee.getId());
        request.setAttribute("editable", true);

        request.getRequestDispatcher("/WEB-INF/views/attendance/employee_attendance.jsp").forward(request, response);
    }

    private AttendanceRecordDTO mapToDTO(AttendanceRecord record) {
        AttendanceRecordDTO dto = new AttendanceRecordDTO();
        dto.setAttendanceRecordId(record.getId());
        dto.setUserId(record.getUserId());
        dto.setWorkDate(record.getWorkDate());
        dto.setCheckIn(record.getCheckIn());
        dto.setCheckOut(record.getCheckOut());
        dto.setCheckInText(record.getCheckIn() != null ? record.getCheckIn().format(TIME_FORMAT) : "--");
        dto.setCheckOutText(record.getCheckOut() != null ? record.getCheckOut().format(TIME_FORMAT) : "--");
        dto.setTotalWorkHours(record.getTotalWorkHours());
        dto.setOvertimeHours(record.getOvertimeHours() != null ? record.getOvertimeHours() : 0.0);
        dto.setLateHours(record.getLateHours());
        dto.setEarlyLeaveHours(record.getEarlyLeaveHours());
        dto.setStatus(record.getStatus());
        dto.setNote(record.getNote());
        dto.setCssClass(resolveCssClass(record.getStatus()));
        dto.setEdited(record.getUpdatedAt() != null && !"ON_LEAVE".equals(record.getStatus()));
        return dto;
    }

    private String getEmployeeCode(int userId) {
        String sql = "SELECT employee_code FROM users WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("employee_code");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean canViewEmployee(HttpSession session, User currentUser, int targetUserId) {
        @SuppressWarnings("unchecked")
        Set<String> permissions = (Set<String>) session.getAttribute("userPermissions");
        if (permissions == null) return false;
        if (targetUserId == currentUser.getId()) {
            return permissions.contains("ATTENDANCE_VIEW_OWN");
        }
        if (permissions.contains("ATTENDANCE_VIEW_ALL")) {
            return true;
        }
        if (permissions.contains("ATTENDANCE_VIEW_DEPARTMENT") && currentUser.getDepartmentId() != null) {
            User targetUser = userDAO.findById(targetUserId);
            return targetUser != null && targetUser.getDepartmentId() != null
                    && targetUser.getDepartmentId().equals(currentUser.getDepartmentId());
        }
        return false;
    }

    private int countWeekdays(YearMonth period) {
        int count = 0;
        for (int d = 1; d <= period.lengthOfMonth(); d++) {
            DayOfWeek dow = period.atDay(d).getDayOfWeek();
            if (dow != DayOfWeek.SATURDAY && dow != DayOfWeek.SUNDAY) count++;
        }
        return count;
    }

    private String resolveCssClass(String status) {
        if (status == null) return "";
        return switch (status) {
            case "ON_TIME" -> "status-on-time";
            case "LATE", "EARLY_LEAVE", "LATE_AND_EARLY", "LATE_AND_EARLY_LEAVE" -> "status-late";
            case "ON_LEAVE" -> "status-leave";
            case "ABSENT" -> "status-absent";
            case "FORGOT_CHECKIN", "FORGOT_CHECKOUT",
                 "FORGOT_CHECK_IN", "FORGOT_CHECK_OUT" -> "status-forgot";
            default -> "";
        };
    }

    private String vietnameseDayOfWeek(DayOfWeek dayOfWeek) {
        return switch (dayOfWeek) {
            case MONDAY -> "T2";
            case TUESDAY -> "T3";
            case WEDNESDAY -> "T4";
            case THURSDAY -> "T5";
            case FRIDAY -> "T6";
            case SATURDAY -> "T7";
            case SUNDAY -> "CN";
        };
    }

    private List<Integer> buildYearOptions(int currentYear, int selectedYear) {
        int first = Math.min(currentYear - 5, selectedYear);
        int last = Math.max(currentYear + 1, selectedYear);
        List<Integer> years = new ArrayList<>();
        for (int y = last; y >= first; y--) years.add(y);
        return years;
    }

    private Integer parsePositiveInteger(String value) {
        try {
            int parsed = Integer.parseInt(value);
            return parsed > 0 ? parsed : null;
        } catch (Exception e) { return null; }
    }

    private int parseIntInRange(String value, int defaultValue, int min, int max) {
        try {
            int parsed = Integer.parseInt(value);
            return (parsed >= min && parsed <= max) ? parsed : defaultValue;
        } catch (Exception e) { return defaultValue; }
    }
}