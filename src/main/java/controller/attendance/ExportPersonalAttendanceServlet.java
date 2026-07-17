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
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.awt.Color;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@WebServlet("/admin/attendance/exportPersonal")
public class ExportPersonalAttendanceServlet extends HttpServlet {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm:ss");

    private static final String GREEN_HEX  = "#22c55e";
    private static final String ORANGE_HEX = "#f59e0b";
    private static final String RED_HEX    = "#ef4444";
    private static final String BLUE_HEX   = "#38bdf8";

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
            CellStyle companyStyle = createCompanyStyle(workbook);
            CellStyle reportTitleStyle = createReportTitleStyle(workbook);
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle weekdayHeaderStyle = createWeekdayHeaderStyle(workbook);

            // Style thường (cho các sheet sau)
            CellStyle normalDataStyle = createDataStyle(workbook, false);
            CellStyle normalGreenStyle  = createColoredDataStyle(workbook, GREEN_HEX, false);
            CellStyle normalOrangeStyle = createColoredDataStyle(workbook, ORANGE_HEX, false);
            CellStyle normalRedStyle    = createColoredDataStyle(workbook, RED_HEX, false);
            CellStyle normalBlueStyle   = createColoredDataStyle(workbook, BLUE_HEX, false);

            // Style bold (chỉ cho sheet ATTENDANCE_LOGS)
            CellStyle boldDataStyle = createDataStyle(workbook, true);
            CellStyle boldGreenStyle  = createColoredDataStyle(workbook, GREEN_HEX, true);
            CellStyle boldOrangeStyle = createColoredDataStyle(workbook, ORANGE_HEX, true);
            CellStyle boldRedStyle    = createColoredDataStyle(workbook, RED_HEX, true);
            CellStyle boldBlueStyle   = createColoredDataStyle(workbook, BLUE_HEX, true);

            createAttendanceLogsSheet(workbook, records, month, year, companyStyle, reportTitleStyle,
                    headerStyle, weekdayHeaderStyle, boldDataStyle,
                    boldGreenStyle, boldOrangeStyle, boldRedStyle, boldBlueStyle);
            createDetailSheet(workbook, first, position, companyStyle, headerStyle, normalDataStyle);
            createRuleSheet(workbook, companyStyle, headerStyle, normalDataStyle);
            createSummarySheet(workbook, first, records, month, year, companyStyle, headerStyle, normalDataStyle);

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

    // ====== Styles ======
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

    private CellStyle createWeekdayHeaderStyle(Workbook workbook) {
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 10);
        font.setColor(IndexedColors.DARK_BLUE.getIndex());
        CellStyle style = workbook.createCellStyle();
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private CellStyle createDataStyle(Workbook workbook, boolean bold) {
        Font font = workbook.createFont();
        font.setBold(bold);
        CellStyle style = workbook.createCellStyle();
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private CellStyle createColoredDataStyle(Workbook workbook, String hexColor, boolean bold) {
        Font font = workbook.createFont();
        font.setBold(bold);
        CellStyle style = workbook.createCellStyle();
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);

        if (font instanceof XSSFFont xssfFont) {
            xssfFont.setColor(hexToXSSFColor(hexColor));
        }
        style.setFont(font);
        return style;
    }

    private XSSFColor hexToXSSFColor(String hex) {
        String rgb = hex.startsWith("#") ? hex.substring(1) : hex;
        int r = Integer.parseInt(rgb.substring(0, 2), 16);
        int g = Integer.parseInt(rgb.substring(2, 4), 16);
        int b = Integer.parseInt(rgb.substring(4, 6), 16);
        return new XSSFColor(new Color(r, g, b), null);
    }

