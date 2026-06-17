package service;

import dao.*;
import model.AttendanceSummary;
import model.Payroll;
import model.Position;
import model.User;
import util.PayrollCalculator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

public class PayrollService {

    private LaborContractDAO laborContractDAO = new LaborContractDAO();
    private PayrollCalculator calculator = new PayrollCalculator();
    private AttendanceDAO attendanceDAO = new AttendanceDAO();
    private PayrollDAO payrollDAO = new PayrollDAO();
    private PositionDAO positionDAO = new PositionDAO();

    public Payroll generateMonthlyPayroll(User user, int month, int year, double basicSalary, double expectedHours) {

        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        AttendanceSummary summary = attendanceDAO.getSummaryByUser(user.getId(), startDate, endDate);

        String positionName = (user.getPositionName() != null) ? user.getPositionName().toLowerCase() : "";

        double rateMultiplier = 1.0;
        double bonus = 0.0;
        if (positionName.contains("manager")) {
            rateMultiplier = 2.0;
            bonus = 5000000.0;
        } else if (positionName.contains("staff")) {
            rateMultiplier = 1.5;
        }

        // Get Actual Working Hours
        double totalActualHours = summary.getTotalWorkHours();

        // Calculate Total Income
        double totalIncome = ((basicSalary * rateMultiplier) / expectedHours) * totalActualHours;

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
        double netPay = incomeBeforeTax - incomeTax + bonus;

        Payroll payroll = new Payroll();
        payroll.setUserId(user.getId());
        payroll.setMonth(month);
        payroll.setYear(year);
        payroll.setExpectedHours(expectedHours);
        payroll.setActualHours(totalActualHours);
        payroll.setBasicSalary(basicSalary);
        payroll.setRateMultiplier(rateMultiplier);
        payroll.setTotalIncome(totalIncome);
        payroll.setBonus(bonus);
        payroll.setDescription(null);
        payroll.setSocialInsurance(socialInsurance);
        payroll.setHealthInsurance(healthInsurance);
        payroll.setUnemploymentInsurance(unemploymentInsurance);
        payroll.setIncomeBeforeTax(incomeBeforeTax);
        payroll.setTaxableIncome(taxableIncome);
        payroll.setIncomeTax(incomeTax);
        payroll.setNetPay(netPay);
        payroll.setStatus("DRAFT");

        boolean isSaved = payrollDAO.savePayroll(payroll);
        return isSaved ? payroll : null;
    }

    public boolean recalculateAndConfirmPayroll(int payrollId, double newBonus, String description) {
        Payroll payroll = payrollDAO.findById(payrollId);
        if (payroll == null || !"DRAFT".equalsIgnoreCase(payroll.getStatus())) {
            return false;
        }

        payroll.setBonus(newBonus);
        payroll.setDescription(description);
        payroll.setStatus("CONFIRMED");

        double totalIncome = payroll.getTotalIncome();
        double totalInsurance = payroll.getSocialInsurance() + payroll.getHealthInsurance() + payroll.getUnemploymentInsurance();
        double incomeBeforeTax = totalIncome - totalInsurance;

        double incomeTax = payroll.getIncomeTax();

        double newNetPay = incomeBeforeTax - incomeTax + newBonus;
        payroll.setNetPay(newNetPay);

        return payrollDAO.updatePayrollValuesAndStatus(payroll);
    }

    public int generateBulkPayroll(List<User> users, int month, int year, double expectedHours) {
        int successCount = 0;

        for (User user : users) {
            int userId = user.getId();

            BigDecimal activeSalary = laborContractDAO.findActiveSalaryByUserId(userId);
            double basicSalary = activeSalary.doubleValue();

            Payroll payroll = this.generateMonthlyPayroll(user, month, year, basicSalary, expectedHours);
            if (payroll != null) {
                successCount++;
            }
        }
        return successCount;
    }
}