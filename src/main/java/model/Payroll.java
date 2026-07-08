package model;

import java.time.LocalDateTime;

public class Payroll {
    private int id;
    private int userId;
    private String employeeName;
    private String departmentName;
    private String positionName;
    private int month;
    private int year;
    private double expectedHours;
    private double actualHours;
    private long basicSalary;
    private double rateMultiplier;
    private long totalIncome;
    private long bonus;
    private String description;
    private long socialInsurance;
    private long healthInsurance;
    private long unemploymentInsurance;
    private long unionFee;
    private long incomeBeforeTax;
    private long taxableIncome;
    private long incomeTax;
    private long overtimePay;
    private long netPay;
    private long companySocialInsurance;
    private long companyHealthInsurance;
    private long companyUnemploymentInsurance;
    private long companyUnionFee;
    private String status;
    private LocalDateTime createdAt;

    public Payroll() {}

    public Payroll(int id, int userId, String employeeName, String departmentName, String positionName, int month, int year, 
                   double expectedHours, double actualHours, long basicSalary, double rateMultiplier, long totalIncome, long bonus, 
                   String description, long socialInsurance, long healthInsurance, long unemploymentInsurance,
                   long unionFee, long incomeBeforeTax, long taxableIncome, long incomeTax, long netPay,
                   long companySocialInsurance, long companyHealthInsurance, long companyUnemploymentInsurance, long companyUnionFee,
                   String status, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.employeeName = employeeName;
        this.departmentName = departmentName;
        this.positionName = positionName;
        this.month = month;
        this.year = year;
        this.expectedHours = expectedHours;
        this.actualHours = actualHours;
        this.basicSalary = basicSalary;
        this.rateMultiplier = rateMultiplier;
        this.totalIncome = totalIncome;
        this.bonus = bonus;
        this.description = description;
        this.socialInsurance = socialInsurance;
        this.healthInsurance = healthInsurance;
        this.unemploymentInsurance = unemploymentInsurance;
        this.unionFee = unionFee;
        this.incomeBeforeTax = incomeBeforeTax;
        this.taxableIncome = taxableIncome;
        this.incomeTax = incomeTax;
        this.netPay = netPay;
        this.companySocialInsurance = companySocialInsurance;
        this.companyHealthInsurance = companyHealthInsurance;
        this.companyUnemploymentInsurance = companyUnemploymentInsurance;
        this.companyUnionFee = companyUnionFee;
        this.status = status;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public String getPositionName() {
        return positionName;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

    public double getExpectedHours() {
        return expectedHours;
    }

    public double getActualHours() {
        return actualHours;
    }

    public long getBasicSalary() {
        return basicSalary;
    }

    public double getRateMultiplier() {
        return rateMultiplier;
    }

    public long getTotalIncome() {
        return totalIncome;
    }

    public long getBonus() {
        return bonus;
    }

    public String getDescription() {
        return description;
    }

    public long getSocialInsurance() {
        return socialInsurance;
    }

    public long getHealthInsurance() {
        return healthInsurance;
    }

    public long getUnemploymentInsurance() {
        return unemploymentInsurance;
    }

    public long getUnionFee() {
        return unionFee;
    }

    public long getIncomeBeforeTax() {
        return incomeBeforeTax;
    }

    public long getTaxableIncome() {
        return taxableIncome;
    }

    public long getIncomeTax() {
        return incomeTax;
    }

    public long getNetPay() {
        return netPay;
    }

    public long getCompanySocialInsurance() {
        return companySocialInsurance;
    }

    public long getCompanyHealthInsurance() {
        return companyHealthInsurance;
    }

    public long getCompanyUnemploymentInsurance() {
        return companyUnemploymentInsurance;
    }

    public long getCompanyUnionFee() {
        return companyUnionFee;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public void setPositionName(String positionName) {
        this.positionName = positionName;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setExpectedHours(double expectedHours) {
        this.expectedHours = expectedHours;
    }

    public void setActualHours(double actualHours) {
        this.actualHours = actualHours;
    }

    public void setBasicSalary(long basicSalary) {
        this.basicSalary = basicSalary;
    }

    public void setRateMultiplier(double rateMultiplier) {
        this.rateMultiplier = rateMultiplier;
    }

    public void setTotalIncome(long totalIncome) {
        this.totalIncome = totalIncome;
    }

    public void setBonus(long bonus) {
        this.bonus = bonus;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setSocialInsurance(long socialInsurance) {
        this.socialInsurance = socialInsurance;
    }

    public void setHealthInsurance(long healthInsurance) {
        this.healthInsurance = healthInsurance;
    }

    public void setUnemploymentInsurance(long unemploymentInsurance) {
        this.unemploymentInsurance = unemploymentInsurance;
    }

    public void setUnionFee(long unionFee) {
        this.unionFee = unionFee;
    }

    public void setIncomeBeforeTax(long incomeBeforeTax) {
        this.incomeBeforeTax = incomeBeforeTax;
    }

    public void setTaxableIncome(long taxableIncome) {
        this.taxableIncome = taxableIncome;
    }

    public void setIncomeTax(long incomeTax) {
        this.incomeTax = incomeTax;
    }

    public void setNetPay(long netPay) {
        this.netPay = netPay;
    }

    public void setCompanySocialInsurance(long companySocialInsurance) {
        this.companySocialInsurance = companySocialInsurance;
    }

    public void setCompanyHealthInsurance(long companyHealthInsurance) {
        this.companyHealthInsurance = companyHealthInsurance;
    }

    public void setCompanyUnemploymentInsurance(long companyUnemploymentInsurance) {
        this.companyUnemploymentInsurance = companyUnemploymentInsurance;
    }

    public void setCompanyUnionFee(long companyUnionFee) {
        this.companyUnionFee = companyUnionFee;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public long getOvertimePay() {
        return overtimePay;
    }

    public void setOvertimePay(long overtimePay) {
        this.overtimePay = overtimePay;
    }
}