package controller.report;

import dao.AttendanceReportDAO;
import model.AttendanceReportRowDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.User;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@WebServlet("/reports/attendance/export")
public class ExportAttendanceReportServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            HttpSession session = req.getSession(false);
            User currentUser = session == null ? null : (User) session.getAttribute("currentUser");
            if (currentUser == null) {
                resp.sendRedirect(req.getContextPath() + "/login");
                return;
            }

            @SuppressWarnings("unchecked")
            Set<String> userPermissions = (Set<String>) session.getAttribute("userPermissions");
            if (userPermissions == null) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }

            // Read parameters
            String periodType = req.getParameter("periodType");
            if (periodType == null || periodType.isEmpty()) {
                periodType = "month";
            }
            int month = 1;
            String mParam = req.getParameter("month");
            if (mParam != null && !mParam.isEmpty()) {
                month = Integer.parseInt(mParam);
            }
            int quarter = 1;
            String qParam = req.getParameter("quarter");
            if (qParam != null && !qParam.isEmpty()) {
                quarter = Integer.parseInt(qParam);
            }
            int year = LocalDate.now().getYear();
            String yParam = req.getParameter("year");
            if (yParam != null && !yParam.isEmpty()) {
                year = Integer.parseInt(yParam);
            }
            
            boolean isRestricted = !userPermissions.contains("ATTENDANCE_VIEW_ALL")
                    && userPermissions.contains("ATTENDANCE_VIEW_DEPARTMENT");
            Integer departmentId = isRestricted
                    ? currentUser.getDepartmentId()
                    : parseInteger(req.getParameter("departmentId"));
            if (isRestricted && departmentId == null) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, "No department assigned.");
                return;
            }

            LocalDate startDate = LocalDate.now();
            LocalDate endDate = LocalDate.now();

            if ("month".equalsIgnoreCase(periodType)) {
                startDate = LocalDate.of(year, month, 1);
                endDate = startDate.plusMonths(1).minusDays(1);
            } else if ("quarter".equalsIgnoreCase(periodType)) {
                int startMonth = (quarter - 1) * 3 + 1;
                startDate = LocalDate.of(year, startMonth, 1);
                endDate = startDate.plusMonths(3).minusDays(1);
            } else if ("year".equalsIgnoreCase(periodType)) {
                startDate = LocalDate.of(year, 1, 1);
                endDate = LocalDate.of(year, 12, 31);
            }

            int expectedWorkdays = calculateExpectedWorkdays(startDate, endDate);
            AttendanceReportDAO reportDAO = new AttendanceReportDAO();
            List<AttendanceReportRowDTO> reportRows = reportDAO.generateAttendanceReport(startDate, endDate, departmentId);

            Workbook workbook = new XSSFWorkbook();
            
            // Define standard styles
            CellStyle titleStyle = createTitleStyle(workbook);
            CellStyle subtitleStyle = createSubtitleStyle(workbook);
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook, false);
            CellStyle boldDataStyle = createDataStyle(workbook, true);
            CellStyle sectionHeaderStyle = createSectionHeaderStyle(workbook);

            // ==================== SHEET 1: CHI TIẾT BÁO CÁO ====================
            Sheet sheet1 = workbook.createSheet("Báo cáo chi tiết");
            int r1 = 0;
            
            // Title
            Row titleRow = sheet1.createRow(r1++);
            titleRow.setHeight((short) 500);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("BÁO CÁO CHUYÊN CẦN CHI TIẾT");
            titleCell.setCellStyle(titleStyle);
            sheet1.addMergedRegion(new CellRangeAddress(0, 0, 0, 13));

            // Subtitle
            Row subtitleRow = sheet1.createRow(r1++);
            subtitleRow.setHeight((short) 350);
            Cell subtitleCell = subtitleRow.createCell(0);
            subtitleCell.setCellValue("Thời gian: " + startDate.toString() + " đến " + endDate.toString() + " | Số ngày công dự kiến: " + expectedWorkdays);
            subtitleCell.setCellStyle(subtitleStyle);
            sheet1.addMergedRegion(new CellRangeAddress(1, 1, 0, 13));

            r1++; // Empty row

            // Headers
            Row headerRow = sheet1.createRow(r1++);
            headerRow.setHeight((short) 400);
            String[] headers = {
                "Mã NV", "Họ tên", "Chức vụ", "Phòng ban", "Tổng ngày công", 
                "Ngày có mặt", "Ngày vắng", "Đi muộn", "Về sớm", "Quên check-in", 
                "Quên check-out", "Tổng giờ công", "Tổng giờ OT", "Tổng Phép"
            };
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Data rows
            double totalActualWorkHours = 0;
            double totalExpectedWorkHours = 0;
            double totalActualOvertimeHours = 0;
            double totalRegisteredOvertimeHours = 0;
            double totalLeaveDays = 0;
            double totalAbsentDays = 0;

            AttendanceReportRowDTO hardestWorking = null;
            double maxHours = -1.0;

            AttendanceReportRowDTO mostPunctual = null;
            double minIrregularities = Double.MAX_VALUE;

            AttendanceReportRowDTO lowestWorking = null;
            double minHours = Double.MAX_VALUE;
            AttendanceReportRowDTO leastPunctual = null;
            int maxIrregularities = 0;

            for (AttendanceReportRowDTO row : reportRows) {
                row.setExpectedWorkdays(expectedWorkdays);
                
                totalActualWorkHours += row.getTotalWorkHours();
                totalExpectedWorkHours += expectedWorkdays * 8.0;
                totalActualOvertimeHours += row.getTotalOvertimeHours();
                totalRegisteredOvertimeHours += row.getRegisteredOvertimeHours();
                totalLeaveDays += row.getLeaveDays();
                totalAbsentDays += row.getAbsentDays();

                // Hardest working check
                double workAndOt = row.getTotalWorkHours() + row.getTotalOvertimeHours();
                if (workAndOt > maxHours) {
                    maxHours = workAndOt;
                    hardestWorking = row;
                }

                // Most punctual check
                if (row.getPresentDays() > 0) {
                    double irregularities = row.getLateDays() + row.getEarlyLeaveDays()
                            + row.getForgotCheckInDays() + row.getForgotCheckOutDays();
                    if (irregularities < minIrregularities) {
                        minIrregularities = irregularities;
                        mostPunctual = row;
                    }

                    if (workAndOt < minHours) {
                        minHours = workAndOt;
                        lowestWorking = row;
                    }
                }

                int irregularities = row.getLateDays() + row.getEarlyLeaveDays()
                        + row.getForgotCheckInDays() + row.getForgotCheckOutDays();
                if (irregularities > maxIrregularities) {
                    maxIrregularities = irregularities;
                    leastPunctual = row;
                }

                Row dr = sheet1.createRow(r1++);
                dr.createCell(0).setCellValue(row.getEmployeeCode());
                dr.createCell(1).setCellValue(row.getEmployeeName());
                dr.createCell(2).setCellValue(row.getPositionName());
                dr.createCell(3).setCellValue(row.getDepartmentName());
                dr.createCell(4).setCellValue(row.getExpectedWorkdays());
                dr.createCell(5).setCellValue(row.getPresentDays());
                dr.createCell(6).setCellValue(row.getAbsentDays());
                dr.createCell(7).setCellValue(row.getLateDays());
                dr.createCell(8).setCellValue(row.getEarlyLeaveDays());
                dr.createCell(9).setCellValue(row.getForgotCheckInDays());
                dr.createCell(10).setCellValue(row.getForgotCheckOutDays());
                dr.createCell(11).setCellValue(row.getTotalWorkHours());
                dr.createCell(12).setCellValue(row.getTotalOvertimeHours());
                dr.createCell(13).setCellValue(row.getLeaveDays());

                for (int i = 0; i < headers.length; i++) {
                    dr.getCell(i).setCellStyle(dataStyle);
                }
            }

            for (int i = 0; i < headers.length; i++) {
                sheet1.autoSizeColumn(i);
            }

            // ==================== SHEET 2: HIỆU SUẤT & HIGHLIGHTS ====================
            Sheet sheet2 = workbook.createSheet("Hiệu suất & Highlights");
            int r2 = 0;

            // Title
            Row titleRow2 = sheet2.createRow(r2++);
            titleRow2.setHeight((short) 500);
            Cell titleCell2 = titleRow2.createCell(0);
            titleCell2.setCellValue("TỔNG HỢP HIỆU SUẤT VÀ KHEN THƯỞNG");
            titleCell2.setCellStyle(titleStyle);
            sheet2.addMergedRegion(new CellRangeAddress(0, 0, 0, 4));

            r2++; // Empty row

            // Section 1: Hiệu Suất Chung
            Row secHeader1 = sheet2.createRow(r2++);
            Cell secCell1 = secHeader1.createCell(0);
            secCell1.setCellValue("I. HIỆU SUẤT CHUNG CỦA TOÀN BỘ NHÂN VIÊN");
            secCell1.setCellStyle(sectionHeaderStyle);
            sheet2.addMergedRegion(new CellRangeAddress(r2-1, r2-1, 0, 4));

            // Section 1 Headers
            Row sec1HeaderRow = sheet2.createRow(r2++);
            sec1HeaderRow.setHeight((short) 350);
            String[] sec1Headers = { "Chỉ số hiệu suất", "Tỉ lệ / Kết quả", "Chi tiết thống kê", "", "" };
            for (int i = 0; i < 3; i++) {
                Cell cell = sec1HeaderRow.createCell(i);
                cell.setCellValue(sec1Headers[i]);
                cell.setCellStyle(headerStyle);
            }
            sec1HeaderRow.createCell(3).setCellStyle(headerStyle);
            sec1HeaderRow.createCell(4).setCellStyle(headerStyle);
            sheet2.addMergedRegion(new CellRangeAddress(r2-1, r2-1, 2, 4));

            // Stat 1
            Row stat1 = sheet2.createRow(r2++);
            stat1.createCell(0).setCellValue("Hiệu suất giờ công");
            double workPercent = totalExpectedWorkHours > 0 ? (totalActualWorkHours / totalExpectedWorkHours) * 100 : 0.0;
            stat1.createCell(1).setCellValue(String.format("%.1f%%", workPercent));
            stat1.createCell(2).setCellValue(String.format("%.1f / %.1f giờ công", totalActualWorkHours, totalExpectedWorkHours));
            stat1.createCell(3);
            stat1.createCell(4);
            sheet2.addMergedRegion(new CellRangeAddress(r2-1, r2-1, 2, 4));

            // Stat 2
            Row stat2 = sheet2.createRow(r2++);
            stat2.createCell(0).setCellValue("Hiệu suất làm thêm (OT)");
            double otPercent = totalRegisteredOvertimeHours > 0 ? (totalActualOvertimeHours / totalRegisteredOvertimeHours) * 100 : 0.0;
            stat2.createCell(1).setCellValue(String.format("%.1f%%", otPercent));
            stat2.createCell(2).setCellValue(String.format("%.1f / %.1f giờ OT", totalActualOvertimeHours, totalRegisteredOvertimeHours));
            stat2.createCell(3);
            stat2.createCell(4);
            sheet2.addMergedRegion(new CellRangeAddress(r2-1, r2-1, 2, 4));

            // Stat 3
            Row stat3 = sheet2.createRow(r2++);
            stat3.createCell(0).setCellValue("Tổng ngày nghỉ phép");
            stat3.createCell(1).setCellValue(totalLeaveDays + " ngày");
            stat3.createCell(2).setCellValue("-");
            stat3.createCell(3);
            stat3.createCell(4);
            sheet2.addMergedRegion(new CellRangeAddress(r2-1, r2-1, 2, 4));

            // Stat 4
            Row stat4 = sheet2.createRow(r2++);
            stat4.createCell(0).setCellValue("Tổng ngày vắng");
            stat4.createCell(1).setCellValue(totalAbsentDays + " ngày");
            stat4.createCell(2).setCellValue("-");
            stat4.createCell(3);
            stat4.createCell(4);
            sheet2.addMergedRegion(new CellRangeAddress(r2-1, r2-1, 2, 4));

            // Apply styles to Stat rows
            for (int r = r2 - 4; r < r2; r++) {
                Row row = sheet2.getRow(r);
                row.getCell(0).setCellStyle(boldDataStyle);
                row.getCell(1).setCellStyle(dataStyle);
                row.getCell(2).setCellStyle(dataStyle);
                row.getCell(3).setCellStyle(dataStyle);
                row.getCell(4).setCellStyle(dataStyle);
            }

            r2 += 2; // Spacing

            // Section 2: Khen Thưởng & Vinh Danh
            Row secHeader2 = sheet2.createRow(r2++);
            Cell secCell2 = secHeader2.createCell(0);
            secCell2.setCellValue("II. DANH HIỆU & VINH DANH CÁ NHÂN");
            secCell2.setCellStyle(sectionHeaderStyle);
            sheet2.addMergedRegion(new CellRangeAddress(r2-1, r2-1, 0, 4));

            // Section 2 Headers
            Row sec2HeaderRow = sheet2.createRow(r2++);
            sec2HeaderRow.setHeight((short) 350);
            String[] sec2Headers = { "Danh hiệu", "Họ và tên", "Mã NV", "Phòng ban", "Thành tích" };
            for (int i = 0; i < sec2Headers.length; i++) {
                Cell cell = sec2HeaderRow.createCell(i);
                cell.setCellValue(sec2Headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Hardest Working Row
            Row hwRow = sheet2.createRow(r2++);
            hwRow.createCell(0).setCellValue("Nhân viên chăm chỉ nhất");
            if (hardestWorking != null) {
                hwRow.createCell(1).setCellValue(hardestWorking.getEmployeeName());
                hwRow.createCell(2).setCellValue(hardestWorking.getEmployeeCode());
                hwRow.createCell(3).setCellValue(hardestWorking.getDepartmentName());
                hwRow.createCell(4).setCellValue(String.format("%.1f giờ (Giờ làm + OT)", hardestWorking.getTotalWorkHours() + hardestWorking.getTotalOvertimeHours()));
            } else {
                hwRow.createCell(1).setCellValue("Không có dữ liệu");
                hwRow.createCell(2).setCellValue("-");
                hwRow.createCell(3).setCellValue("-");
                hwRow.createCell(4).setCellValue("-");
            }

            // Most Punctual Row
            Row mpRow = sheet2.createRow(r2++);
            mpRow.createCell(0).setCellValue("Nhân viên đúng giờ nhất");
            if (mostPunctual != null) {
                mpRow.createCell(1).setCellValue(mostPunctual.getEmployeeName());
                mpRow.createCell(2).setCellValue(mostPunctual.getEmployeeCode());
                mpRow.createCell(3).setCellValue(mostPunctual.getDepartmentName());
                mpRow.createCell(4).setCellValue(String.format("Đi muộn: %d | Về sớm: %d | Quên: %d", 
                    mostPunctual.getLateDays(), mostPunctual.getEarlyLeaveDays(), mostPunctual.getForgotCheckInDays()));
            } else {
                mpRow.createCell(1).setCellValue("Không có dữ liệu");
                mpRow.createCell(2).setCellValue("-");
                mpRow.createCell(3).setCellValue("-");
                mpRow.createCell(4).setCellValue("-");
            }

            // Apply styles to Section 2 data rows
            for (int r = r2 - 2; r < r2; r++) {
                Row row = sheet2.getRow(r);
                row.getCell(0).setCellStyle(boldDataStyle);
                for (int i = 1; i <= 4; i++) {
                    if (row.getCell(i) != null) {
                        row.getCell(i).setCellStyle(dataStyle);
                    }
                }
            }

            r2 += 2;
            Row secHeader3 = sheet2.createRow(r2++);
            Cell secCell3 = secHeader3.createCell(0);
            secCell3.setCellValue("III. EMPLOYEES NEEDING ATTENTION");
            secCell3.setCellStyle(sectionHeaderStyle);
            sheet2.addMergedRegion(new CellRangeAddress(r2 - 1, r2 - 1, 0, 4));

            String[] attentionLabels = {
                "Lowest total work + OT hours",
                "Least punctual employee"
            };
            AttendanceReportRowDTO[] attentionRows = {
                lowestWorking, leastPunctual
            };
            String[] attentionDetails = {
                lowestWorking == null ? "No data" : String.format("%.1f work + OT hours", lowestWorking.getTotalWorkHours() + lowestWorking.getTotalOvertimeHours()),
                leastPunctual == null ? "No data" : "Late: " + leastPunctual.getLateDays()
                        + " | Early leave: " + leastPunctual.getEarlyLeaveDays()
                        + " | Missing check-in: " + leastPunctual.getForgotCheckInDays()
                        + " | Missing check-out: " + leastPunctual.getForgotCheckOutDays()
            };

            for (int i = 0; i < attentionLabels.length; i++) {
                Row row = sheet2.createRow(r2++);
                AttendanceReportRowDTO employee = attentionRows[i];
                row.createCell(0).setCellValue(attentionLabels[i]);
                row.createCell(1).setCellValue(employee == null ? "No data" : employee.getEmployeeName());
                row.createCell(2).setCellValue(employee == null ? "-" : employee.getEmployeeCode());
                row.createCell(3).setCellValue(employee == null ? "-" : employee.getDepartmentName());
                row.createCell(4).setCellValue(attentionDetails[i]);
                row.getCell(0).setCellStyle(boldDataStyle);
                for (int cellIndex = 1; cellIndex <= 4; cellIndex++) {
                    row.getCell(cellIndex).setCellStyle(dataStyle);
                }
            }

            sheet2.autoSizeColumn(0);
            sheet2.autoSizeColumn(1);
            sheet2.autoSizeColumn(2);
            sheet2.autoSizeColumn(3);
            sheet2.autoSizeColumn(4);

            // Send Response
            resp.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            String filename = "attendance_report_" + year + "_" + (("month".equalsIgnoreCase(periodType)) ? month : periodType) + ".xlsx";
            resp.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
            workbook.write(resp.getOutputStream());
            workbook.close();

        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error generating Excel report: " + e.getMessage());
        }
    }

    private int calculateExpectedWorkdays(LocalDate start, LocalDate end) {
        int count = 0;
        LocalDate curr = start;
        while (!curr.isAfter(end)) {
            if (curr.getDayOfWeek().getValue() < 6) { // Mon-Fri
                count++;
            }
            curr = curr.plusDays(1);
        }
        return count;
    }

    private Integer parseInteger(String value) {
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            return null;
        }
    }

    // ====== Styles helper ======
    private CellStyle createTitleStyle(Workbook workbook) {
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 16);
        font.setColor(IndexedColors.DARK_BLUE.getIndex());
        CellStyle style = workbook.createCellStyle();
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

    private CellStyle createSubtitleStyle(Workbook workbook) {
        Font font = workbook.createFont();
        font.setItalic(true);
        font.setFontHeightInPoints((short) 11);
        font.setColor(IndexedColors.GREY_50_PERCENT.getIndex());
        CellStyle style = workbook.createCellStyle();
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.LEFT);
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

    private CellStyle createDataStyle(Workbook workbook, boolean bold) {
        Font font = workbook.createFont();
        font.setBold(bold);
        CellStyle style = workbook.createCellStyle();
        style.setFont(font);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private CellStyle createSectionHeaderStyle(Workbook workbook) {
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 12);
        font.setColor(IndexedColors.WHITE.getIndex());
        CellStyle style = workbook.createCellStyle();
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }
}
