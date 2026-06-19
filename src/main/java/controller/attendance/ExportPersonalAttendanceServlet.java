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
    private static final DateTimeFormatter MONTH_YEAR_FMT = DateTimeFormatter.ofPattern("MM/yyyy");

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
            // Tạo style chung
            CellStyle companyStyle = createCompanyStyle(workbook);
            CellStyle reportTitleStyle = createReportTitleStyle(workbook);
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);

            createAttendanceLogsSheet(workbook, records, month, year, companyStyle, reportTitleStyle, headerStyle, dataStyle);
            createDetailSheet(workbook, first, position, companyStyle, headerStyle, dataStyle);
            createRuleSheet(workbook, companyStyle, headerStyle, dataStyle);
            createSummarySheet(workbook, first, records, month, year, companyStyle, headerStyle, dataStyle);

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

    // ====== Sheet ATTENDANCE_LOGS (chỉ còn 4 cột) ======
    private void createAttendanceLogsSheet(Workbook workbook, List<AttendanceRecordDTO> records,
                                           int month, int year,
                                           CellStyle companyStyle, CellStyle reportTitleStyle,
                                           CellStyle headerStyle, CellStyle dataStyle) {
        Sheet sheet = workbook.createSheet("ATTENDANCE_LOGS");
        int colCount = 4; // work_date, check_in, check_out, status
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

        // Dòng 3: Để trống một dòng
        rowIdx++;

        // Dòng 4: Header
        Row headerRow = sheet.createRow(rowIdx++);
        String[] headers = {"work_date", "check_in", "check_out", "status"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // Dữ liệu
        for (AttendanceRecordDTO r : records) {
            Row row = sheet.createRow(rowIdx++);
            Cell cell0 = row.createCell(0);
            cell0.setCellValue(r.getWorkDate().format(DATE_FMT));
            cell0.setCellStyle(dataStyle);

            Cell cell1 = row.createCell(1);
            if (r.getCheckIn() != null) {
                cell1.setCellValue(r.getCheckIn().format(DATETIME_FMT));
            }
            cell1.setCellStyle(dataStyle);

            Cell cell2 = row.createCell(2);
            if (r.getCheckOut() != null) {
                cell2.setCellValue(r.getCheckOut().format(DATETIME_FMT));
            }
            cell2.setCellStyle(dataStyle);

            Cell cell3 = row.createCell(3);
            cell3.setCellValue(r.getStatus() != null ? r.getStatus() : "");
            cell3.setCellStyle(dataStyle);
        }

        // Auto size cột
        for (int i = 0; i < colCount; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    // ====== Sheet CHI TIẾT NHÂN VIÊN (thêm dòng công ty) ======
    private void createDetailSheet(Workbook workbook, AttendanceRecordDTO first, String position,
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

        rowIdx++; // cách 1 dòng

        // Header
        Row headerRow = sheet.createRow(rowIdx++);
        String[] headers = {"employee_id", "employee_code", "full_name", "position"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // Data
        Row dataRow = sheet.createRow(rowIdx++);
        Cell cell0 = dataRow.createCell(0);
        cell0.setCellValue(first.getUserId());
        cell0.setCellStyle(dataStyle);

        Cell cell1 = dataRow.createCell(1);
        cell1.setCellValue(first.getEmployeeCode());
        cell1.setCellStyle(dataStyle);

        Cell cell2 = dataRow.createCell(2);
        cell2.setCellValue(first.getEmployeeName());
        cell2.setCellStyle(dataStyle);

        Cell cell3 = dataRow.createCell(3);
        cell3.setCellValue(position);
        cell3.setCellStyle(dataStyle);

        for (int i = 0; i < colCount; i++) sheet.autoSizeColumn(i);
    }

    // ====== Sheet CHÚ THÍCH (thêm dòng công ty) ======
    private void createRuleSheet(Workbook workbook, CellStyle companyStyle, CellStyle headerStyle, CellStyle dataStyle) {
        Sheet sheet = workbook.createSheet("CHÚ THÍCH");
        int colCount = 3; // Rule, Mô tả, Trạng thái
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

    // ====== Sheet TỔNG HỢP (thêm dòng công ty) ======
    private void createSummarySheet(Workbook workbook, AttendanceRecordDTO first,
                                    List<AttendanceRecordDTO> records, int month, int year,
                                    CellStyle companyStyle, CellStyle headerStyle, CellStyle dataStyle) {
        Sheet sheet = workbook.createSheet("TỔNG HỢP");
        int colCount = 2;
        int rowIdx = 0;

        // Tiêu đề chính
        Row mainTitleRow = sheet.createRow(rowIdx++);
        mainTitleRow.setHeight((short) 500);
        Cell mainTitleCell = mainTitleRow.createCell(0);
        mainTitleCell.setCellValue("TỔNG HỢP CHẤM CÔNG");
        mainTitleCell.setCellStyle(companyStyle); // dùng style to, đậm
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, colCount - 1));

        rowIdx++; // cách dòng

        // Thông tin nhân viên (không cần dòng tiêu đề riêng)
        String[][] info = {
                {"Mã NV:", first.getEmployeeCode()},
                {"Họ tên:", first.getEmployeeName()},
                {"Chức vụ:", first.getPositionName() != null ? first.getPositionName() : ""},
                {"Phòng ban:", first.getDepartmentName() != null ? first.getDepartmentName() : ""},
                {"Tháng/Năm:", month + "/" + year}
        };
        for (String[] rowData : info) {
            Row r = sheet.createRow(rowIdx++);
            Cell c0 = r.createCell(0);
            c0.setCellValue(rowData[0]);
            c0.setCellStyle(headerStyle);
            Cell c1 = r.createCell(1);
            c1.setCellValue(rowData[1]);
            c1.setCellStyle(dataStyle);
        }

        rowIdx++; // cách dòng trước bảng thống kê

        // Thống kê
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

        String[][] stats = {
                {"Tổng ngày công trong tháng", String.valueOf(totalDays)},
                {"Ngày có mặt", String.valueOf(presentDays)},
                {"Ngày vắng", String.valueOf(absentDays)},
                {"Số lần đi muộn", String.valueOf(lateCount)},
                {"Số lần về sớm", String.valueOf(earlyCount)},
                {"Quên check-in", String.valueOf(forgotCheckin)},
                {"Quên check-out", String.valueOf(forgotCheckout)}
        };

        // Header cho bảng thống kê
        Row statHeader = sheet.createRow(rowIdx++);
        Cell sh0 = statHeader.createCell(0);
        sh0.setCellValue("Chỉ số");
        sh0.setCellStyle(headerStyle);
        Cell sh1 = statHeader.createCell(1);
        sh1.setCellValue("Số lượng");
        sh1.setCellStyle(headerStyle);

        for (String[] stat : stats) {
            Row row = sheet.createRow(rowIdx++);
            Cell c0 = row.createCell(0);
            c0.setCellValue(stat[0]);
            c0.setCellStyle(dataStyle);
            Cell c1 = row.createCell(1);
            c1.setCellValue(stat[1]);
            c1.setCellStyle(dataStyle);
        }

        // Auto size
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
    }
}