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

        String deptName = "TẤT CẢ PHÒNG BAN";
        if (departmentId != null && payrollList != null && !payrollList.isEmpty()) {
            deptName = payrollList.get(0).getDepartmentName();
        }

        String fileName = "Bao_Cao_Luong_" + (departmentId != null ? departmentId : "All") + "_" + month + "_" + year + ".xlsx";
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

        try (Workbook workbook = new XSSFWorkbook();
             OutputStream out = response.getOutputStream()) {

            Sheet sheet = workbook.createSheet("Báo cáo tiền lương");

            Font titleFont = workbook.createFont();
            titleFont.setFontName("Arial");
            titleFont.setFontHeightInPoints((short) 16);
            titleFont.setBold(true);
            CellStyle titleStyle = workbook.createCellStyle();
            titleStyle.setFont(titleFont);

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
            Cell cellTitle1 = row0.createCell(0);
            cellTitle1.setCellValue("CÔNG TY QUẢN LÝ NHÂN SỰ HRM");
            cellTitle1.setCellStyle(titleStyle);

            Row row1 = sheet.createRow(1);
            Cell cellTitle2 = row1.createCell(0);
            cellTitle2.setCellValue("BÁO CÁO TIỀN LƯƠNG PHÒNG: " + deptName.toUpperCase() + " - THÁNG " + month + "/" + year);
            cellTitle2.setCellStyle(titleStyle);

            String[] headers = {"Full Name", "Position", "Basic Salary", "Net Pay Amount"};
            Row headerRow = sheet.createRow(3);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowIndex = 4;
            double totalNetPay = 0.0;

            if (payrollList != null) {
                for (Payroll p : payrollList) {
                    Row row = sheet.createRow(rowIndex++);
                    row.createCell(0).setCellValue(p.getEmployeeName());
                    row.createCell(1).setCellValue(p.getPositionName());

                    Cell cellBasic = row.createCell(2);
                    cellBasic.setCellValue(p.getBasicSalary());
                    cellBasic.setCellStyle(currencyStyle);

                    Cell cellNetPay = row.createCell(3);
                    cellNetPay.setCellValue(p.getNetPay());
                    cellNetPay.setCellStyle(currencyStyle);

                    totalNetPay += p.getNetPay();
                }
            }

            Row totalRow = sheet.createRow(rowIndex);
            Cell cellTotalLabel = totalRow.createCell(0);
            cellTotalLabel.setCellValue("Total Net Pay");
            cellTotalLabel.setCellStyle(totalStyle);

            Cell cellTotalValue = totalRow.createCell(3);
            cellTotalValue.setCellValue(totalNetPay);
            cellTotalValue.setCellStyle(totalStyle);

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}