package controller.payroll;

import dao.PayrollDAO;
import dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Payroll;
import model.User;

import java.io.IOException;

@WebServlet("/payroll/detail")
public class PayrollDetailServlet extends HttpServlet {

    private final PayrollDAO payrollDAO = new PayrollDAO();
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            String idParam = request.getParameter("id");

            if (idParam == null || idParam.trim().isEmpty()) {
                request.getSession().setAttribute("error", "Invalid payroll request id.");
                response.sendRedirect(request.getContextPath() + "/payroll/list");
                return;
            }

            int payrollId = Integer.parseInt(idParam);
            Payroll payroll = payrollDAO.findById(payrollId);

            if (payroll == null) {
                request.getSession().setAttribute("error", "Payroll record not found.");
                response.sendRedirect(request.getContextPath() + "/payroll/list");
                return;
            }

            User employeeInfo = userDAO.findByIdWithEmployeeCode(payroll.getUserId());

            if (employeeInfo == null) {
                employeeInfo = new User();
                employeeInfo.setId(payroll.getUserId());
                employeeInfo.setFullName("Unknown Employee");
                employeeInfo.setDepartmentName("N/A");
                employeeInfo.setPositionName("N/A");
            }

            request.setAttribute("payroll", payroll);
            request.setAttribute("employee", employeeInfo);
            request.setAttribute("isMyPayroll", "my".equalsIgnoreCase(request.getParameter("from")));

            request.getRequestDispatcher("/WEB-INF/views/payroll/payroll_detail.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            request.getSession().setAttribute("error", "Payroll ID must be a valid number.");
            response.sendRedirect(request.getContextPath() + "/payroll/list");
        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("error", "An error occurred while loading details.");
            response.sendRedirect(request.getContextPath() + "/payroll/list");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
