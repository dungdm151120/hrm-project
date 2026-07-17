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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/payroll/pit")
public class PayrollPitBracketServlet extends HttpServlet {

    private final PayrollDAO payrollDao = new PayrollDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idStr = request.getParameter("id");
        String effectiveDateStr = request.getParameter("effectiveDate");
        List<PitBracket> bracketList = null;

        try {
            if (idStr != null && !idStr.trim().isEmpty()) {
                int id = Integer.parseInt(idStr);
                bracketList = payrollDao.getPitBrackets(id);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        if (bracketList == null || bracketList.isEmpty()) {
            PitBracketVersion latestVersion = payrollDao.getLatestPitBracketVersion();
            if (latestVersion != null) {
                bracketList = payrollDao.getPitBrackets(latestVersion.getId());
                if (effectiveDateStr == null || effectiveDateStr.trim().isEmpty()) {
                    effectiveDateStr = latestVersion.getEffectiveDate().toString();
                }
            }
        }

        request.setAttribute("bracketList", bracketList);
        request.setAttribute("effectiveDate", effectiveDateStr);
        request.getRequestDispatcher("/WEB-INF/views/payroll/payroll_pit_bracket.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "This view is read-only.");
    }
}