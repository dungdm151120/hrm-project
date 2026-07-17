package controller.attendance;

import dao.AttendanceConfirmDAO;
import dao.AttendanceDAO;
import dao.DepartmentDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.AttendanceRecordDTO;
import model.DepartmentConfirmStatusDTO;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@WebServlet({"/attendance/records", "/attendance/view_all"})
public class AttendanceRecordServlet extends HttpServlet {
    private static final int PAGE_SIZE = 5;
    private static final DateTimeFormatter DAY_FORMAT = DateTimeFormatter.ofPattern("dd/MM");

    private final AttendanceDAO attendanceDAO = new AttendanceDAO();
    private final DepartmentDAO departmentDAO = new DepartmentDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        boolean isUpdateMode = "/attendance/records".equals(request.getServletPath());
        String actionUrl = request.getContextPath() + request.getServletPath();

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
        Integer selectedDepartmentId = parsePositiveInteger(request.getParameter("departmentId"));
        String keyword = normalizeKeyword(request.getParameter("keyword"));
        int currentPage = Math.max(1, parseInt(request.getParameter("page"), 1));

        // Đảm bảo dữ liệu holiday đã có cho tất cả nhân viên trong tháng
        attendanceDAO.createHolidayRecordsForMonth(selectedYear, selectedMonth);

        int totalEmployees = attendanceDAO.countEmployeesForAttendanceMatrix(
                selectedMonth,
                selectedYear,
                selectedDepartmentId,
                keyword
        );
        int totalPages = (int) Math.ceil((double) totalEmployees / PAGE_SIZE);
        if (totalPages > 0 && currentPage > totalPages) {
            currentPage = totalPages;
        }

        List<AttendanceRecordDTO> records = attendanceDAO.getAttendanceRecordsForMatrix(
                selectedMonth,
                selectedYear,
                selectedDepartmentId,
                keyword,
                currentPage,
                PAGE_SIZE
        );

        AttendanceConfirmDAO confirmDAO = new AttendanceConfirmDAO();
        String overallStatus = confirmDAO.getOverallStatus(selectedMonth, selectedYear);
        List<DepartmentConfirmStatusDTO> deptStatuses = confirmDAO.getDepartmentLockStatuses(selectedMonth, selectedYear);

        Map<Integer, Boolean> lockedDepartments = new java.util.HashMap<>();
        for (DepartmentConfirmStatusDTO ds : deptStatuses) {
            lockedDepartments.put(ds.getDepartmentId(), "CONFIRMED".equals(ds.getStatus()));
        }
        boolean isOverallLocked = "HR_SENT".equals(overallStatus) || "APPROVED".equals(overallStatus);

        Map<Integer, AttendanceRecordDTO> employeeMap = new LinkedHashMap<>();
        Map<String, AttendanceRecordDTO> attendanceMap = new LinkedHashMap<>();
        for (AttendanceRecordDTO record : records) {
            boolean isDeptLocked = record.getDepartmentId() != null && lockedDepartments.getOrDefault(record.getDepartmentId(), false);
            record.setLocked(isOverallLocked || isDeptLocked);

            employeeMap.putIfAbsent(record.getUserId(), record);
            attendanceMap.put(buildAttendanceKey(record.getUserId(), record.getWorkDate()), record);
        }

        // Lấy danh sách ngày lễ trong tháng
        List<LocalDate> holidayDates = attendanceDAO.getHolidayDatesInMonth(selectedYear, selectedMonth);

        YearMonth selectedPeriod = YearMonth.of(selectedYear, selectedMonth);
        List<LocalDate> daysInMonth = new ArrayList<>();
        List<String> dayLabels = new ArrayList<>();
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
        request.setAttribute("departments", departmentDAO.getAllDepartments());
        request.setAttribute("years", buildYearOptions(today.getYear(), selectedYear));
        request.setAttribute("selectedMonth", selectedMonth);
        request.setAttribute("selectedYear", selectedYear);
        request.setAttribute("selectedDepartmentId", selectedDepartmentId);
        request.setAttribute("keyword", keyword);
        request.setAttribute("currentPage", currentPage);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalEmployees", totalEmployees);
        request.setAttribute("holidayDates", holidayDates);
        request.setAttribute("isUpdateMode", isUpdateMode);
        request.setAttribute("actionUrl", actionUrl);
        request.setAttribute("servletPath", request.getServletPath());
        request.getRequestDispatcher("/WEB-INF/views/attendance/attendance_records.jsp")
                .forward(request, response);
    }

    private String buildAttendanceKey(int userId, LocalDate workDate) {
        return userId + "_" + workDate;
    }

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

    private Integer parsePositiveInteger(String value) {
        try {
            int parsed = Integer.parseInt(value);
            return parsed > 0 ? parsed : null;
        } catch (Exception e) {
            return null;
        }
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