package controller.payroll;

import dao.PayrollDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.PitBracket;
import model.PitBracketVersion;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/payroll/pit/update")
public class PayrollPitBracketUpdateServlet extends HttpServlet {

    private final PayrollDAO payrollDAO = new PayrollDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        PitBracketVersion latestVersion = payrollDAO.getLatestPitBracketVersion();
        List<PitBracket> bracketList = new ArrayList<>();
        if (latestVersion != null) {
            bracketList = payrollDAO.getPitBrackets(latestVersion.getId());
        }
        request.setAttribute("bracketList", bracketList);
        request.getRequestDispatcher("/WEB-INF/views/payroll/payroll_pit_bracket_update.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String monthStr = request.getParameter("month");
            String yearStr = request.getParameter("year");

            int month = Integer.parseInt(monthStr);
            int year = Integer.parseInt(yearStr);

            String[] minValues = request.getParameterValues("minValues");
            String[] maxValues = request.getParameterValues("maxValues");
            String[] taxRates = request.getParameterValues("taxRates");

            List<PitBracket> brackets = new ArrayList<>();
            if (minValues != null) {
                for (int i = 0; i < minValues.length; i++) {
                    PitBracket b = new PitBracket();
                    b.setBracketLevel(i + 1);
                    b.setMinValue(Long.parseLong(minValues[i].trim()));

                    String maxStr = (maxValues != null && i < maxValues.length) ? maxValues[i].trim() : "";
                    if (!maxStr.isEmpty()) {
                        b.setMaxValue(Long.parseLong(maxStr));
                    } else {
                        b.setMaxValue(null);
                    }

                    b.setTaxRate(Double.parseDouble(taxRates[i].trim()));
                    brackets.add(b);
                }
            }

            boolean isSaved = payrollDAO.saveOrUpdatePitVersionAndBrackets(month, year, brackets);
            if (isSaved) {
                request.getSession().setAttribute("message", "PIT Tax Brackets updated successfully!");
            } else {
                request.getSession().setAttribute("error", "Database operational error occurred.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("error", "Invalid or missing parameters: " + e.getMessage());
        }
        response.sendRedirect(request.getContextPath() + "/payroll/pit/list");
    }
}
