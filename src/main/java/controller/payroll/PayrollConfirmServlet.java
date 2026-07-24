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
        String allParam = request.getParameter("all");

        String redirectDept = request.getParameter("redirectDepartmentId");
        String redirectMonth = request.getParameter("redirectMonth");
        String redirectYear = request.getParameter("redirectYear");

        Integer deptId = null;
        if (redirectDept != null && !redirectDept.isEmpty() && !"all".equalsIgnoreCase(redirectDept)) {
            try {
                deptId = Integer.parseInt(redirectDept);
            } catch (NumberFormatException e) {
                deptId = null;
            }
        }

        Integer month = null;
        if (redirectMonth != null && !redirectMonth.isEmpty()) {
            try { month = Integer.parseInt(redirectMonth); } catch (NumberFormatException ignored) {}
        }

        Integer year = null;
        if (redirectYear != null && !redirectYear.isEmpty()) {
            try { year = Integer.parseInt(redirectYear); } catch (NumberFormatException ignored) {}
        }

        StringBuilder redirectUrl = new StringBuilder(request.getContextPath() + "/payroll/list?");
        if (redirectDept != null && !redirectDept.isEmpty()) {
            redirectUrl.append("departmentId=").append(redirectDept).append("&");
        }
        if (redirectMonth != null && !redirectMonth.isEmpty()) {
            redirectUrl.append("month=").append(redirectMonth).append("&");
        }
        if (redirectYear != null && !redirectYear.isEmpty()) {
            redirectUrl.append("year=").append(redirectYear).append("&");
        }

        String finalRedirectUrl = redirectUrl.toString();
        if (finalRedirectUrl.endsWith("&") || finalRedirectUrl.endsWith("?")) {
            finalRedirectUrl = finalRedirectUrl.substring(0, finalRedirectUrl.length() - 1);
        }

        try {
            if ("all".equalsIgnoreCase(allParam)) {
                int successCount = payrollDAO.confirmAllDepartmentPayrolls(deptId, month, year);
                if (successCount > 0) {
                    request.getSession().setAttribute("message", "Successfully confirmed all " + successCount + " draft payrolls.");
                } else {
                    request.getSession().setAttribute("error", "No draft payrolls found to confirm for this period.");
                }
                response.sendRedirect(redirectUrl.toString());
                return;
            }

            if (idParam == null || idParam.trim().isEmpty()) {
                request.getSession().setAttribute("error", "Invalid payroll request id.");
                response.sendRedirect(redirectUrl.toString());
                return;
            }

            int payrollId = Integer.parseInt(idParam);
            boolean updated = payrollDAO.updateStatus(payrollId, "CONFIRMED");

            if (updated) {
                request.getSession().setAttribute("message", "Payroll confirmed successfully.");
            } else {
                request.getSession().setAttribute("error", "Only draft payrolls can be confirmed.");
            }
            response.sendRedirect(redirectUrl.toString());

        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("error", "An error occurred during payroll processing.");
            response.sendRedirect(redirectUrl.toString());
        }
    }
}
