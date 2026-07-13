package controller.payroll;

import dao.PayrollDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.PayrollSetting;

import java.io.IOException;
import java.util.List;

@WebServlet("/payroll/setting/list")
public class PayrollSettingListServlet extends HttpServlet {

    private final PayrollDAO payrollDAO = new PayrollDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String monthStr = request.getParameter("month");
            String yearStr = request.getParameter("year");
            String pageStr = request.getParameter("page");

            Integer month = (monthStr != null && !monthStr.trim().isEmpty()) ? Integer.parseInt(monthStr) : null;
            Integer year = (yearStr != null && !yearStr.trim().isEmpty()) ? Integer.parseInt(yearStr) : null;
            int currentPage = (pageStr != null && !pageStr.trim().isEmpty()) ? Integer.parseInt(pageStr) : 1;
            int limit = 10;
            int offset = (currentPage - 1) * limit;

            List<PayrollSetting> settings = payrollDAO.getPayrollSettings(month, year, offset, limit);
            PayrollSetting latestSetting = payrollDAO.getLatestPayrollSetting();

            request.setAttribute("settings", settings);
            request.setAttribute("latestId", latestSetting != null ? latestSetting.getId() : -1);
            request.setAttribute("month", month);
            request.setAttribute("year", year);
            request.setAttribute("currentPage", currentPage);

            request.getRequestDispatcher("/WEB-INF/views/payroll/payroll_setting_list.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred while loading payroll settings.");
        }
    }

}