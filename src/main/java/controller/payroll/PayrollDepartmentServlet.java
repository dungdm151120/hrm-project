package controller.payroll;

import dao.DepartmentDAO;
import dao.PayrollDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Department;
import model.DepartmentPayrollSummary;

import java.io.IOException;
import java.util.List;

@WebServlet("/payroll/department")
public class PayrollDepartmentServlet extends HttpServlet {

    private final DepartmentDAO departmentDAO = new DepartmentDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String deptParam = request.getParameter("departmentId");
        String monthParam = request.getParameter("month");
        String yearParam = request.getParameter("year");
        String pageParam = request.getParameter("page");

        Integer departmentId = (deptParam != null && !deptParam.isEmpty()) ? Integer.parseInt(deptParam) : null;
        Integer month = (monthParam != null && !monthParam.isEmpty()) ? Integer.parseInt(monthParam) : null;
        Integer year = (yearParam != null && !yearParam.isEmpty()) ? Integer.parseInt(yearParam) : null;

        int currentPage = 1;
        int pageSize = 6;
        if (pageParam != null && !pageParam.isEmpty()) {
            try { currentPage = Integer.parseInt(pageParam); }
            catch (NumberFormatException ignored) {}
        }

        PayrollDAO payrollDAO = new PayrollDAO();
        int totalRows = payrollDAO.countDepartmentPayrollSummaries(departmentId, month, year);
        int totalPages = (int) Math.ceil((double) totalRows / pageSize);
        if (currentPage > totalPages && totalPages > 0) currentPage = totalPages;
        int offset = (currentPage - 1) * pageSize;

        List<DepartmentPayrollSummary> summaryList = payrollDAO.findDepartmentPayrollSummaries(departmentId, month, year, offset, pageSize);
        List<Department> departments = departmentDAO.getAllDepartments();

        request.setAttribute("departments", departments);
        request.setAttribute("summaryList", summaryList);
        request.setAttribute("currentPage", currentPage);
        request.setAttribute("totalPage", totalPages);
        request.setAttribute("departmentId", departmentId);
        request.setAttribute("month", month);
        request.setAttribute("year", year);
        request.setAttribute("currentYear", java.time.Year.now().getValue());

        request.getRequestDispatcher("/WEB-INF/views/payroll/payroll_department.jsp").forward(request, response);
    }
}