    // ====== Sheet ATTENDANCE_LOGS (dùng style BOLD) ======
    private void createAttendanceLogsSheet(Workbook workbook, List<AttendanceRecordDTO> records,
                                           int month, int year,
                                           CellStyle companyStyle, CellStyle reportTitleStyle,
                                           CellStyle headerStyle, CellStyle weekdayHeaderStyle,
                                           CellStyle dataStyle,
                                           CellStyle greenStyle, CellStyle orangeStyle,
                                           CellStyle redStyle, CellStyle blueStyle) {
        // Vì chỉ có 1 nhân viên, ta lấy tất cả các ngày có dữ liệu
        List<LocalDate> dates = records.stream()
                .map(AttendanceRecordDTO::getWorkDate)
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        // Tạo map ngày -> record
        Map<LocalDate, AttendanceRecordDTO> recordMap = new HashMap<>();
        for (AttendanceRecordDTO r : records) {
            recordMap.put(r.getWorkDate(), r);
        }

        Sheet sheet = workbook.createSheet("ATTENDANCE_LOGS");
        int colCount = 1 + dates.size();
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

        // Dòng 3: trống
        rowIdx++;

        // Dòng 4: Thứ trong tuần
        Row weekdayRow = sheet.createRow(rowIdx++);
        Cell blankCorner = weekdayRow.createCell(0);
        blankCorner.setCellStyle(weekdayHeaderStyle);
        for (int i = 0; i < dates.size(); i++) {
            LocalDate date = dates.get(i);
            DayOfWeek dayOfWeek = date.getDayOfWeek();
            String weekday = vietnameseDayOfWeek(dayOfWeek);
            Cell cell = weekdayRow.createCell(i + 1);
            cell.setCellValue(weekday);
            cell.setCellStyle(weekdayHeaderStyle);
        }

        // Dòng 5: Header ngày
        Row headerRow = sheet.createRow(rowIdx++);
        Cell headerName = headerRow.createCell(0);
        headerName.setCellValue("employee_name");
        headerName.setCellStyle(headerStyle);
        for (int i = 0; i < dates.size(); i++) {
            Cell cell = headerRow.createCell(i + 1);
            cell.setCellValue(dates.get(i).format(DATE_FMT));
            cell.setCellStyle(headerStyle);
        }

        // Dữ liệu (chỉ 1 dòng cho nhân viên này)
        Row row = sheet.createRow(rowIdx++);
        Cell nameCell = row.createCell(0);
        nameCell.setCellValue(records.get(0).getEmployeeName());
        nameCell.setCellStyle(dataStyle);

        for (int i = 0; i < dates.size(); i++) {
            LocalDate date = dates.get(i);
            Cell cell = row.createCell(i + 1);

            AttendanceRecordDTO record = recordMap.get(date);
            if (record == null) {
                cell.setCellValue("null - null");
                cell.setCellStyle(dataStyle);
            } else {
                String status = record.getStatus();
                String cellValue = "";
                CellStyle styleToUse = dataStyle;

                if ("ON_LEAVE".equals(status)) {
                    cellValue = "On leave";
                    styleToUse = blueStyle;
                } else if ("SICK_LEAVE".equals(status)) {
                    cellValue = "Sick leave";
                    styleToUse = blueStyle;
                } else if ("HOLIDAY".equals(status)) {
                    cellValue = "Holiday";
                    styleToUse = blueStyle;
                } else if ("ABSENT".equals(status)) {
                    cellValue = "Absent";
                    styleToUse = redStyle;
                } else if ("ON_TIME".equals(status)) {
                    String checkInStr = record.getCheckIn() != null ?
                            record.getCheckIn().format(TIME_FMT) : "null";
                    String checkOutStr = record.getCheckOut() != null ?
                            record.getCheckOut().format(TIME_FMT) : "null";
                    cellValue = checkInStr + " - " + checkOutStr;
                    styleToUse = greenStyle;
                } else if (status != null && (
                        status.equals("LATE") ||
                                status.equals("EARLY_LEAVE") ||
                                status.equals("LATE_AND_EARLY") ||
                                status.equals("LATE_AND_EARLY_LEAVE") ||
                                status.equals("FORGOT_CHECKIN") ||
                                status.equals("FORGOT_CHECKOUT") ||
                                status.equals("FORGOT_CHECK_IN") ||
                                status.equals("FORGOT_CHECK_OUT"))) {
                    String checkInStr = record.getCheckIn() != null ?
                            record.getCheckIn().format(TIME_FMT) : "null";
                    String checkOutStr = record.getCheckOut() != null ?
                            record.getCheckOut().format(TIME_FMT) : "null";
                    cellValue = checkInStr + " - " + checkOutStr;
                    styleToUse = orangeStyle;
                } else {
                    String checkInStr = record.getCheckIn() != null ?
                            record.getCheckIn().format(TIME_FMT) : "null";
                    String checkOutStr = record.getCheckOut() != null ?
                            record.getCheckOut().format(TIME_FMT) : "null";
                    cellValue = checkInStr + " - " + checkOutStr;
                    styleToUse = dataStyle;
                }
                cell.setCellValue(cellValue);
                cell.setCellStyle(styleToUse);
            }
        }

        for (int i = 0; i < colCount; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private String vietnameseDayOfWeek(DayOfWeek dayOfWeek) {
        return switch (dayOfWeek) {
            case MONDAY -> "Thứ Hai";
            case TUESDAY -> "Thứ Ba";
            case WEDNESDAY -> "Thứ Tư";
            case THURSDAY -> "Thứ Năm";
            case FRIDAY -> "Thứ Sáu";
            case SATURDAY -> "Thứ Bảy";
            case SUNDAY -> "Chủ Nhật";
        };
    }

    // ====== Sheet CHI TIẾT NHÂN VIÊN (style thường) ======
    private void createDetailSheet(Workbook workbook, AttendanceRecordDTO first, String position,
                                   CellStyle companyStyle, CellStyle headerStyle, CellStyle dataStyle) {
        Sheet sheet = workbook.createSheet("CHI TIẾT NHÂN VIÊN");
        int colCount = 4;
        int rowIdx = 0;

        Row companyRow = sheet.createRow(rowIdx++);
        companyRow.setHeight((short) 500);
        Cell companyCell = companyRow.createCell(0);
        companyCell.setCellValue("CÔNG TY QUẢN LÍ NHÂN SỰ HRM");
        companyCell.setCellStyle(companyStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, colCount - 1));

        rowIdx++;

        Row headerRow = sheet.createRow(rowIdx++);
        String[] headers = {"employee_id", "employee_code", "full_name", "position"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        Row dataRow = sheet.createRow(rowIdx++);
        dataRow.createCell(0).setCellValue(first.getUserId());
        dataRow.createCell(1).setCellValue(first.getEmployeeCode());
        dataRow.createCell(2).setCellValue(first.getEmployeeName());
        dataRow.createCell(3).setCellValue(position);
        for (int i = 0; i < colCount; i++) {
            dataRow.getCell(i).setCellStyle(dataStyle);
        }

        for (int i = 0; i < colCount; i++) sheet.autoSizeColumn(i);
    }

    // ====== Sheet CHÚ THÍCH (style thường) ======
    private void createRuleSheet(Workbook workbook, CellStyle companyStyle, CellStyle headerStyle, CellStyle dataStyle) {
        Sheet sheet = workbook.createSheet("CHÚ THÍCH");
        int colCount = 3;
        int rowIdx = 0;

        Row companyRow = sheet.createRow(rowIdx++);
        companyRow.setHeight((short) 500);
        Cell companyCell = companyRow.createCell(0);
        companyCell.setCellValue("CÔNG TY QUẢN LÍ NHÂN SỰ HRM");
        companyCell.setCellStyle(companyStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, colCount - 1));

        rowIdx++;

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

    // ====== Sheet TỔNG HỢP (style thường) ======
    private void createSummarySheet(Workbook workbook, AttendanceRecordDTO first,
                                    List<AttendanceRecordDTO> records, int month, int year,
                                    CellStyle companyStyle, CellStyle headerStyle, CellStyle dataStyle) {
        Sheet sheet = workbook.createSheet("TỔNG HỢP");
        int colCount = 2;
        int rowIdx = 0;

        Row mainTitleRow = sheet.createRow(rowIdx++);
        mainTitleRow.setHeight((short) 500);
        Cell mainTitleCell = mainTitleRow.createCell(0);
        mainTitleCell.setCellValue("TỔNG HỢP CHẤM CÔNG");
        mainTitleCell.setCellStyle(companyStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, colCount - 1));

        rowIdx++;

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

        rowIdx++;

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

        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
    }
}