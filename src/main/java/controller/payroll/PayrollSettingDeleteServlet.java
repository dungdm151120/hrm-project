package controller.payroll;

import dao.PayrollDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/payroll/setting/delete")
public class PayrollSettingDeleteServlet extends HttpServlet {

    private final PayrollDAO payrollDAO = new PayrollDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String idStr = request.getParameter("id");

            if (idStr != null && !idStr.trim().isEmpty()) {
                int id = Integer.parseInt(idStr);

                boolean deleted = payrollDAO.deletePayrollSetting(id);
                if (deleted) {
                    request.getSession().setAttribute("message", "Payroll setting deleted successfully!");
                } else {
                    request.getSession().setAttribute("error", "Failed to delete payroll setting.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("error", "An error occurred while deleting payroll setting: " + e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/payroll/setting/list");
    }
}