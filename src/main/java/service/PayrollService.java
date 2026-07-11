package service;

import dao.*;
import model.*;
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
    private UserDAO userDAO = new UserDAO();

    public Payroll generateMonthlyPayroll(User user, int month, int year, long basicSalary) {

        double expectedHours = calculateExpectedHours(month, year);
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        AttendanceConfirmedSummary summary = attendanceDAO.getSummaryConfirmedAttendanceByUser(user.getId(), startDate, endDate);
        PayrollSetting setting = payrollDAO.getPayrollSetting();
        List<PitBracket> brackets = payrollDAO.getPitBrackets();

        String positionName = (user.getPositionName() != null) ? user.getPositionName().toLowerCase() : "";
        int numberOfDependents = payrollDAO.countDependentByUserId(user.getId(), month, year);

        double rateMultiplier = 1.0;
        long bonus = 0;
        String description = null;

        if (positionName.contains("manager") || positionName.equals("system administrator")) {
            rateMultiplier = 2.0;
            bonus = 2000000;
            description = "Lương thưởng cho Manager";
        } else if (positionName.contains("staff")) {
            rateMultiplier = 1.5;
        }

        // Get Actual Working Hours
        double totalActualHours = summary.getTotalWorkHours();

        // Calculate Gross Income
        long grossIncome = Math.round(((basicSalary * rateMultiplier) / expectedHours) * totalActualHours);

        // Calculate Insurance for Employee
        long employeeSocialInsurance = Math.round(calculator.calculateInsuranceAmount(grossIncome, setting.getEmployeeSocialInsurance()));
        long employeeHealthInsurance = Math.round(calculator.calculateInsuranceAmount(grossIncome, setting.getEmployeeHealthInsurance()));
        long employeeUnemploymentInsurance = Math.round(calculator.calculateInsuranceAmount(grossIncome, setting.getEmployeeUnemploymentInsurance()));
        boolean isUnionMember = payrollDAO.isUnionMember(user.getId());
        long employeeUnionFee = 0;
        if (isUnionMember) {
            employeeUnionFee = Math.round(calculator.calculateInsuranceAmount(grossIncome, setting.getEmployeeUnion()));
        }
        long employeeTotalInsurance = employeeSocialInsurance + employeeHealthInsurance + employeeUnemploymentInsurance + employeeUnionFee;

        // Calculate Income Before Tax
        long incomeBeforeTax = grossIncome - employeeTotalInsurance;

        // Get Taxable Income
        long totalDeductionAmount = (long) (setting.getSelfDeduction() + (setting.getDependentDeduction() * numberOfDependents));
        long taxableIncome = Math.max(0, Math.round(incomeBeforeTax - totalDeductionAmount));

        // Income Tax
        long incomeTax = Math.round(calculator.calculateIncomeTax(taxableIncome, brackets));

        // Overtime Pay
        long overtimePay = calculateOvertimePay(summary.getOvertimeHours(), expectedHours, basicSalary);

        // Net Income
        long netPay = incomeBeforeTax - incomeTax + bonus + overtimePay;

        // Calculate Insurance Employer pay for each Employee
        long companySocialInsurance = Math.round(calculator.calculateInsuranceAmount(grossIncome, setting.getCompanySocialInsurance()));
        long companyHealthInsurance = Math.round(calculator.calculateInsuranceAmount(grossIncome, setting.getCompanyHealthInsurance()));
        long companyUnemploymentInsurance = Math.round(calculator.calculateInsuranceAmount(grossIncome, setting.getCompanyUnemploymentInsurance()));
        long companyUnionFee = Math.round(calculator.calculateInsuranceAmount(grossIncome, setting.getCompanyUnion()));

        Payroll payroll = new Payroll();
        payroll.setUserId(user.getId());
        payroll.setMonth(month);
        payroll.setYear(year);
        payroll.setExpectedHours(expectedHours);
        payroll.setActualHours(totalActualHours);
        payroll.setBasicSalary(basicSalary);
        payroll.setRateMultiplier(rateMultiplier);
        payroll.setTotalIncome(grossIncome);
        payroll.setBonus(bonus);
        payroll.setDescription(description);

        payroll.setSocialInsurance(employeeSocialInsurance);
        payroll.setHealthInsurance(employeeHealthInsurance);
        payroll.setUnemploymentInsurance(employeeUnemploymentInsurance);
        payroll.setUnionFee(employeeUnionFee);

        payroll.setIncomeBeforeTax(incomeBeforeTax);
        payroll.setTaxableIncome(taxableIncome);
        payroll.setIncomeTax(incomeTax);
        payroll.setOvertimePay(overtimePay);
        payroll.setNetPay(netPay);

        payroll.setCompanySocialInsurance(companySocialInsurance);
        payroll.setCompanyHealthInsurance(companyHealthInsurance);
        payroll.setCompanyUnemploymentInsurance(companyUnemploymentInsurance);
        payroll.setCompanyUnionFee(companyUnionFee);
        payroll.setStatus("DRAFT");

        boolean isSaved = payrollDAO.savePayroll(payroll);
        return isSaved ? payroll : null;
    }

    private long calculateOvertimePay(double overtimeHours, double expectedHours, long basicSalary) {
        double hourlyRate = (expectedHours > 0) ? ((double) basicSalary / expectedHours) : 0;
        return Math.round(overtimeHours * hourlyRate * 1.5);
    }

    public int generateBulkPayroll(List<User> users, int month, int year, Integer departmentId) throws Exception{

        Integer queryDeptId = (departmentId != null && departmentId == 0) ? null : departmentId;

        boolean hasSnapshot = attendanceDAO.hasAttendanceSnapshot(month, year, queryDeptId);

        if (!hasSnapshot) {
            throw new Exception("There is no confirmed attendance for this period in this department");
        }

        int totalUsersInDept = users.size();

        if (totalUsersInDept == 0) {
            throw new Exception("This department has no active employees to generate payroll.");
        }

        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        int employeesWithAttendance = attendanceDAO.countEmployeesWithAttendance(queryDeptId, startDate, endDate);

        if (employeesWithAttendance < totalUsersInDept) {
            int missingCount = totalUsersInDept - employeesWithAttendance;
            throw new Exception("Cannot generate payroll! There are " + missingCount
                    + " employee(s) in this department who do not have attendance data for " + month + "/" + year + ".");
        }

        int successCount = 0;
        for (User user : users) {
            int userId = user.getId();

            User detailedUser = userDAO.findByIdWithEmployeeCode(userId);
            if (detailedUser == null) {
                continue;
            }

            BigDecimal activeSalary = laborContractDAO.findActiveSalaryByUserId(userId);
            long basicSalary = activeSalary.longValue();

            Payroll payroll = this.generateMonthlyPayroll(detailedUser, month, year, basicSalary);
            if (payroll != null) {
                successCount++;
            }
        }
        return successCount;
    }

    private double calculateExpectedHours(int month, int year) {
        YearMonth yearMonth = YearMonth.of(year, month);
        int weekdays = 0;
        for (int day = 1; day <= yearMonth.lengthOfMonth(); day++) {
            LocalDate date = yearMonth.atDay(day);
            if (date.getDayOfWeek().getValue() < 6) {
                weekdays++;
            }
        }
        return weekdays * 8.0;
    }
}