package controller.payroll;

import dao.PayrollDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.PayrollSetting;
import model.PitBracketVersion;

import java.io.IOException;
import java.util.List;

@WebServlet("/payroll/pit/list")
public class PayrollPitBracketListServlet extends HttpServlet {

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

            List<PitBracketVersion> versions = payrollDAO.getPitBracketVersions(month, year, offset, limit);

            int totalRecords = payrollDAO.countPitBracketVersions(month, year);
            int totalPages = (totalRecords > 0) ? (int) Math.ceil((double) totalRecords / limit) : 1;

            if (currentPage > totalPages) {
                currentPage = totalPages;
            }

            PitBracketVersion latestVersion = payrollDAO.getLatestPitBracketVersion();

            request.setAttribute("versions", versions);
            request.setAttribute("latestId", latestVersion != null ? latestVersion.getId() : -1);
            request.setAttribute("month", month);
            request.setAttribute("year", year);
            request.setAttribute("currentPage", currentPage);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("totalRecords", totalRecords);

            request.getRequestDispatcher("/WEB-INF/views/payroll/payroll_pit_versions.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred while loading payroll settings.");
        }
    }

}