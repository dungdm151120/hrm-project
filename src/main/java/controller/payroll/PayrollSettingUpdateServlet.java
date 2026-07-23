package controller.payroll;

import dao.PayrollDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.PayrollSetting;

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
        StringBuilder errorMsg = new StringBuilder();
        int currentYear = java.time.Year.now().getValue();

        double employeeSocialInsurance = parseAndValidateDouble(
                request.getParameter("employeeSocialInsurance"), "Employee Social Insurance", 0.0, 100.0, errorMsg);

        double employeeHealthInsurance = parseAndValidateDouble(
                request.getParameter("employeeHealthInsurance"), "Employee Health Insurance", 0.0, 100.0, errorMsg);

        double employeeUnemploymentInsurance = parseAndValidateDouble(
                request.getParameter("employeeUnemploymentInsurance"), "Employee Unemployment Insurance", 0.0, 100.0, errorMsg);

        double employeeUnion = parseAndValidateDouble(
                request.getParameter("employeeUnion"), "Employee Union Fee", 0.0, 100.0, errorMsg);

        long selfDeduction = parseAndValidateLong(
                request.getParameter("selfDeduction"), "Personal Deduction", 0L, errorMsg);

        long dependentDeduction = parseAndValidateLong(
                request.getParameter("dependentDeduction"), "Dependent Deduction", 0L, errorMsg);

        double companySocialInsurance = parseAndValidateDouble(
                request.getParameter("companySocialInsurance"), "Company Social Insurance", 0.0, 100.0, errorMsg);

        double companyHealthInsurance = parseAndValidateDouble(
                request.getParameter("companyHealthInsurance"), "Company Health Insurance", 0.0, 100.0, errorMsg);

        double companyUnemploymentInsurance = parseAndValidateDouble(
                request.getParameter("companyUnemploymentInsurance"), "Company Unemployment Insurance", 0.0, 100.0, errorMsg);

        double companyUnion = parseAndValidateDouble(
                request.getParameter("companyUnion"), "Company Union Fee", 0.0, 100.0, errorMsg);

        double sickLeaveRate = parseAndValidateDouble(
                request.getParameter("sickLeaveRate"), "Sick Leave Rate", 0.0, 100.0, errorMsg);

        double otWeekdayRate = parseAndValidateDouble(
                request.getParameter("otWeekdayRate"), "OT Weekday Rate", 100.0, 300.0, errorMsg);

        double otWeekendRate = parseAndValidateDouble(
                request.getParameter("otWeekendRate"), "OT Weekend Rate", 100.0, 400.0, errorMsg);

        double otHolidayRate = parseAndValidateDouble(
                request.getParameter("otHolidayRate"), "OT Holiday Rate", 100.0, 500.0, errorMsg);

        int effectiveMonth = parseAndValidateInt(
                request.getParameter("effectiveMonth"), "Effective Month", 1, 12, errorMsg);

        int effectiveYear = Integer.parseInt(request.getParameter("effectiveYear"));

        if (errorMsg.length() > 0) {
            request.getSession().setAttribute("error", errorMsg.toString());
            response.sendRedirect(request.getContextPath() + "/payroll/setting/list");
            return;
        }

        try {
            PayrollSetting setting = new model.PayrollSetting();
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

    private double parseAndValidateDouble(String rawVal, String fieldName, double min, double max, StringBuilder errorMsg) {
        if (rawVal == null || rawVal.trim().isEmpty()) {
            errorMsg.append(fieldName).append(" is required.<br/>");
            return 0.0;
        }
        try {
            double val = Double.parseDouble(rawVal.trim());
            if (val < min || val > max) {
                errorMsg.append(fieldName).append(" must be between ").append(min).append(" and ").append(max).append(".<br/>");
            }
            return val;
        } catch (NumberFormatException e) {
            errorMsg.append(fieldName).append(" must be a valid number.<br/>");
            return 0.0;
        }
    }

    private long parseAndValidateLong(String rawVal, String fieldName, long min, StringBuilder errorMsg) {
        if (rawVal == null || rawVal.trim().isEmpty()) {
            errorMsg.append(fieldName).append(" is required.<br/>");
            return 0L;
        }
        try {
            long val = Long.parseLong(rawVal.trim());
            if (val < min) {
                errorMsg.append(fieldName).append(" cannot be negative.<br/>");
            }
            return val;
        } catch (NumberFormatException e) {
            errorMsg.append(fieldName).append(" must be a valid number.<br/>");
            return 0L;
        }
    }

    private int parseAndValidateInt(String rawVal, String fieldName, int min, int max, StringBuilder errorMsg) {
        if (rawVal == null || rawVal.trim().isEmpty()) {
            errorMsg.append(fieldName).append(" is required.<br/>");
            return 0;
        }
        try {
            int val = Integer.parseInt(rawVal.trim());
            if (val < min || val > max) {
                errorMsg.append(fieldName).append(" must be between ").append(min).append(" and ").append(max).append(".<br/>");
            }
            return val;
        } catch (NumberFormatException e) {
            errorMsg.append(fieldName).append(" must be a valid integer.<br/>");
            return 0;
        }
    }
}