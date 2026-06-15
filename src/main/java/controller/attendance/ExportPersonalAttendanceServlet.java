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
import java.util.List;

@WebServlet("/admin/attendance/exportPersonal")
public class ExportPersonalAttendanceServlet extends HttpServlet {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            int userId = Integer.parseInt(req.getParameter("userId"));
            int month = req.getParameter("month") != null ?
                    Integer.parseInt(req.getParameter("month")) : LocalDate.now().getMonthValue();
            int year = req.getParameter("year") != null ?
                    Integer.parseInt(req.getParameter("year")) : LocalDate.now().getYear();

            AttendanceDAO attendanceDAO = new AttendanceDAO();
            List<AttendanceRecordDTO> records = attendanceDAO.getAttendanceDetailByUserAndMonth(userId, month, year);

            if (records.isEmpty()) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "No attendance data found");
                return;
            }

            AttendanceRecordDTO first = records.get(0);
            String employeeCode = first.getEmployeeCode();
            String employeeName = first.getEmployeeName();
            String position = first.getPositionName() != null ? first.getPositionName() : "";
            String department = first.getDepartmentName() != null ? first.getDepartmentName() : "";

            Workbook workbook = new XSSFWorkbook();
            createAttendanceLogsSheet(workbook, records);
            createDetailSheet(workbook, first, position);
            createRuleSheet(workbook);
            createSummarySheet(workbook, first, records, month, year);

            resp.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            String filename = "attendance_" + employeeCode + "_" + year + "_" + month + ".xlsx";
            resp.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
            workbook.write(resp.getOutputStream());
            workbook.close();
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error generating Excel file");
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

    private void createDetailSheet(Workbook workbook, AttendanceRecordDTO first, String position) {
        Sheet sheet = workbook.createSheet("CHI TIẾT NHÂN VIÊN");
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("employee_id");
        headerRow.createCell(1).setCellValue("employee_code");
        headerRow.createCell(2).setCellValue("full_name");
        headerRow.createCell(3).setCellValue("position");

        Row row = sheet.createRow(1);
        row.createCell(0).setCellValue(first.getUserId());
        row.createCell(1).setCellValue(first.getEmployeeCode());
        row.createCell(2).setCellValue(first.getEmployeeName());
        row.createCell(3).setCellValue(position);

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

    private void createSummarySheet(Workbook workbook, AttendanceRecordDTO first,
                                    List<AttendanceRecordDTO> records, int month, int year) {
        Sheet sheet = workbook.createSheet("TỔNG HỢP");

        Row titleRow = sheet.createRow(0);
        titleRow.createCell(0).setCellValue("BÁO CÁO CHẤM CÔNG CÁ NHÂN");
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 2));

        int r = 2;
        createInfoRow(sheet, r++, "Mã NV:", first.getEmployeeCode());
        createInfoRow(sheet, r++, "Họ tên:", first.getEmployeeName());
        createInfoRow(sheet, r++, "Chức vụ:", first.getPositionName() != null ? first.getPositionName() : "");
        createInfoRow(sheet, r++, "Phòng ban:", first.getDepartmentName() != null ? first.getDepartmentName() : "");
        createInfoRow(sheet, r++, "Tháng/Năm:", month + "/" + year);

        int totalDays = records.size();
        long presentDays = records.stream().filter(rec -> !"ABSENT".equals(rec.getStatus())).count();
        long absentDays = records.stream().filter(rec -> "ABSENT".equals(rec.getStatus())).count();
        long lateCount = records.stream().filter(rec -> {
            String s = rec.getStatus();
            return s != null && (s.contains("LATE") || s.equals("LATE_AND_EARLY_LEAVE"));
        }).count();
        long earlyCount = records.stream().filter(rec -> {
            String s = rec.getStatus();
            return s != null && s.contains("EARLY_LEAVE");
        }).count();
        long forgotCheckin = records.stream().filter(rec -> "FORGOT_CHECK_IN".equals(rec.getStatus())).count();
        long forgotCheckout = records.stream().filter(rec -> "FORGOT_CHECK_OUT".equals(rec.getStatus())).count();

        r += 1;
        Row headerRow = sheet.createRow(r++);
        headerRow.createCell(0).setCellValue("Chỉ số");
        headerRow.createCell(1).setCellValue("Số lượng");

        addSummaryRow(sheet, r++, "Tổng ngày công trong tháng", totalDays);
        addSummaryRow(sheet, r++, "Ngày có mặt", presentDays);
        addSummaryRow(sheet, r++, "Ngày vắng", absentDays);
        addSummaryRow(sheet, r++, "Số lần đi muộn", lateCount);
        addSummaryRow(sheet, r++, "Số lần về sớm", earlyCount);
        addSummaryRow(sheet, r++, "Quên check-in", forgotCheckin);
        addSummaryRow(sheet, r++, "Quên check-out", forgotCheckout);

        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
    }

    private void createInfoRow(Sheet sheet, int rowIdx, String label, String value) {
        Row row = sheet.createRow(rowIdx);
        row.createCell(0).setCellValue(label);
        row.createCell(1).setCellValue(value);
    }

    private void addSummaryRow(Sheet sheet, int rowIdx, String label, long value) {
        Row row = sheet.createRow(rowIdx);
        row.createCell(0).setCellValue(label);
        row.createCell(1).setCellValue(value);
    }
}