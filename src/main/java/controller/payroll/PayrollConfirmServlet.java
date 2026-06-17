package controller.payroll;

import dao.PayrollDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Payroll;
import service.PayrollService;

import java.io.IOException;

@WebServlet("/payroll/confirm")
public class PayrollConfirmServlet extends HttpServlet {

    private final PayrollDAO payrollDAO = new PayrollDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idParam = request.getParameter("id");
        double bonus = Double.parseDouble(request.getParameter("bonus"));
        String description = request.getParameter("description");

        if (idParam == null || idParam.trim().isEmpty()) {
            request.getSession().setAttribute("error", "Invalid payroll request id.");
            response.sendRedirect(request.getContextPath() + "/payroll/list");
            return;
        }

        try {
            int payrollId = Integer.parseInt(idParam);
            PayrollService payrollService = new PayrollService();
            boolean updated = payrollService.recalculateAndConfirmPayroll(payrollId, bonus, description);

            if (updated) {
                Payroll payroll = payrollDAO.findById(payrollId);
                request.getSession().setAttribute("payroll", payroll);
                request.getSession().setAttribute("message", "Payroll confirmed successfully.");
            } else {
                request.getSession().setAttribute("error", "Only draft payrolls can be confirmed.");
            }

            response.sendRedirect(request.getContextPath() + "/payroll/detail?id=" + payrollId);
        } catch (NumberFormatException e) {
            request.getSession().setAttribute("error", "Payroll ID must be a valid number.");
            response.sendRedirect(request.getContextPath() + "/payroll/list");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + "/payroll/list");
    }
}
