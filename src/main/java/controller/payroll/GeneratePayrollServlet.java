package controller.payroll;

import dao.DepartmentDAO;
import dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.User;
import service.PayrollService;

import java.io.IOException;
import java.util.List;

@WebServlet("/payroll/generate")
public class GeneratePayrollServlet extends HttpServlet {

    private PayrollService payrollService = new PayrollService();
    private UserDAO userDAO = new UserDAO();
    private DepartmentDAO departmentDAO = new DepartmentDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("departments", departmentDAO.getAllDepartments());
        request.getRequestDispatcher("/WEB-INF/views/payroll/generate_payrolls.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int departmentId = Integer.parseInt(request.getParameter("departmentId"));
            int month = Integer.parseInt(request.getParameter("month"));
            int year = Integer.parseInt(request.getParameter("year"));
            double expectedHours = 172.0;

            List<User> employeesToCalculate;

            if (departmentId == 0) {
                employeesToCalculate = userDAO.getAllActiveUsers();
            } else {
                employeesToCalculate = userDAO.findActiveByDepartmentId(departmentId);
            }

            if (employeesToCalculate == null || employeesToCalculate.isEmpty()) {
                request.setAttribute("error", "No employees found for the selected department.");
                request.setAttribute("departments", departmentDAO.getAllDepartments());
                request.getRequestDispatcher("/WEB-INF/views/payroll/generate_payrolls.jsp").forward(request, response);
                return;
            }

            int successCount = payrollService.generateBulkPayroll(employeesToCalculate, month, year, expectedHours);

            request.setAttribute("success", "Successfully generated " + successCount + " payroll records for " + month + "/" + year + ".");

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "An error occurred during payroll generation: " + e.getMessage());
        }

        request.setAttribute("departments", departmentDAO.getAllDepartments());
        request.getRequestDispatcher("/WEB-INF/views/payroll/generate_payrolls.jsp").forward(request, response);
    }
}