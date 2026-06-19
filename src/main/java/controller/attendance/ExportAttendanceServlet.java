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
            // Tạo style chung
            CellStyle companyStyle = createCompanyStyle(workbook);
            CellStyle reportTitleStyle = createReportTitleStyle(workbook);
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);

            createAttendanceLogsSheet(workbook, records, month, year, companyStyle, reportTitleStyle, headerStyle, dataStyle);
            createEmployeeDetailSheet(workbook, records, companyStyle, headerStyle, dataStyle);
            createRuleSheet(workbook, companyStyle, headerStyle, dataStyle);
            createSummarySheet(workbook, records, month, year, companyStyle, headerStyle, dataStyle);

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

    // ====== Các phương thức tạo style ======
    private CellStyle createCompanyStyle(Workbook workbook) {
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 18);
        font.setColor(IndexedColors.DARK_BLUE.getIndex());
        CellStyle style = workbook.createCellStyle();
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

    private CellStyle createReportTitleStyle(Workbook workbook) {
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
        font.setFontHeightInPoints((short) 11);
        CellStyle style = workbook.createCellStyle();
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private CellStyle createDataStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    // ====== Sheet ATTENDANCE_LOGS (có cột employee_name + merge ô) ======
    private void createAttendanceLogsSheet(Workbook workbook, List<AttendanceRecordDTO> records,
                                           int month, int year,
                                           CellStyle companyStyle, CellStyle reportTitleStyle,
                                           CellStyle headerStyle, CellStyle dataStyle) {
        Sheet sheet = workbook.createSheet("ATTENDANCE_LOGS");
        int colCount = 5; // employee_name, work_date, check_in, check_out, status
        int rowIdx = 0;

        // Dòng 1: Tên công ty
        Row companyRow = sheet.createRow(rowIdx++);
        companyRow.setHeight((short) 500);
        Cell companyCell = companyRow.createCell(0);
        companyCell.setCellValue("CÔNG TY QUẢN LÍ NHÂN SỰ HRM");
        companyCell.setCellStyle(companyStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, colCount - 1));

        // Dòng 2: Tiêu đề báo cáo
        Row titleRow = sheet.createRow(rowIdx++);
        titleRow.setHeight((short) 400);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("BÁO CÁO CHẤM CÔNG THÁNG " + month + "/" + year);
        titleCell.setCellStyle(reportTitleStyle);
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, colCount - 1));

        // Dòng 3: Để trống
        rowIdx++;

        // Dòng 4: Header
        Row headerRow = sheet.createRow(rowIdx++);
        String[] headers = {"employee_name", "work_date", "check_in", "check_out", "status"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // Sắp xếp records theo employee_name rồi đến work_date để merge dễ dàng
        List<AttendanceRecordDTO> sortedRecords = records.stream()
                .sorted(Comparator.comparing(AttendanceRecordDTO::getEmployeeName, Comparator.nullsLast(String::compareTo))
                        .thenComparing(AttendanceRecordDTO::getWorkDate))
                .collect(Collectors.toList());

        // Ghi dữ liệu và merge ô employee_name
        String lastEmployeeName = null;
        int mergeStartRow = rowIdx; // dòng bắt đầu của nhóm nhân viên hiện tại

        for (AttendanceRecordDTO r : sortedRecords) {
            Row row = sheet.createRow(rowIdx);
            String currentEmployeeName = r.getEmployeeName() != null ? r.getEmployeeName() : "";

            // Nếu tên nhân viên thay đổi so với dòng trước đó, thực hiện merge cho nhóm cũ
            if (lastEmployeeName != null && !currentEmployeeName.equals(lastEmployeeName)) {
                if (rowIdx - 1 > mergeStartRow) { // có nhiều hơn 1 dòng
                    sheet.addMergedRegion(new CellRangeAddress(mergeStartRow, rowIdx - 1, 0, 0));
                }
                mergeStartRow = rowIdx;
            }
            // Nếu là lần đầu tiên
            if (lastEmployeeName == null) {
                mergeStartRow = rowIdx;
            }

            Cell cell0 = row.createCell(0);
            cell0.setCellValue(currentEmployeeName);
            cell0.setCellStyle(dataStyle);

            Cell cell1 = row.createCell(1);
            cell1.setCellValue(r.getWorkDate().format(DATE_FMT));
            cell1.setCellStyle(dataStyle);

            Cell cell2 = row.createCell(2);
            if (r.getCheckIn() != null) {
                cell2.setCellValue(r.getCheckIn().format(DATETIME_FMT));
            }
            cell2.setCellStyle(dataStyle);

            Cell cell3 = row.createCell(3);
            if (r.getCheckOut() != null) {
                cell3.setCellValue(r.getCheckOut().format(DATETIME_FMT));
            }
            cell3.setCellStyle(dataStyle);

            Cell cell4 = row.createCell(4);
            cell4.setCellValue(r.getStatus() != null ? r.getStatus() : "");
            cell4.setCellStyle(dataStyle);

            lastEmployeeName = currentEmployeeName;
            rowIdx++;
        }

        // Merge cho nhóm cuối cùng
        if (rowIdx - 1 > mergeStartRow && lastEmployeeName != null) {
            sheet.addMergedRegion(new CellRangeAddress(mergeStartRow, rowIdx - 1, 0, 0));
        }

        // Auto size cột
        for (int i = 0; i < colCount; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    // ====== Sheet CHI TIẾT NHÂN VIÊN ======
    private void createEmployeeDetailSheet(Workbook workbook, List<AttendanceRecordDTO> records,
                                           CellStyle companyStyle, CellStyle headerStyle, CellStyle dataStyle) {
        Sheet sheet = workbook.createSheet("CHI TIẾT NHÂN VIÊN");
        int colCount = 4; // employee_id, employee_code, full_name, position
        int rowIdx = 0;

        // Dòng công ty
        Row companyRow = sheet.createRow(rowIdx++);
        companyRow.setHeight((short) 500);
        Cell companyCell = companyRow.createCell(0);
        companyCell.setCellValue("CÔNG TY QUẢN LÍ NHÂN SỰ HRM");
        companyCell.setCellStyle(companyStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, colCount - 1));

        rowIdx++; // cách dòng

        // Header
        Row headerRow = sheet.createRow(rowIdx++);
        String[] headers = {"employee_id", "employee_code", "full_name", "position"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // Lọc nhân viên duy nhất (giữ nguyên thứ tự xuất hiện)
        Map<Integer, AttendanceRecordDTO> uniqueEmployees = new LinkedHashMap<>();
        for (AttendanceRecordDTO r : records) {
            uniqueEmployees.putIfAbsent(r.getUserId(), r);
        }

        for (AttendanceRecordDTO emp : uniqueEmployees.values()) {
            Row row = sheet.createRow(rowIdx++);
            Cell cell0 = row.createCell(0);
            cell0.setCellValue(emp.getUserId());
            cell0.setCellStyle(dataStyle);

            Cell cell1 = row.createCell(1);
            cell1.setCellValue(emp.getEmployeeCode());
            cell1.setCellStyle(dataStyle);

            Cell cell2 = row.createCell(2);
            cell2.setCellValue(emp.getEmployeeName());
            cell2.setCellStyle(dataStyle);

            Cell cell3 = row.createCell(3);
            cell3.setCellValue(emp.getPositionName() != null ? emp.getPositionName() : "");
            cell3.setCellStyle(dataStyle);
        }

        for (int i = 0; i < colCount; i++) sheet.autoSizeColumn(i);
    }

    // ====== Sheet CHÚ THÍCH ======
    private void createRuleSheet(Workbook workbook, CellStyle companyStyle, CellStyle headerStyle, CellStyle dataStyle) {
        Sheet sheet = workbook.createSheet("CHÚ THÍCH");
        int colCount = 3;
        int rowIdx = 0;

        // Dòng công ty
        Row companyRow = sheet.createRow(rowIdx++);
        companyRow.setHeight((short) 500);
        Cell companyCell = companyRow.createCell(0);
        companyCell.setCellValue("CÔNG TY QUẢN LÍ NHÂN SỰ HRM");
        companyCell.setCellStyle(companyStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, colCount - 1));

        rowIdx++;

        // Header
        Row headerRow = sheet.createRow(rowIdx++);
        String[] headers = {"Rule", "Mô tả", "Trạng thái"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        String[][] rules = {
                {"1", "Check-in <= 08:05", "ON_TIME"},
                {"2", "Check-in > 08:05", "LATE"},
                {"3", "Check-out < 17:00", "EARLY_LEAVE"},
                {"4", "Check-in > 08:05 AND Check-out < 17:00", "LATE & EARLY_LEAVE"},
                {"5", "Check-in IS NULL AND Check-out IS NULL", "ABSENT"},
                {"6", "Check-in IS NULL AND Check-out IS NOT NULL", "FORGOT_CHECK_IN"},
                {"7", "Check-in IS NOT NULL AND Check-out IS NULL", "FORGOT_CHECK_OUT"}
        };
        for (String[] rule : rules) {
            Row row = sheet.createRow(rowIdx++);
            for (int i = 0; i < rule.length; i++) {
                Cell cell = row.createCell(i);
                cell.setCellValue(rule[i]);
                cell.setCellStyle(dataStyle);
            }
        }
        for (int i = 0; i < colCount; i++) sheet.autoSizeColumn(i);
    }

    // ====== Sheet TỔNG HỢP ======
    private void createSummarySheet(Workbook workbook, List<AttendanceRecordDTO> records,
                                    int month, int year,
                                    CellStyle companyStyle, CellStyle headerStyle, CellStyle dataStyle) {
        Sheet sheet = workbook.createSheet("TỔNG HỢP");
        int colCount = 11; // Mã NV, Họ tên, Chức vụ, Phòng ban, Tổng ngày công, Ngày có mặt, Ngày vắng, Đi muộn, Về sớm, Quên check-in, Quên check-out
        int rowIdx = 0;

        // Tiêu đề "TỔNG HỢP CHẤM CÔNG"
        Row titleRow = sheet.createRow(rowIdx++);
        titleRow.setHeight((short) 500);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("TỔNG HỢP CHẤM CÔNG");
        titleCell.setCellStyle(companyStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, colCount - 1));

        rowIdx++; // cách dòng

        // Header
        Row headerRow = sheet.createRow(rowIdx++);
        String[] headers = {"Mã NV", "Họ tên", "Chức vụ", "Phòng ban", "Tổng ngày công",
                "Ngày có mặt", "Ngày vắng", "Đi muộn", "Về sớm", "Quên check-in", "Quên check-out"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // Gom nhóm theo userId
        Map<Integer, List<AttendanceRecordDTO>> grouped = records.stream()
                .collect(Collectors.groupingBy(AttendanceRecordDTO::getUserId, LinkedHashMap::new, Collectors.toList()));

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
            Cell c0 = row.createCell(0);
            c0.setCellValue(first.getEmployeeCode());
            c0.setCellStyle(dataStyle);
            Cell c1 = row.createCell(1);
            c1.setCellValue(first.getEmployeeName());
            c1.setCellStyle(dataStyle);
            Cell c2 = row.createCell(2);
            c2.setCellValue(first.getPositionName() != null ? first.getPositionName() : "");
            c2.setCellStyle(dataStyle);
            Cell c3 = row.createCell(3);
            c3.setCellValue(first.getDepartmentName() != null ? first.getDepartmentName() : "");
            c3.setCellStyle(dataStyle);
            Cell c4 = row.createCell(4);
            c4.setCellValue(totalDays);
            c4.setCellStyle(dataStyle);
            Cell c5 = row.createCell(5);
            c5.setCellValue(presentDays);
            c5.setCellStyle(dataStyle);
            Cell c6 = row.createCell(6);
            c6.setCellValue(absentDays);
            c6.setCellStyle(dataStyle);
            Cell c7 = row.createCell(7);
            c7.setCellValue(lateCount);
            c7.setCellStyle(dataStyle);
            Cell c8 = row.createCell(8);
            c8.setCellValue(earlyCount);
            c8.setCellStyle(dataStyle);
            Cell c9 = row.createCell(9);
            c9.setCellValue(forgotCheckin);
            c9.setCellStyle(dataStyle);
            Cell c10 = row.createCell(10);
            c10.setCellValue(forgotCheckout);
            c10.setCellStyle(dataStyle);
        }

        for (int i = 0; i < colCount; i++) {
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