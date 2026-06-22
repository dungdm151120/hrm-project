package controller.payroll;

import dao.DepartmentDAO;
import dao.PayrollDAO;
import dao.UserDAO;
import model.Payroll;
import model.Department;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

@WebServlet("/payroll/export")
public class ExportPayrollServlet extends HttpServlet {

    private final PayrollDAO payrollDAO = new PayrollDAO();
    private final DepartmentDAO departmentDAO = new DepartmentDAO();
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<Department> departments = departmentDAO.getAllDepartments();
        request.setAttribute("departments", departments);
        request.getRequestDispatcher("/WEB-INF/views/payroll/export_payroll.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String deptIdParam = request.getParameter("departmentId");
        String monthParam = request.getParameter("month");
        String yearParam = request.getParameter("year");

        if (monthParam == null || monthParam.isEmpty() || yearParam == null || yearParam.isEmpty()) {
            request.getSession().setAttribute("error", "Please select a specific month and year to export.");
            response.sendRedirect(request.getContextPath() + "/payroll/export");
            return;
        }

        Integer departmentId = (deptIdParam != null && !deptIdParam.isEmpty()) ? Integer.parseInt(deptIdParam) : null;
        int month = Integer.parseInt(monthParam);
        int year = Integer.parseInt(yearParam);

        int totalUsers = userDAO.findByDepartmentId(departmentId).size();

        int payrollCount = payrollDAO.countEmployeesWithPayroll(departmentId, month, year);

        if (payrollCount < totalUsers) {
            int missingPayrollCount = totalUsers - payrollCount;
            request.getSession().setAttribute("error", "Cannot export report! There are " + missingPayrollCount
                    + " employee(s) in this department who do not have payroll records for " + month + "/" + year + ". Please generate payroll first.");
            response.sendRedirect(request.getContextPath() + "/payroll/export");
            return;
        }

        List<Payroll> payrollList = payrollDAO.findPayrollsByDepartment(departmentId, month, year);
        boolean isAllCompany = (departmentId == null || departmentId == 0);

        String fileName = "Bao_Cao_Luong_" + (isAllCompany ? "AllCompany" : "Dept_" + departmentId) + "_" + month + "_" + year + ".xlsx";
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

        try (Workbook workbook = generateExcelReport(payrollList, isAllCompany, month, year);
             OutputStream out = response.getOutputStream()) {
            workbook.write(out);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Workbook generateExcelReport(List<Payroll> payrollList, boolean isAllCompany, int month, int year) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Báo cáo tiền lương");

        String titleLine1;
        String titleLine2;
        String[] headers;

        if (isAllCompany) {
            titleLine1 = "BÁO CÁO TIỀN LƯƠNG CÔNG TY";
            titleLine2 = "TOÀN CÔNG TY - THÁNG " + month + "/" + year;
            headers = new String[]{"Full Name", "Position", "Department", "Basic Salary", "Net Pay Amount"};
        } else {
            String deptName = (payrollList != null && !payrollList.isEmpty()) ? payrollList.get(0).getDepartmentName() : "N/A";
            titleLine1 = "BÁO CÁO TIỀN LƯƠNG PHÒNG";
            titleLine2 = deptName.toUpperCase() + " - THÁNG " + month + "/" + year;
            headers = new String[]{"Full Name", "Position", "Basic Salary", "Net Pay Amount"};
        }

        Font companyFont = workbook.createFont();
        companyFont.setFontName("Arial");
        companyFont.setFontHeightInPoints((short) 12);
        companyFont.setBold(true);
        CellStyle companyStyle = workbook.createCellStyle();
        companyStyle.setFont(companyFont);

        Font titleFont1 = workbook.createFont();
        titleFont1.setFontName("Arial");
        titleFont1.setFontHeightInPoints((short) 16);
        titleFont1.setBold(true);
        CellStyle titleStyle1 = workbook.createCellStyle();
        titleStyle1.setFont(titleFont1);

        Font titleFont2 = workbook.createFont();
        titleFont2.setFontName("Arial");
        titleFont2.setFontHeightInPoints((short) 13);
        titleFont2.setBold(true);
        titleFont2.setColor(IndexedColors.GREY_50_PERCENT.getIndex());
        CellStyle titleStyle2 = workbook.createCellStyle();
        titleStyle2.setFont(titleFont2);

        Font headerFont = workbook.createFont();
        headerFont.setFontName("Arial");
        headerFont.setFontHeightInPoints((short) 11);
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.WHITE.getIndex());

        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.BLUE_GREY.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);

