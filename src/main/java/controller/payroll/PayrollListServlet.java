package controller.payroll;

import dao.DepartmentDAO;
import dao.PayrollDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Department;
import model.Payroll;
import model.User;

import java.io.IOException;
import java.time.Year;
import java.util.List;

@WebServlet("/payroll/list")
public class PayrollListServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String keyword = request.getParameter("search");
        String statusParam = request.getParameter("status");
        String monthParam = request.getParameter("month");
        String yearParam = request.getParameter("year");
        String deptParam = request.getParameter("departmentId");
        String sort = request.getParameter("sort");
        String pageParam = request.getParameter("page");

        Integer userId = null;

        java.time.LocalDate today = java.time.LocalDate.now();

        Integer month = parseIntegerInRange(monthParam, 1, 12);
        if (month == null && (monthParam == null || monthParam.trim().isEmpty())) {
            month = today.getMonthValue();
        }

        Integer year = parseIntegerInRange(yearParam, 1900, 9999);
        if (year == null && (yearParam == null || yearParam.trim().isEmpty())) {
            year = today.getYear();
        }

        if (statusParam == null || statusParam.trim().isEmpty()) {
            statusParam = "all";
        }

        if (sort == null || sort.trim().isEmpty()) {
            sort = "name_asc";
        }

        Integer departmentId = null;
        if (deptParam != null && !deptParam.isEmpty() && !"all".equalsIgnoreCase(deptParam)) {
            try {
                departmentId = Integer.parseInt(deptParam);
            } catch (NumberFormatException ignored) {}
        }

        int currentPage = 1;
        int pageSize = 7;
        if (pageParam != null && !pageParam.isEmpty()) {
            try {
                currentPage = Integer.parseInt(pageParam);
                if (currentPage < 1) currentPage = 1;
            } catch (NumberFormatException ignored) {}
        }

        PayrollDAO payrollDAO = new PayrollDAO();
        DepartmentDAO departmentDAO = new DepartmentDAO();

        int totalRows = payrollDAO.countPayrolls(keyword, statusParam, userId, month, year, departmentId);
        int totalPages = (int) Math.ceil((double) totalRows / pageSize);

        if (currentPage > totalPages && totalPages > 0) {
            currentPage = totalPages;
        }

        int offset = (currentPage - 1) * pageSize;

        List<Payroll> payrollList = payrollDAO.findPayrollsAdvanced(keyword, statusParam, sort, offset, pageSize, userId, month, year, departmentId);
        List<Department> departmentList = departmentDAO.getAllDepartments();
        Payroll totalSummary = payrollDAO.calculatePayrollSummary(keyword, statusParam, userId, month, year, departmentId);

        long sumActualBasicSalary = 0;
        for (Payroll p : payrollList) {
            sumActualBasicSalary += p.getActualBasicSalary();
        }

        request.setAttribute("actualBasicSalary", sumActualBasicSalary);
        request.setAttribute("totalSummary", totalSummary);
        request.setAttribute("departmentList", departmentList);
        request.setAttribute("payrollList", payrollList);
        request.setAttribute("currentPage", currentPage);
        request.setAttribute("totalPage", totalPages);
        request.setAttribute("keyword", keyword);
        request.setAttribute("status", statusParam);
        request.setAttribute("month", month);
        request.setAttribute("year", year);
        request.setAttribute("departmentId", departmentId);
        request.setAttribute("currentYear", today.getYear());
        request.setAttribute("sort", sort);

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
