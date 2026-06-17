package controller.attendance;

import dao.AttendanceDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.AttendanceRecordDTO;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@WebServlet("/attendance/export")
public class ExportAttendanceServlet extends HttpServlet {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            int month = Integer.parseInt(req.getParameter("month"));
            int year = Integer.parseInt(req.getParameter("year"));
            Integer departmentId = parseInteger(req.getParameter("departmentId"));
            String keyword = req.getParameter("keyword");

            AttendanceDAO dao = new AttendanceDAO();
            List<AttendanceRecordDTO> records = dao.getAllAttendanceRecordsForExport(month, year, departmentId, keyword);

            if (records.isEmpty()) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "No attendance data found for the selected criteria.");
                return;
            }

            Workbook workbook = new XSSFWorkbook();
            createAttendanceLogsSheet(workbook, records);
            createEmployeeDetailSheet(workbook, records);
            createRuleSheet(workbook);
            createSummarySheet(workbook, records, month, year);

            resp.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            String filename = "attendance_report_" + year + "_" + month + ".xlsx";
            resp.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
            workbook.write(resp.getOutputStream());
            workbook.close();
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error generating Excel report.");
        }
    }

    private void createAttendanceLogsSheet(Workbook workbook, List<AttendanceRecordDTO> records) {
        Sheet sheet = workbook.createSheet("ATTENDANCE_LOGS");
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("employee_id");
        headerRow.createCell(1).setCellValue("employee_name");
        headerRow.createCell(2).setCellValue("work_date");
        headerRow.createCell(3).setCellValue("check_in");
        headerRow.createCell(4).setCellValue("check_out");
        headerRow.createCell(5).setCellValue("status");

        int rowIdx = 1;
        for (AttendanceRecordDTO r : records) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(r.getUserId());
            row.createCell(1).setCellValue(r.getEmployeeName());
            row.createCell(2).setCellValue(r.getWorkDate().format(DATE_FMT));
            if (r.getCheckIn() != null) {
                row.createCell(3).setCellValue(r.getCheckIn().format(DATETIME_FMT));
            }
            if (r.getCheckOut() != null) {
                row.createCell(4).setCellValue(r.getCheckOut().format(DATETIME_FMT));
            }
            row.createCell(5).setCellValue(r.getStatus() != null ? r.getStatus() : "");
        }
        for (int i = 0; i < 6; i++) sheet.autoSizeColumn(i);
    }

    private void createEmployeeDetailSheet(Workbook workbook, List<AttendanceRecordDTO> records) {
        Sheet sheet = workbook.createSheet("CHI TIẾT NHÂN VIÊN");
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("employee_id");
        headerRow.createCell(1).setCellValue("employee_code");
        headerRow.createCell(2).setCellValue("full_name");
        headerRow.createCell(3).setCellValue("position");

        Map<Integer, AttendanceRecordDTO> uniqueEmployees = new LinkedHashMap<>();
        for (AttendanceRecordDTO r : records) {
            uniqueEmployees.putIfAbsent(r.getUserId(), r);
        }

        int rowIdx = 1;
        for (AttendanceRecordDTO emp : uniqueEmployees.values()) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(emp.getUserId());
            row.createCell(1).setCellValue(emp.getEmployeeCode());
            row.createCell(2).setCellValue(emp.getEmployeeName());
            row.createCell(3).setCellValue(emp.getPositionName() != null ? emp.getPositionName() : "");
        }
        for (int i = 0; i < 4; i++) sheet.autoSizeColumn(i);
    }

    private void createRuleSheet(Workbook workbook) {
        Sheet sheet = workbook.createSheet("CHÚ THÍCH");
        Row titleRow = sheet.createRow(0);
        titleRow.createCell(0).setCellValue("QUY TẮC ĐÁNH GIÁ CHẤM CÔNG");
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 2));

        Row headerRow = sheet.createRow(2);
        headerRow.createCell(0).setCellValue("Rule");
        headerRow.createCell(1).setCellValue("Mô tả");
        headerRow.createCell(2).setCellValue("Trạng thái");

        String[][] rules = {
                {"1", "Check-in <= 08:05", "ON_TIME"},
                {"2", "Check-in > 08:05", "LATE"},
                {"3", "Check-out < 17:00", "EARLY_LEAVE"},
                {"4", "Check-in > 08:05 AND Check-out < 17:00", "LATE & EARLY_LEAVE"},
                {"5", "Check-in IS NULL AND Check-out IS NULL", "ABSENT"},
                {"6", "Check-in IS NULL AND Check-out IS NOT NULL", "FORGOT_CHECK_IN"},
                {"7", "Check-in IS NOT NULL AND Check-out IS NULL", "FORGOT_CHECK_OUT"}
        };
        int rowIdx = 3;
        for (String[] rule : rules) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(rule[0]);
            row.createCell(1).setCellValue(rule[1]);
            row.createCell(2).setCellValue(rule[2]);
        }
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
        sheet.autoSizeColumn(2);
    }

    private void createSummarySheet(Workbook workbook, List<AttendanceRecordDTO> records, int month, int year) {
        Sheet sheet = workbook.createSheet("TỔNG HỢP");

        Row titleRow = sheet.createRow(0);
        titleRow.createCell(0).setCellValue("BÁO CÁO CHẤM CÔNG TỔNG HỢP");
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 8));

        Row headerRow = sheet.createRow(2);
        String[] headers = {"Mã NV", "Họ tên", "Chức vụ", "Phòng ban", "Tổng ngày công",
                "Ngày có mặt", "Ngày vắng", "Đi muộn", "Về sớm", "Quên check-in", "Quên check-out"};
        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i]);
        }

        Map<Integer, List<AttendanceRecordDTO>> grouped = records.stream()
                .collect(Collectors.groupingBy(AttendanceRecordDTO::getUserId, LinkedHashMap::new, Collectors.toList()));

        int rowIdx = 3;
        for (List<AttendanceRecordDTO> userRecords : grouped.values()) {
            AttendanceRecordDTO first = userRecords.get(0);
            int totalDays = userRecords.size();
            long presentDays = userRecords.stream().filter(r -> !"ABSENT".equals(r.getStatus())).count();
            long absentDays = userRecords.stream().filter(r -> "ABSENT".equals(r.getStatus())).count();
            long lateCount = userRecords.stream().filter(r -> {
                String s = r.getStatus();
                return s != null && (s.contains("LATE") || s.equals("LATE_AND_EARLY_LEAVE"));
            }).count();
            long earlyCount = userRecords.stream().filter(r -> {
                String s = r.getStatus();
                return s != null && s.contains("EARLY_LEAVE");
            }).count();
            long forgotCheckin = userRecords.stream().filter(r -> "FORGOT_CHECK_IN".equals(r.getStatus())).count();
            long forgotCheckout = userRecords.stream().filter(r -> "FORGOT_CHECK_OUT".equals(r.getStatus())).count();

            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(first.getEmployeeCode());
            row.createCell(1).setCellValue(first.getEmployeeName());
            row.createCell(2).setCellValue(first.getPositionName() != null ? first.getPositionName() : "");
            row.createCell(3).setCellValue(first.getDepartmentName() != null ? first.getDepartmentName() : "");
            row.createCell(4).setCellValue(totalDays);
            row.createCell(5).setCellValue(presentDays);
            row.createCell(6).setCellValue(absentDays);
            row.createCell(7).setCellValue(lateCount);
            row.createCell(8).setCellValue(earlyCount);
            row.createCell(9).setCellValue(forgotCheckin);
            row.createCell(10).setCellValue(forgotCheckout);
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private Integer parseInteger(String value) {
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            return null;
        }
    }
}