        CellStyle currencyStyle = workbook.createCellStyle();
        DataFormat format = workbook.createDataFormat();
        currencyStyle.setDataFormat(format.getFormat("#,##0\" VND\""));

        Font totalFont = workbook.createFont();
        totalFont.setBold(true);
        CellStyle totalStyle = workbook.createCellStyle();
        totalStyle.setFont(totalFont);
        totalStyle.setDataFormat(format.getFormat("#,##0\" VND\""));

        Row row0 = sheet.createRow(0);
        Cell cellCompany = row0.createCell(0);
        cellCompany.setCellValue("CÔNG TY QUẢN LÝ NHÂN SỰ HRM");
        cellCompany.setCellStyle(companyStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, headers.length - 1));

        Row row1 = sheet.createRow(1);
        Cell cellTitle1 = row1.createCell(0);
        cellTitle1.setCellValue(titleLine1);
        cellTitle1.setCellStyle(titleStyle1);
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, headers.length - 1));

        Row row2 = sheet.createRow(2);
        Cell cellTitle2 = row2.createCell(0);
        cellTitle2.setCellValue(titleLine2);
        cellTitle2.setCellStyle(titleStyle2);
        sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, headers.length - 1));

        Row headerRow = sheet.createRow(4);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        int rowIndex = 5;
        double totalNetPay = 0.0;

        if (payrollList != null) {
            for (Payroll p : payrollList) {
                Row row = sheet.createRow(rowIndex++);

                if (isAllCompany) {
                    row.createCell(0).setCellValue(p.getEmployeeName());
                    row.createCell(1).setCellValue(p.getPositionName());
                    row.createCell(2).setCellValue(p.getDepartmentName());

                    Cell cellBasic = row.createCell(3);
                    cellBasic.setCellValue(p.getBasicSalary());
                    cellBasic.setCellStyle(currencyStyle);

                    Cell cellNetPay = row.createCell(4);
                    cellNetPay.setCellValue(p.getNetPay());
                    cellNetPay.setCellStyle(currencyStyle);
                } else {
                    row.createCell(0).setCellValue(p.getEmployeeName());
                    row.createCell(1).setCellValue(p.getPositionName());

                    Cell cellBasic = row.createCell(2);
                    cellBasic.setCellValue(p.getBasicSalary());
                    cellBasic.setCellStyle(currencyStyle);

                    Cell cellNetPay = row.createCell(3);
                    cellNetPay.setCellValue(p.getNetPay());
                    cellNetPay.setCellStyle(currencyStyle);
                }
                totalNetPay += p.getNetPay();
            }
        }

        Row totalRow = sheet.createRow(rowIndex);
        Cell cellTotalLabel = totalRow.createCell(0);
        cellTotalLabel.setCellValue("Total Net Pay");
        cellTotalLabel.setCellStyle(totalStyle);

        int totalValueColumnIndex = isAllCompany ? 4 : 3;
        Cell cellTotalValue = totalRow.createCell(totalValueColumnIndex);
        cellTotalValue.setCellValue(totalNetPay);
        cellTotalValue.setCellStyle(totalStyle);

        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
            if (sheet.getColumnWidth(i) < 5000) {
                sheet.setColumnWidth(i, 5200);
            }
        }

        return workbook;
    }
}