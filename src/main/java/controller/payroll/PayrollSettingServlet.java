package controller.payroll;

import dao.PayrollDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.PayrollSetting;

import java.io.IOException;

@WebServlet("/payroll/setting")
public class PayrollSettingServlet extends HttpServlet {

    PayrollDAO payrollDao = new PayrollDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idStr = request.getParameter("id");
        PayrollSetting setting = null;

        try {
            if (idStr != null && !idStr.trim().isEmpty()) {
                int id = Integer.parseInt(idStr);
                setting = payrollDao.getPayrollSettingById(id);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        if (setting == null) {
            setting = payrollDao.getLatestPayrollSetting();
        }

        request.setAttribute("setting", setting);
        request.getRequestDispatcher("/WEB-INF/views/payroll/payroll_setting.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "This view is read-only.");
    }
}