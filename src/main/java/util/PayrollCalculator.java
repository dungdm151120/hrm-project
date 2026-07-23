package util;

import model.*;

import java.util.List;

public class PayrollCalculator {

    public Payroll calculate(User user, long basicSalary, double expectedHours, AttendanceConfirmedSummary summary,
                             boolean isUnionMember, PayrollSetting setting, String positionName, int numberOfDependents,
                             List<PitBracket> brackets, int sickLeaveDays, int month, int year) {
        Payroll payroll = new Payroll();
        payroll.setUserId(user.getId());
        payroll.setMonth(month);
        payroll.setYear(year);
        payroll.setExpectedHours(expectedHours);
        payroll.setBasicSalary(basicSalary);

        double rateMultiplier = 1.0;
        long bonus = 0;
        String description = null;

        if (positionName.contains("manager") || positionName.equals("system administrator")) {
            bonus += 2000000;
            description = "Lương thưởng cho Manager";
        } else if (positionName.contains("staff")) {
            bonus += 500000;
            description = "Lương thưởng cho Staff";
        }

        payroll.setBonus(bonus);
        payroll.setDescription(description);

        // Get Actual Working Hours
        double totalActualHours = (summary != null) ? summary.getTotalWorkHours() : 0.0;
        payroll.setActualHours(totalActualHours);

        // Calculate Gross Income
        long grossIncome = Math.round((basicSalary / expectedHours) * totalActualHours + bonus);
        payroll.setTotalIncome(grossIncome);

        // Calculate Insurance for Employee
        long employeeSocialInsurance = Math.round(calculateInsuranceAmount(grossIncome, setting.getEmployeeSocialInsurance()));
        long employeeHealthInsurance = Math.round(calculateInsuranceAmount(grossIncome, setting.getEmployeeHealthInsurance()));
        long employeeUnemploymentInsurance = Math.round(calculateInsuranceAmount(grossIncome, setting.getEmployeeUnemploymentInsurance()));
        long employeeUnionFee = 0;
        if (isUnionMember) {
            employeeUnionFee = Math.round(calculateInsuranceAmount(grossIncome, setting.getEmployeeUnion()));
        }
        long employeeTotalInsurance = employeeSocialInsurance + employeeHealthInsurance + employeeUnemploymentInsurance + employeeUnionFee;

        payroll.setSocialInsurance(employeeSocialInsurance);
        payroll.setHealthInsurance(employeeHealthInsurance);
        payroll.setUnemploymentInsurance(employeeUnemploymentInsurance);
        payroll.setUnionFee(employeeUnionFee);

        // Calculate Income Before Tax
        long incomeBeforeTax = grossIncome - employeeTotalInsurance;
        payroll.setIncomeBeforeTax(incomeBeforeTax);

        // Get Taxable Income
        long totalDeductionAmount = (long) (setting.getSelfDeduction() + (setting.getDependentDeduction() * numberOfDependents));
        long taxableIncome = Math.max(0, Math.round(incomeBeforeTax - totalDeductionAmount));
        payroll.setTaxableIncome(taxableIncome);

        // Income Tax
        long incomeTax = Math.round(calculateIncomeTax(taxableIncome, brackets));
        payroll.setIncomeTax(incomeTax);

        // Overtime Pay & Sick Leave Pay
        double sickLeaveRate = (setting.getSickLeaveRate() / 100.0);
        long overtimePay = (summary != null) ? calculateOvertimePay(setting, summary.getOvertimeHours(), expectedHours, basicSalary) : 0;
        long sickLeavePay = Math.round((basicSalary / 24.0) * sickLeaveRate * sickLeaveDays);
        payroll.setOvertimePay(overtimePay);
        payroll.setSickLeavePay(sickLeavePay);

        // Net Income
        long netPay = incomeBeforeTax - incomeTax + overtimePay + sickLeavePay;
        payroll.setNetPay(netPay);

        // Calculate Insurance Employer pay for each Employee
        long companySocialInsurance = Math.round(calculateInsuranceAmount(grossIncome, setting.getCompanySocialInsurance()));
        long companyHealthInsurance = Math.round(calculateInsuranceAmount(grossIncome, setting.getCompanyHealthInsurance()));
        long companyUnemploymentInsurance = Math.round(calculateInsuranceAmount(grossIncome, setting.getCompanyUnemploymentInsurance()));
        long companyUnionFee = Math.round(calculateInsuranceAmount(grossIncome, setting.getCompanyUnion()));
        payroll.setCompanySocialInsurance(companySocialInsurance);
        payroll.setCompanyHealthInsurance(companyHealthInsurance);
        payroll.setCompanyUnemploymentInsurance(companyUnemploymentInsurance);
        payroll.setCompanyUnionFee(companyUnionFee);
        payroll.setStatus("DRAFT");

        return payroll;
    }

    private long calculateOvertimePay(PayrollSetting setting, double overtimeHours, double expectedHours, long basicSalary) {
        double hourlyRate = (expectedHours > 0) ? ((double) basicSalary / expectedHours) : 0;
        return Math.round(overtimeHours * hourlyRate * (setting.getOtWeekdayRate() / 100));
    }

    public Long calculateInsuranceAmount(long grossIncome, double ratePercentage) {
        return Math.round(grossIncome * (ratePercentage / 100.0));
    }

    public double calculateIncomeTax(long taxableIncome, List<PitBracket> brackets) {
        if (taxableIncome <= 0 || brackets == null || brackets.isEmpty()) return 0.0;

        double tax = 0.0;

        for (PitBracket bracket : brackets) {
            long min = bracket.getMinValue();
            Long max = bracket.getMaxValue();
            double rate = bracket.getTaxRate() / 100.0;

            if (taxableIncome <= min) {
                break;
            }

            long taxableAmountInBracket = 0;

            if (max == null || taxableIncome < max) {
                taxableAmountInBracket = (long) (taxableIncome - min);
                tax += Math.round(taxableAmountInBracket * rate);
                break;
            } else {
                taxableAmountInBracket = max - min;
                tax += Math.round(taxableAmountInBracket * rate);
            }
        }

        return tax;
    }

}
