package controller.report;

import dao.SalaryReportDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.SalaryReportRowDTO;
import model.MonthlySalaryTotalDTO;

import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@WebServlet("/reports/salary")
public class SalaryReportServlet extends HttpServlet {
    private final SalaryReportDAO reportDAO = new SalaryReportDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        @SuppressWarnings("unchecked")
        Set<String> userPermissions = session != null ? (Set<String>) session.getAttribute("userPermissions") : null;
        if (userPermissions == null ||
                !(userPermissions.contains("PAYROLL_VIEW_LIST") || userPermissions.contains("PAYROLL_EXPORT_REPORT"))) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "You do not have permission to view salary reports.");
            return;
        }

        LocalDate today = LocalDate.now();
        int currentYear = today.getYear();

        String periodType = request.getParameter("periodType");
        if (!"quarter".equals(periodType) && !"year".equals(periodType)) {
            periodType = "month";
        }

        String groupBy = request.getParameter("groupBy");
        if (!"employee".equals(groupBy) && !"department".equals(groupBy)) {
            groupBy = "position";
        }

        String salarySort = request.getParameter("salarySort");
        if (!"salaryAsc".equals(salarySort) && !"salaryDesc".equals(salarySort)) {
            salarySort = "default";
        }

        int selectedMonth = parseIntInRange(request.getParameter("month"), today.getMonthValue(), 1, 12);
        int selectedQuarter = parseIntInRange(request.getParameter("quarter"),
                ((today.getMonthValue() - 1) / 3) + 1, 1, 4);
        int selectedYear = parseIntInRange(request.getParameter("year"), currentYear, 2020, currentYear + 3);
        String action = request.getParameter("action");
        boolean isGenerated = action == null || "generate".equalsIgnoreCase(action);

        List<Integer> years = new ArrayList<>();
        for (int y = currentYear - 3; y <= currentYear + 3; y++) {
            years.add(y);
        }

        if (isGenerated) {
            YearMonth startPeriod;
            YearMonth endPeriod;

            if ("quarter".equals(periodType)) {
                int startMonth = (selectedQuarter - 1) * 3 + 1;
                startPeriod = YearMonth.of(selectedYear, startMonth);
                endPeriod = startPeriod.plusMonths(2);
            } else if ("year".equals(periodType)) {
                startPeriod = YearMonth.of(selectedYear, 1);
                endPeriod = YearMonth.of(selectedYear, 12);
            } else {
                startPeriod = YearMonth.of(selectedYear, selectedMonth);
                endPeriod = startPeriod;
            }

            List<SalaryReportRowDTO> reportRows = reportDAO.generateSalaryReport(
                    groupBy, startPeriod, endPeriod, salarySort);
            List<SalaryReportRowDTO> departmentRows = reportDAO.generateSalaryReport(
                    "department", startPeriod, endPeriod, "salaryDesc");
            List<MonthlySalaryTotalDTO> monthlySalaryTotals = "month".equals(periodType)
                    ? List.of()
                    : reportDAO.getMonthlyCompanySalaryTotals(startPeriod, endPeriod);
            long totalWorkdayIncome = 0L;
            long totalProductIncome = 0L;
            long totalOvertimeIncome = 0L;
            long totalSickLeaveIncome = 0L;
            long totalGrossIncome = 0L;
            long totalIncome = 0L;
            int totalEmployees = 0;

            for (SalaryReportRowDTO row : reportRows) {
                totalWorkdayIncome += row.getWorkdayIncome();
                totalProductIncome += row.getProductIncome();
                totalOvertimeIncome += row.getOvertimeIncome();
                totalSickLeaveIncome += row.getSickLeaveIncome();
                totalGrossIncome += row.getGrossIncome();
                totalIncome += row.getTotalIncome();
                totalEmployees += row.getEmployeeCount();
            }

            request.setAttribute("reportRows", reportRows);
            request.setAttribute("departmentRows", departmentRows);
            request.setAttribute("monthlySalaryTotals", monthlySalaryTotals);
            request.setAttribute("totalWorkdayIncome", totalWorkdayIncome);
            request.setAttribute("totalProductIncome", totalProductIncome);
            request.setAttribute("totalOvertimeIncome", totalOvertimeIncome);
            request.setAttribute("totalSickLeaveIncome", totalSickLeaveIncome);
            request.setAttribute("totalGrossIncome", totalGrossIncome);
            request.setAttribute("totalIncome", totalIncome);
            request.setAttribute("totalEmployees", totalEmployees);
        }

        request.setAttribute("isGenerated", isGenerated);
        request.setAttribute("periodType", periodType);
        request.setAttribute("groupBy", groupBy);
        request.setAttribute("salarySort", salarySort);
        request.setAttribute("selectedMonth", selectedMonth);
        request.setAttribute("selectedQuarter", selectedQuarter);
        request.setAttribute("selectedYear", selectedYear);
        request.setAttribute("years", years);

        request.getRequestDispatcher("/WEB-INF/views/report/salary_report.jsp").forward(request, response);
    }

    private int parseIntInRange(String value, int defaultValue, int min, int max) {
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        try {
            int parsed = Integer.parseInt(value.trim());
            return parsed >= min && parsed <= max ? parsed : defaultValue;
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
