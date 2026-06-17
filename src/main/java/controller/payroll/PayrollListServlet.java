package controller.payroll;

import dao.PayrollDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Payroll;
import model.User;

import java.io.IOException;
import java.time.Year;
import java.util.List;

@WebServlet({"/payroll/list", "/payroll/my"})
public class PayrollListServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String keyword = request.getParameter("search");
        String statusParam = request.getParameter("status");
        String monthParam = request.getParameter("month");
        String yearParam = request.getParameter("year");
        String sort = request.getParameter("sort");
        String pageParam = request.getParameter("page");
        boolean isMyPayroll = "/payroll/my".equals(request.getServletPath());
        Integer userId = null;
        Integer month = parseIntegerInRange(monthParam, 1, 12);
        Integer year = parseIntegerInRange(yearParam, 1900, 9999);

        if (isMyPayroll) {
            HttpSession session = request.getSession(false);
            User currentUser = session == null ? null : (User) session.getAttribute("currentUser");
            if (currentUser == null) {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }
            userId = currentUser.getId();
            statusParam = "confirmed";
        } else if (statusParam == null || statusParam.trim().isEmpty()) {
            statusParam = "all";
        }

        int currentPage = 1;
        int pageSize = 10;
        if (pageParam != null && !pageParam.isEmpty()) {
            try {
                currentPage = Integer.parseInt(pageParam);
                if (currentPage < 1) currentPage = 1;
            } catch (NumberFormatException ignored) {}
        }

        PayrollDAO payrollDAO = new PayrollDAO();

        int totalRows = payrollDAO.countPayrolls(keyword, statusParam, userId, month, year);
        int totalPages = (int) Math.ceil((double) totalRows / pageSize);

        if (currentPage > totalPages && totalPages > 0) {
            currentPage = totalPages;
        }

        int offset = (currentPage - 1) * pageSize;

        List<Payroll> payrollList = payrollDAO.findPayrollsAdvanced(keyword, statusParam, sort, offset, pageSize, userId, month, year);

        request.setAttribute("payrollList", payrollList);
        request.setAttribute("currentPage", currentPage);
        request.setAttribute("totalPage", totalPages);

        request.setAttribute("keyword", keyword);
        request.setAttribute("status", statusParam);
        request.setAttribute("month", month);
        request.setAttribute("year", year);
        request.setAttribute("currentYear", Year.now().getValue());
        request.setAttribute("sort", sort);
        request.setAttribute("isMyPayroll", isMyPayroll);

        request.getRequestDispatcher("/WEB-INF/views/payroll/payroll_list.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    private Integer parseIntegerInRange(String value, int min, int max) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        try {
            int parsed = Integer.parseInt(value.trim());
            return parsed >= min && parsed <= max ? parsed : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
