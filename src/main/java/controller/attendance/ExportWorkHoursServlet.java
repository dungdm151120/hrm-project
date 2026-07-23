package controller.attendance;

import dao.AttendanceDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.AttendanceRecordDTO;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/attendance/work-hours/export")
public class ExportWorkHoursServlet extends HttpServlet {
    private static final DateTimeFormatter DAY_FORMAT = DateTimeFormatter.ofPattern("dd/MM");
    private final AttendanceDAO attendanceDAO = new AttendanceDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Integer month = parseInteger(request.getParameter("month"));
        Integer year = parseInteger(request.getParameter("year"));
        if (month == null || month < 1 || month > 12 || year == null || year < 2000 || year > 2100) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid month or year.");
            return;
        }

        Integer departmentId = parsePositiveInteger(request.getParameter("departmentId"));
        String keyword = normalizeKeyword(request.getParameter("keyword"));
        attendanceDAO.createHolidayRecordsForMonth(year, month);

        List<AttendanceRecordDTO> records = attendanceDAO.getAllAttendanceRecordsForExport(
                month, year, departmentId, keyword);
        if (records.isEmpty()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "No work hour data found for the selected criteria.");
            return;
        }

        YearMonth period = YearMonth.of(year, month);
        List<LocalDate> holidayDates = attendanceDAO.getHolidayDatesInMonth(year, month);
        double standardHours = calculateStandardHours(period, holidayDates);

        Map<Integer, AttendanceRecordDTO> employees = new LinkedHashMap<>();
        Map<String, AttendanceRecordDTO> attendanceByDay = new LinkedHashMap<>();
        Map<Integer, Double> totalWorkHours = new LinkedHashMap<>();
        Map<Integer, Double> totalOvertimeHours = new LinkedHashMap<>();
        for (AttendanceRecordDTO record : records) {
            employees.putIfAbsent(record.getUserId(), record);
            attendanceByDay.put(buildAttendanceKey(record.getUserId(), record.getWorkDate()), record);
            totalWorkHours.merge(record.getUserId(), valueOrZero(record.getTotalWorkHours()), Double::sum);
            totalOvertimeHours.merge(record.getUserId(), valueOrZero(record.getOvertimeHours()), Double::sum);
        }

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Work Hours Summary");
            CellStyle titleStyle = createTitleStyle(workbook);
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);
            int totalColumns = period.lengthOfMonth() + 6;

            Row titleRow = sheet.createRow(0);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("WORK HOURS SUMMARY " + month + "/" + year);
            titleCell.setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, totalColumns - 1));

            Row headerRow = sheet.createRow(2);
            String[] summaryHeaders = {
                "Employee Code", "Employee Name", "Department", "Total Work Hours", "Standard Hours", "Total OT Hours"
            };
            for (int index = 0; index < summaryHeaders.length; index++) {
                createCell(headerRow, index, summaryHeaders[index], headerStyle);
            }
            for (int day = 1; day <= period.lengthOfMonth(); day++) {
                createCell(headerRow, summaryHeaders.length + day - 1,
                        period.atDay(day).format(DAY_FORMAT), headerStyle);
            }

            int rowIndex = 3;
            for (AttendanceRecordDTO employee : employees.values()) {
                Row row = sheet.createRow(rowIndex++);
                createCell(row, 0, employee.getEmployeeCode(), dataStyle);
                createCell(row, 1, employee.getEmployeeName(), dataStyle);
                createCell(row, 2, employee.getDepartmentName(), dataStyle);
                createCell(row, 3, totalWorkHours.getOrDefault(employee.getUserId(), 0.0), dataStyle);
                createCell(row, 4, standardHours, dataStyle);
                createCell(row, 5, totalOvertimeHours.getOrDefault(employee.getUserId(), 0.0), dataStyle);

                for (int day = 1; day <= period.lengthOfMonth(); day++) {
                    AttendanceRecordDTO record = attendanceByDay.get(
                            buildAttendanceKey(employee.getUserId(), period.atDay(day)));
                    createCell(row, summaryHeaders.length + day - 1,
                            record == null ? "--" : formatDailyValue(record), dataStyle);
                }
            }

            for (int column = 0; column < totalColumns; column++) {
                sheet.autoSizeColumn(column);
            }

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=\"work_hours_summary_"
                    + year + "_" + month + ".xlsx\"");
            workbook.write(response.getOutputStream());
        }
    }

    private double calculateStandardHours(YearMonth period, List<LocalDate> holidayDates) {
        int standardWorkdays = 0;
        for (int day = 1; day <= period.lengthOfMonth(); day++) {
            LocalDate date = period.atDay(day);
            if (date.getDayOfWeek() != DayOfWeek.SATURDAY
                    && date.getDayOfWeek() != DayOfWeek.SUNDAY
                    && !holidayDates.contains(date)) {
                standardWorkdays++;
            }
        }
        return standardWorkdays * 8.0;
    }

    private String formatDailyValue(AttendanceRecordDTO record) {
        if ("ON_LEAVE".equals(record.getStatus())) return "On leave";
        if ("SICK_LEAVE".equals(record.getStatus())) return "Sick leave";
        if ("HOLIDAY".equals(record.getStatus())) return "Holiday";
        return String.format("%.1f hrs", valueOrZero(record.getTotalWorkHours()));
    }

    private CellStyle createTitleStyle(Workbook workbook) {
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 14);
        font.setColor(IndexedColors.DARK_BLUE.getIndex());
        CellStyle style = workbook.createCellStyle();
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        Font font = workbook.createFont();
        font.setBold(true);
        CellStyle style = createDataStyle(workbook);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    private CellStyle createDataStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private void createCell(Row row, int index, String value, CellStyle style) {
        Cell cell = row.createCell(index);
        cell.setCellValue(value == null ? "" : value);
        cell.setCellStyle(style);
    }

    private void createCell(Row row, int index, double value, CellStyle style) {
        Cell cell = row.createCell(index);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }

    private String buildAttendanceKey(int userId, LocalDate date) {
        return userId + "_" + date;
    }

    private double valueOrZero(Double value) {
        return value == null ? 0.0 : value;
    }

    private Integer parseInteger(String value) {
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            return null;
        }
    }

    private Integer parsePositiveInteger(String value) {
        Integer parsed = parseInteger(value);
        return parsed != null && parsed > 0 ? parsed : null;
    }

    private String normalizeKeyword(String keyword) {
        return keyword == null ? "" : keyword.trim().replaceAll("\\s+", " ");
    }
}
