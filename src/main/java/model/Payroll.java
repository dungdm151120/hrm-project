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
    private double basicSalary;
    private double totalIncome;
    private double socialInsurance;
    private double healthInsurance;
    private double unemploymentInsurance;
    private double incomeBeforeTax;
    private double taxableIncome;
    private double incomeTax;
    private double netPay;
    private String status;
    private LocalDateTime createdAt;

    public Payroll() {}

    public Payroll(LocalDateTime createdAt, String status, double netPay, double incomeTax, double taxableIncome, double incomeBeforeTax, double unemploymentInsurance, double healthInsurance, double socialInsurance, double totalIncome, double basicSalary, double actualHours, double expectedHours, int year, int month, String positionName, String departmentName, String employeeName, int userId, int id) {
        this.createdAt = createdAt;
        this.status = status;
        this.netPay = netPay;
        this.incomeTax = incomeTax;
        this.taxableIncome = taxableIncome;
        this.incomeBeforeTax = incomeBeforeTax;
        this.unemploymentInsurance = unemploymentInsurance;
        this.healthInsurance = healthInsurance;
        this.socialInsurance = socialInsurance;
        this.totalIncome = totalIncome;
        this.basicSalary = basicSalary;
        this.actualHours = actualHours;
        this.expectedHours = expectedHours;
        this.year = year;
        this.month = month;
        this.positionName = positionName;
        this.departmentName = departmentName;
        this.employeeName = employeeName;
        this.userId = userId;
        this.id = id;
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

    public double getBasicSalary() {
        return basicSalary;
    }

    public double getTotalIncome() {
        return totalIncome;
    }

    public double getSocialInsurance() {
        return socialInsurance;
    }

    public double getHealthInsurance() {
        return healthInsurance;
    }

    public double getUnemploymentInsurance() {
        return unemploymentInsurance;
    }

    public double getIncomeBeforeTax() {
        return incomeBeforeTax;
    }

    public double getTaxableIncome() {
        return taxableIncome;
    }

    public double getIncomeTax() {
        return incomeTax;
    }

    public double getNetPay() {
        return netPay;
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

    public void setBasicSalary(double basicSalary) {
        this.basicSalary = basicSalary;
    }

    public void setTotalIncome(double totalIncome) {
        this.totalIncome = totalIncome;
    }

    public void setSocialInsurance(double socialInsurance) {
        this.socialInsurance = socialInsurance;
    }

    public void setHealthInsurance(double healthInsurance) {
        this.healthInsurance = healthInsurance;
    }

    public void setUnemploymentInsurance(double unemploymentInsurance) {
        this.unemploymentInsurance = unemploymentInsurance;
    }

    public void setIncomeBeforeTax(double incomeBeforeTax) {
        this.incomeBeforeTax = incomeBeforeTax;
    }

    public void setTaxableIncome(double taxableIncome) {
        this.taxableIncome = taxableIncome;
    }

    public void setIncomeTax(double incomeTax) {
        this.incomeTax = incomeTax;
    }

    public void setNetPay(double netPay) {
        this.netPay = netPay;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}