package controller.report;

import dao.HRReportDAO;
import model.DeptEmployeeChangeDTO;
import model.HRReportDTO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/reports/hr")
public class HRReportServlet extends HttpServlet {

    private final HRReportDAO reportDAO = new HRReportDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String deptParam = request.getParameter("departmentId");

        LocalDate today = LocalDate.now();
        int currentYear = today.getYear();
        int currentMonth = today.getMonthValue();

        List<Integer> years = new ArrayList<>();
        for (int i = currentYear; i >= 2020; i--) {
            years.add(i);
        }
        request.setAttribute("years", years);

        Integer selectedDeptId = null;
        if (deptParam != null && !deptParam.trim().isEmpty() && !deptParam.equals("all")) {
            try {
                selectedDeptId = Integer.parseInt(deptParam);
            } catch (NumberFormatException e) {
                selectedDeptId = null;
            }
        }
        request.setAttribute("selectedDeptId", selectedDeptId);

        String periodType = request.getParameter("periodType");
        if (periodType == null || periodType.trim().isEmpty()) {
            periodType = "month";
        }

        String monthParam = request.getParameter("month");
        String quarterParam = request.getParameter("quarter");
        String yearParam = request.getParameter("year");

        int year = (yearParam != null) ? Integer.parseInt(yearParam) : currentYear;
        int selectedMonth = (monthParam != null) ? Integer.parseInt(monthParam) : currentMonth;
        int selectedQuarter = (quarterParam != null) ? Integer.parseInt(quarterParam) : ((currentMonth - 1) / 3 + 1);

        // 1. Tính khoảng thời gian Lọc [startDate -> endDate]
        LocalDate startDate;
        LocalDate endDate;

        if ("month".equals(periodType)) {
            startDate = LocalDate.of(year, selectedMonth, 1);
            endDate = YearMonth.of(year, selectedMonth).atEndOfMonth();
        } else if ("quarter".equals(periodType)) {
            int startMonth = (selectedQuarter - 1) * 3 + 1;
            int endMonth = selectedQuarter * 3;
            startDate = LocalDate.of(year, startMonth, 1);
            endDate = YearMonth.of(year, endMonth).atEndOfMonth();
        } else { // year
            startDate = LocalDate.of(year, 1, 1);
            endDate = LocalDate.of(year, 12, 31);
        }

        // Nếu ngày kết thúc vượt quá hôm nay -> Giới hạn lại tới hôm nay
        if (endDate.isAfter(today)) {
            endDate = today;
        }

        // 2. Lấy dữ liệu báo cáo tổng quan & dữ liệu biến động
        HRReportDTO reportData = reportDAO.getHRReportData(endDate, selectedDeptId);
        List<DeptEmployeeChangeDTO> deptChanges = reportDAO.getDeptEmployeeChanges(startDate, endDate);

        // 3. Đẩy dữ liệu sang JSP
        request.setAttribute("isGenerated", true);
        request.setAttribute("reportData", reportData);
        request.setAttribute("deptChanges", deptChanges);
        request.setAttribute("periodType", periodType);
        request.setAttribute("selectedMonth", selectedMonth);
        request.setAttribute("selectedQuarter", selectedQuarter);
        request.setAttribute("selectedYear", year);

        request.getRequestDispatcher("/WEB-INF/views/report/hr_report.jsp").forward(request, response);
    }
}