package controller.payroll;

import dao.PayrollDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;

@WebServlet("/payroll/setting/update")
public class PayrollSettingUpdateServlet extends HttpServlet {

    private final PayrollDAO payrollDao = new PayrollDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("setting", payrollDao.getLatestPayrollSetting());
        request.getRequestDispatcher("/WEB-INF/views/payroll/payroll_setting_update.jsp").forward(request, response);
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

            double sickLeaveRate = Double.parseDouble(request.getParameter("sickLeaveRate"));
            double otWeekdayRate = Double.parseDouble(request.getParameter("otWeekdayRate"));
            double otWeekendRate = Double.parseDouble(request.getParameter("otWeekendRate"));
            double otHolidayRate = Double.parseDouble(request.getParameter("otHolidayRate"));

            double companySocialInsurance = Double.parseDouble(request.getParameter("companySocialInsurance"));
            double companyHealthInsurance = Double.parseDouble(request.getParameter("companyHealthInsurance"));
            double companyUnemploymentInsurance = Double.parseDouble(request.getParameter("companyUnemploymentInsurance"));
            double companyUnion = Double.parseDouble(request.getParameter("companyUnion"));

            long selfDeduction = Long.parseLong(request.getParameter("selfDeduction"));
            long dependentDeduction = Long.parseLong(request.getParameter("dependentDeduction"));

            int effectiveMonth = Integer.parseInt(request.getParameter("effectiveMonth"));
            int effectiveYear = Integer.parseInt(request.getParameter("effectiveYear"));

            model.PayrollSetting setting = new model.PayrollSetting();
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
            setting.setSickLeaveRate(sickLeaveRate);
            setting.setOtWeekdayRate(otWeekdayRate);
            setting.setOtWeekendRate(otWeekendRate);
            setting.setOtHolidayRate(otHolidayRate);

            LocalDate effectiveDate = LocalDate.of(effectiveYear, effectiveMonth, 1);
            setting.setEffectiveDate(effectiveDate);

            boolean isUpdated = payrollDao.insertPayrollSetting(setting);

            if (isUpdated) {
                request.getSession().setAttribute("message", "New payroll settings version applied successfully!");
            } else {
                request.getSession().setAttribute("error", "Failed to apply new payroll settings.");
            }

            response.sendRedirect(request.getContextPath() + "/payroll/setting/list");
        } catch (NumberFormatException e) {
            request.getSession().setAttribute("error", "Invalid input data format. Please check your numbers.");
            response.sendRedirect(request.getContextPath() + "/payroll/setting/list");
        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("error", "An error occurred: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/payroll/setting/list");
        }
    }
}