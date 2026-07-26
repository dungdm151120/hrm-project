package controller.attendance;

import dao.AttendanceDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import model.AttendanceLog;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import util.DBConnection;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@WebServlet("/admin/attendance/import")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 2,  // 2MB
        maxFileSize = 1024 * 1024 * 10,       // 10MB
        maxRequestSize = 1024 * 1024 * 50     // 50MB
)
public class ImportAttendanceServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/attendance/import.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            Part filePart = req.getPart("excelFile");
            if (filePart == null || filePart.getSize() == 0) {
                req.setAttribute("importError", "Import failed: Please select an Excel file.");
                req.getRequestDispatcher("/WEB-INF/views/attendance/import.jsp").forward(req, resp);
                return;
            }

            InputStream fileContent = filePart.getInputStream();
            Workbook workbook = new XSSFWorkbook(fileContent);
            Sheet sheet = workbook.getSheetAt(0);

            List<AttendanceLog> logs = new ArrayList<>();

            // 1. Fetch existing employee IDs from system
            Set<Integer> allUserIds = new HashSet<>();
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement("SELECT id FROM users");
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    allUserIds.add(rs.getInt("id"));
                }
            }

            // 2. Fetch holidays from system
            Map<LocalDate, String> holidayMap = new HashMap<>();
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement("SELECT holiday_date, holiday_name FROM holidays");
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    holidayMap.put(rs.getDate("holiday_date").toLocalDate(), rs.getString("holiday_name"));
                }
            }

            int lastRow = sheet.getLastRowNum();
            for (int i = 1; i <= lastRow; i++) {
                Row row = sheet.getRow(i);
                if (row == null || isRowEmpty(row)) continue;

                int rowNum = i + 1; // 1-based line number in Excel

                // A. Check Employee ID
                Cell cell0 = row.getCell(0);
                Integer employeeId = null;
                if (cell0 != null) {
                    if (cell0.getCellType() == CellType.NUMERIC) {
                        employeeId = (int) cell0.getNumericCellValue();
                    } else if (cell0.getCellType() == CellType.STRING) {
                        String str = cell0.getStringCellValue().trim();
                        if (!str.isEmpty()) {
                            try {
                                employeeId = Integer.parseInt(str);
                            } catch (NumberFormatException ignored) {}
                        }
                    }
                }

                if (employeeId == null || !allUserIds.contains(employeeId)) {
                    String idDisplay = (employeeId != null) ? String.valueOf(employeeId) : (cell0 != null ? cell0.toString().trim() : "");
                    workbook.close();
                    throw new Exception("Import failed at row " + rowNum + ": Employee (" + idDisplay + ") does not exist in system. Please check import file again.");
                }

                // B. Check Work Date
                Cell cell1 = row.getCell(1);
                LocalDate workDate = getCellLocalDate(cell1);
                if (workDate == null) {
                    workbook.close();
                    throw new Exception("Invalid date import at row " + rowNum + ": Missing or invalid date format. Please check import file again.");
                }

                DayOfWeek dayOfWeek = workDate.getDayOfWeek();
                if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
                    String dayName = dayOfWeek.toString();
                    dayName = dayName.charAt(0) + dayName.substring(1).toLowerCase();
                    workbook.close();
                    throw new Exception("Invalid date import: " + workDate + " is a weekend (" + dayName + ") at row " + rowNum + ". Please check import file again.");
                }

                if (holidayMap.containsKey(workDate)) {
                    String holidayName = holidayMap.get(workDate);
                    workbook.close();
                    throw new Exception("Invalid date import: " + workDate + " is a holiday (" + holidayName + ") at row " + rowNum + ". Please check import file again.");
                }

                // C. Check Check-in and Check-out Time
                Cell cell2 = row.getCell(2);
                Cell cell3 = row.getCell(3);

                LocalDateTime checkIn = getCellLocalDateTime(cell2, workDate);
                LocalDateTime checkOut = getCellLocalDateTime(cell3, workDate);

                if (checkIn != null && checkOut != null && checkIn.isAfter(checkOut)) {
                    DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("HH:mm");
                    String inTimeStr = checkIn.format(timeFmt);
                    String outTimeStr = checkOut.format(timeFmt);
                    workbook.close();
                    throw new Exception("Import failed at row " + rowNum + ": Check-in time (" + inTimeStr + ") is later than check-out time (" + outTimeStr + "). Please check import file again.");
                }

                AttendanceLog log = new AttendanceLog();
                log.setEmployeeId(employeeId);
                log.setWorkDate(workDate);
                log.setCheckIn(checkIn);
                log.setCheckOut(checkOut);
                logs.add(log);
            }

            workbook.close();

            if (logs.isEmpty()) {
                req.setAttribute("importError", "Import failed: Excel file contains no data rows.");
                req.getRequestDispatcher("/WEB-INF/views/attendance/import.jsp").forward(req, resp);
                return;
            }

            // 3. Save logs to database only if ALL rows are valid
            AttendanceDAO dao = new AttendanceDAO();
            int count = dao.saveAllAttendanceLogs(logs);

            dao.processAllPendingLogs();

            req.getSession().setAttribute("importSuccess", "Imported " + count + " records successfully!");
            resp.sendRedirect(req.getContextPath() + "/admin/attendance/import");

        } catch (Exception e) {
            req.setAttribute("importError", e.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/attendance/import.jsp").forward(req, resp);
        }
    }

    private boolean isRowEmpty(Row row) {
        if (row == null) return true;
        for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
            Cell cell = row.getCell(c);
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                return false;
            }
        }
        return true;
    }

    private LocalDate getCellLocalDate(Cell cell) {
        if (cell == null) return null;
        try {
            if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
                Date date = cell.getDateCellValue();
                return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            } else if (cell.getCellType() == CellType.STRING) {
                String str = cell.getStringCellValue().trim();
                if (str.isEmpty()) return null;
                try {
                    return LocalDate.parse(str);
                } catch (Exception e1) {
                    DateTimeFormatter fmt = DateTimeFormatter.ofPattern("[dd/MM/yyyy][d/M/yyyy][yyyy/MM/dd]");
                    return LocalDate.parse(str, fmt);
                }
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    private LocalDateTime getCellLocalDateTime(Cell cell, LocalDate workDate) {
        if (cell == null) return null;
        try {
            if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
                Date date = cell.getDateCellValue();
                LocalDateTime ldt = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                if (ldt.getYear() < 2000 && workDate != null) {
                    return LocalDateTime.of(workDate, ldt.toLocalTime());
                }
                return ldt;
            } else if (cell.getCellType() == CellType.STRING) {
                String str = cell.getStringCellValue().trim();
                if (str.isEmpty()) return null;
                String normalized = str.replace(" ", "T");
                try {
                    return LocalDateTime.parse(normalized);
                } catch (Exception e1) {
                    try {
                        LocalTime lt = LocalTime.parse(str);
                        if (workDate != null) {
                            return LocalDateTime.of(workDate, lt);
                        }
                    } catch (Exception e2) {
                        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                        return LocalDateTime.parse(str, fmt);
                    }
                }
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }
}
