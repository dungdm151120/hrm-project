package controller.payroll;

import dao.PayrollDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/payroll/setting")
public class PayrollSettingServlet extends HttpServlet {

    PayrollDAO payrollDao = new PayrollDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("setting", payrollDao.getPayrollSetting());
        request.getRequestDispatcher("/WEB-INF/views/payroll/payroll_setting.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));

            double employeeSocialInsurance = Double.parseDouble(request.getParameter("employeeSocialInsurance"));
            double employeeHealthInsurance = Double.parseDouble(request.getParameter("employeeHealthInsurance"));
            double employeeUnemploymentInsurance = Double.parseDouble(request.getParameter("employeeUnemploymentInsurance"));
            double employeeUnion = Double.parseDouble(request.getParameter("employeeUnion"));

            double companySocialInsurance = Double.parseDouble(request.getParameter("companySocialInsurance"));
            double companyHealthInsurance = Double.parseDouble(request.getParameter("companyHealthInsurance"));
            double companyUnemploymentInsurance = Double.parseDouble(request.getParameter("companyUnemploymentInsurance"));
            double companyUnion = Double.parseDouble(request.getParameter("companyUnion"));

            long selfDeduction = Long.parseLong(request.getParameter("selfDeduction"));
            long dependentDeduction = Long.parseLong(request.getParameter("dependentDeduction"));
            String effectiveDateStr = request.getParameter("effectiveDate");

            model.PayrollSetting setting = new model.PayrollSetting();
            setting.setId(id);
            setting.setEmployeeSocialInsurance(employeeSocialInsurance);
            setting.setEmployeeHealthInsurance(employeeHealthInsurance);
            setting.setEmployeeUnemploymentInsurance(employeeUnemploymentInsurance);
            setting.setEmployeeUnion(employeeUnion);
            setting.setCompanySocialInsurance(companySocialInsurance);
            setting.setCompanyHealthInsurance(companyHealthInsurance);
            setting.setCompanyUnemploymentInsurance(companyUnemploymentInsurance);
            setting.setCompanyUnion(companyUnion);
            setting.setSelfDeduction(selfDeduction);
            setting.setDependentDeduction(dependentDeduction);
            setting.setEffectiveDate(java.time.LocalDate.parse(effectiveDateStr));

            boolean isUpdated = payrollDao.updatePayrollSetting(setting);

            if (isUpdated) {
                request.getSession().setAttribute("message", "Payroll settings updated successfully!");
            } else {
                request.getSession().setAttribute("error", "Failed to update payroll settings.");
            }

            response.sendRedirect(request.getContextPath() + "/payroll/setting");
        } catch (NumberFormatException e) {
            request.getSession().setAttribute("error", "Invalid input data format.");
            response.sendRedirect(request.getContextPath() + "/payroll/update_component");
        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("error", "An error occurred: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/payroll/update_component");
        }
    }
}