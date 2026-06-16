package service;

import dao.AttendanceDAO;
import dao.LaborContractDAO;
import dao.PayrollDAO;
import model.AttendanceSummary;
import model.Payroll;
import model.User;
import util.PayrollCalculator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

public class PayrollService {

    private LaborContractDAO laborContractDAO = new LaborContractDAO();
    private PayrollCalculator calculator = new PayrollCalculator();
    private AttendanceDAO attendanceDAO = new AttendanceDAO();
    private PayrollDAO payrollDAO = new PayrollDAO();

    public Payroll generateMonthlyPayroll(int userId, int month, int year, double basicSalary, double expectedHours) {

        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        AttendanceSummary summary = attendanceDAO.getSummaryByUser(userId, startDate, endDate);

        // Get Actual Working Hours
        double totalActualHours = summary.getTotalWorkHours();

        // Calculate Total Income
        double totalIncome = (basicSalary / expectedHours) * totalActualHours;

        // Calculate Insurance
        double socialInsurance = calculator.calculateSocialInsurance(totalIncome);
        double healthInsurance = calculator.calculateHealthInsurance(totalIncome);
        double unemploymentInsurance = calculator.calculateUnemploymentInsurance(totalIncome);
        double totalInsurance = socialInsurance + healthInsurance + unemploymentInsurance;

        // Calculate Income Before Tax
        double incomeBeforeTax = totalIncome - totalInsurance;

        // Get Taxable Income
        double taxableIncome = Math.max(0.0, incomeBeforeTax - 15500000.0);

        // Income Tax
        double incomeTax = calculator.calculateIncomeTax(taxableIncome);

        // Net Income
        double netPay = incomeBeforeTax - incomeTax;

        Payroll payroll = new Payroll();
        payroll.setUserId(userId);
        payroll.setMonth(month);
        payroll.setYear(year);
        payroll.setExpectedHours(expectedHours);
        payroll.setActualHours(totalActualHours);
        payroll.setBasicSalary(basicSalary);
        payroll.setTotalIncome(totalIncome);
        payroll.setSocialInsurance(socialInsurance);
        payroll.setHealthInsurance(healthInsurance);
        payroll.setUnemploymentInsurance(unemploymentInsurance);
        payroll.setIncomeBeforeTax(incomeBeforeTax);
        payroll.setTaxableIncome(taxableIncome);
        payroll.setIncomeTax(incomeTax);
        payroll.setNetPay(netPay);
        payroll.setStatus("Draft");

        boolean isSaved = payrollDAO.savePayroll(payroll);
        return isSaved ? payroll : null;
    }

    public int generateBulkPayroll(List<User> users, int month, int year, double expectedHours) {
        int successCount = 0;

        for (User user : users) {
            int userId = user.getId();

            BigDecimal activeSalary = laborContractDAO.findActiveSalaryByUserId(userId);
            double basicSalary = activeSalary.doubleValue();

            Payroll payroll = this.generateMonthlyPayroll(userId, month, year, basicSalary, expectedHours);
            if (payroll != null) {
                successCount++;
            }
        }
        return successCount;
    }
}