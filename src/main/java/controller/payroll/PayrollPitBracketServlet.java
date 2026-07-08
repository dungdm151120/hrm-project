package controller.payroll;

import dao.PayrollDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.PitBracket;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/payroll/pit")
public class PayrollPitBracketServlet extends HttpServlet {

    private final PayrollDAO payrollDao = new PayrollDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<PitBracket> bracketList = payrollDao.getPitBrackets();

        LocalDate commonEffectiveDate = LocalDate.now();
        if (bracketList != null && !bracketList.isEmpty() && bracketList.get(0).getEffectiveDate() != null) {
            commonEffectiveDate = bracketList.get(0).getEffectiveDate();
        }

        request.setAttribute("bracketList", bracketList);
        request.setAttribute("commonEffectiveDate", commonEffectiveDate);
        request.getRequestDispatcher("/WEB-INF/views/payroll/payroll_pit_bracket.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String commonDateStr = request.getParameter("commonEffectiveDate");
            LocalDate effectiveDate = LocalDate.parse(commonDateStr);

            String[] ids = request.getParameterValues("bracketIds");
            String[] levels = request.getParameterValues("bracketLevels");
            String[] minValues = request.getParameterValues("minValues");
            String[] maxValues = request.getParameterValues("maxValues");
            String[] taxRates = request.getParameterValues("taxRates");

            List<PitBracket> newBrackets = new ArrayList<>();

            if (ids != null) {
                for (int i = 0; i < ids.length; i++) {
                    PitBracket bracket = new PitBracket();
                    bracket.setId(Integer.parseInt(ids[i]));
                    bracket.setBracketLevel(Integer.parseInt(levels[i]));
                    bracket.setMinValue(Long.parseLong(minValues[i].trim()));

                    String maxStr = maxValues[i] != null ? maxValues[i].trim() : "";
                    if (!maxStr.isEmpty()) {
                        bracket.setMaxValue(Long.parseLong(maxStr));
                    } else {
                        bracket.setMaxValue(null); // Không giới hạn (vô cực)
                    }

                    bracket.setTaxRate(Double.parseDouble(taxRates[i]));
                    bracket.setEffectiveDate(effectiveDate);

                    newBrackets.add(bracket);
                }
            }

            boolean isUpdated = payrollDao.updatePitBrackets(newBrackets, effectiveDate);

            if (isUpdated) {
                request.getSession().setAttribute("message", "PIT tax brackets updated successfully!");
            } else {
                request.getSession().setAttribute("error", "Failed to update PIT tax brackets.");
            }

            response.sendRedirect(request.getContextPath() + "/payroll/pit");

        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("error", "An error occurred: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/payroll/pit");
        }
    }
}