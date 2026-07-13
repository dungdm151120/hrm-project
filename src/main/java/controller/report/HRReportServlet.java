package controller.report;

import dao.HRReportDAO;
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

        String action = request.getParameter("action");
        String deptParam = request.getParameter("departmentId");

        // 1. Tạo danh sách năm hiển thị bộ lọc (Từ 2020 đến năm hiện tại)
        int currentYear = LocalDate.now().getYear();
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

        // 2. Kiểm tra nếu người dùng chưa nhấn nút "Tạo báo cáo"
        if (!"generate".equals(action)) {
            request.setAttribute("isGenerated", false);
            request.setAttribute("periodType", "month");
            request.setAttribute("selectedMonth", LocalDate.now().getMonthValue());
            request.setAttribute("selectedQuarter", 1);
            request.setAttribute("selectedYear", currentYear);

            request.getRequestDispatcher("/WEB-INF/views/report/hr_report.jsp").forward(request, response);
            return;
        }

        // 3. Xử lý logic tính toán Target Date tối ưu từ các ô chọn độc lập
        String periodType = request.getParameter("periodType");
        String monthParam = request.getParameter("month");
        String quarterParam = request.getParameter("quarter");
        String yearParam = request.getParameter("year");

        int year = (yearParam != null) ? Integer.parseInt(yearParam) : currentYear;
        LocalDate targetDate = LocalDate.now();

        int selectedMonth = LocalDate.now().getMonthValue();
        int selectedQuarter = 1;

        if ("month".equals(periodType) && monthParam != null) {
            selectedMonth = Integer.parseInt(monthParam);
            targetDate = YearMonth.of(year, selectedMonth).atEndOfMonth();
        } else if ("quarter".equals(periodType) && quarterParam != null) {
            selectedQuarter = Integer.parseInt(quarterParam);
            int monthOfQuarter = selectedQuarter * 3; // Lấy tháng cuối cùng của Quý
            targetDate = YearMonth.of(year, monthOfQuarter).atEndOfMonth();
        } else if ("year".equals(periodType)) {
            targetDate = LocalDate.of(year, 12, 31);
        }

        // Chặn nếu chọn mốc thời gian ở tương lai
        if (targetDate.isAfter(LocalDate.now())) {
            targetDate = LocalDate.now();
        }

        // 4. Lấy dữ liệu từ DAO
        HRReportDTO reportData = reportDAO.getHRReportData(targetDate, selectedDeptId);

        // 5. Đẩy ngược dữ liệu dạng Số nguyên chuẩn sang JSP
        request.setAttribute("isGenerated", true);
        request.setAttribute("reportData", reportData);
        request.setAttribute("periodType", periodType);
        request.setAttribute("selectedMonth", selectedMonth);
        request.setAttribute("selectedQuarter", selectedQuarter);
        request.setAttribute("selectedYear", year);

        request.getRequestDispatcher("/WEB-INF/views/report/hr_report.jsp").forward(request, response);
    }
}