package controller.payroll;

import dao.PayrollDAO;
import dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Payroll;
import model.PayrollSetting;
import model.User;

import java.io.IOException;
import java.time.Year;
import java.util.List;

@WebServlet("/payroll/my")
public class PayrollMyServlet extends HttpServlet {

    private final PayrollDAO payrollDAO = new PayrollDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("currentUser") == null) {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }

            User currentUser = (User) session.getAttribute("currentUser");
            int employeeId = currentUser.getId();

            String monthParam = request.getParameter("month");
            String yearParam = request.getParameter("year");
            String pageParam = request.getParameter("page");

            Integer month = (monthParam != null && !monthParam.isEmpty()) ? Integer.parseInt(monthParam) : null;
            Integer year = (yearParam != null && !yearParam.isEmpty()) ? Integer.parseInt(yearParam) : null;

            int currentPage = 1;
            int pageSize = 6;
            if (pageParam != null && !pageParam.isEmpty()) {
                try { currentPage = Integer.parseInt(pageParam); }
                catch (NumberFormatException ignored) {}
            }

            int totalRows = payrollDAO.countPayrolls(null, "CONFIRMED", employeeId, month, year, null);
            int totalPages = (int) Math.ceil((double) totalRows / pageSize);
            if (currentPage > totalPages && totalPages > 0) currentPage = totalPages;
            int offset = (currentPage - 1) * pageSize;

            List<Payroll> myPayrollList = payrollDAO.findPayrollsAdvanced(
                    null,
                    "CONFIRMED",
                    "desc",
                    offset,
                    pageSize,
                    employeeId,
                    month,
                    year,
                    null
            );

            request.setAttribute("payrollList", myPayrollList);
            request.setAttribute("currentPage", currentPage);
            request.setAttribute("totalPage", totalPages);
            request.setAttribute("month", month);
            request.setAttribute("year", year);
            request.setAttribute("currentYear", Year.now().getValue());

            request.getRequestDispatcher("/WEB-INF/views/payroll/payroll_my_list.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("error", "An error occurred while loading your payroll history.");
            response.sendRedirect(request.getContextPath() + "/dashboard");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}