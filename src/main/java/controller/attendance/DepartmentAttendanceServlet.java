package controller.attendance;

import dao.AttendanceDAO;
import dao.DepartmentDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.AttendanceRecordDTO;
import model.Department;
import model.User;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/attendance/department")
public class DepartmentAttendanceServlet extends HttpServlet {
    private static final int PAGE_SIZE = 5;
    // private static final int MATRIX_DAY_COUNT = 7; // không còn dùng nữa
    private static final DateTimeFormatter DAY_FORMAT = DateTimeFormatter.ofPattern("dd/MM");

    private final AttendanceDAO attendanceDAO = new AttendanceDAO();
    private final DepartmentDAO departmentDAO = new DepartmentDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User currentUser = session != null ? (User) session.getAttribute("currentUser") : null;
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        Integer departmentId = currentUser.getDepartmentId();
        if (departmentId == null) {
            request.setAttribute("records", List.of());
            request.setAttribute("employees", List.of());
            request.setAttribute("daysInMonth", List.of());
            request.setAttribute("dayLabels", List.of());
            request.setAttribute("attendanceMap", Map.of());
            request.setAttribute("years", buildYearOptions(LocalDate.now().getYear(), LocalDate.now().getYear()));
            request.setAttribute("selectedMonth", LocalDate.now().getMonthValue());
            request.setAttribute("selectedYear", LocalDate.now().getYear());
            request.setAttribute("keyword", "");
            request.setAttribute("currentPage", 1);
            request.setAttribute("totalPages", 0);
            request.setAttribute("totalEmployees", 0);
            request.setAttribute("departmentName", "No department assigned");
            request.setAttribute("noDepartmentAssigned", true);
            request.getRequestDispatcher("/WEB-INF/views/attendance/department_attendance.jsp")
                    .forward(request, response);
            return;
        }

        Department department = departmentDAO.getDepartmentById(departmentId);
        LocalDate today = LocalDate.now();
        int selectedMonth = parseIntInRange(
                request.getParameter("month"),
                today.getMonthValue(),
                1,
                12
        );
        int selectedYear = parseIntInRange(
                request.getParameter("year"),
                today.getYear(),
                2000,
                2100
        );
        String keyword = normalizeKeyword(request.getParameter("keyword"));
        int currentPage = Math.max(1, parseInt(request.getParameter("page"), 1));

        int totalEmployees = attendanceDAO.countEmployeesForAttendanceMatrix(
                selectedMonth,
                selectedYear,
                departmentId,
                keyword
        );
        int totalPages = (int) Math.ceil((double) totalEmployees / PAGE_SIZE);
        if (totalPages > 0 && currentPage > totalPages) {
            currentPage = totalPages;
        }

        List<AttendanceRecordDTO> records = attendanceDAO.getAttendanceRecordsForMatrix(
                selectedMonth,
                selectedYear,
                departmentId,
                keyword,
                currentPage,
                PAGE_SIZE
        );

        Map<Integer, AttendanceRecordDTO> employeeMap = new LinkedHashMap<>();
        Map<String, AttendanceRecordDTO> attendanceMap = new LinkedHashMap<>();
        for (AttendanceRecordDTO record : records) {
            employeeMap.putIfAbsent(record.getUserId(), record);
            attendanceMap.put(buildAttendanceKey(record.getUserId(), record.getWorkDate()), record);
        }

        YearMonth selectedPeriod = YearMonth.of(selectedYear, selectedMonth);
        List<LocalDate> daysInMonth = new ArrayList<>();
        List<String> dayLabels = new ArrayList<>();
        // Hiển thị toàn bộ các ngày trong tháng (không giới hạn 7 ngày)
        for (int day = 1; day <= selectedPeriod.lengthOfMonth(); day++) {
            LocalDate date = selectedPeriod.atDay(day);
            daysInMonth.add(date);
            dayLabels.add(formatDayLabel(date));
        }

        request.setAttribute("records", records);
        request.setAttribute("employees", new ArrayList<>(employeeMap.values()));
        request.setAttribute("daysInMonth", daysInMonth);
        request.setAttribute("dayLabels", dayLabels);
        request.setAttribute("attendanceMap", attendanceMap);
        request.setAttribute("years", buildYearOptions(today.getYear(), selectedYear));
        request.setAttribute("selectedMonth", selectedMonth);
        request.setAttribute("selectedYear", selectedYear);
        request.setAttribute("keyword", keyword);
        request.setAttribute("currentPage", currentPage);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalEmployees", totalEmployees);
        request.setAttribute("departmentName", department != null ? department.getName() : "My department");
        request.getRequestDispatcher("/WEB-INF/views/attendance/department_attendance.jsp")
                .forward(request, response);
    }

    private String buildAttendanceKey(int userId, LocalDate workDate) {
        return userId + "_" + workDate;
    }

    // Đã đổi sang tiếng Anh
    private String formatDayLabel(LocalDate date) {
        return englishDayOfWeek(date.getDayOfWeek()) + " - " + date.format(DAY_FORMAT);
    }

    private String englishDayOfWeek(DayOfWeek dayOfWeek) {
        return switch (dayOfWeek) {
            case MONDAY    -> "Mon";
            case TUESDAY   -> "Tue";
            case WEDNESDAY -> "Wed";
            case THURSDAY  -> "Thu";
            case FRIDAY    -> "Fri";
            case SATURDAY  -> "Sat";
            case SUNDAY    -> "Sun";
        };
    }

    private List<Integer> buildYearOptions(int currentYear, int selectedYear) {
        int firstYear = Math.min(currentYear - 5, selectedYear);
        int lastYear = Math.max(currentYear + 1, selectedYear);
        List<Integer> years = new ArrayList<>();
        for (int year = lastYear; year >= firstYear; year--) {
            years.add(year);
        }
        return years;
    }

    private String normalizeKeyword(String keyword) {
        if (keyword == null) {
            return "";
        }
        return keyword.trim().replaceAll("\\s+", " ");
    }

    private int parseIntInRange(String value, int defaultValue, int min, int max) {
        int parsed = parseInt(value, defaultValue);
        return parsed >= min && parsed <= max ? parsed : defaultValue;
    }

    private int parseInt(String value, int defaultValue) {
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            return defaultValue;
        }
    }
}