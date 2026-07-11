package model;

import java.time.LocalDate;

public class PayrollSetting {
    private int id;
    private double employeeSocialInsurance;
    private double employeeHealthInsurance;
    private double employeeUnemploymentInsurance;
    private double employeeUnion;
    private double companySocialInsurance;
    private double companyHealthInsurance;
    private double companyUnemploymentInsurance;
    private double companyUnion;
    private long selfDeduction;
    private long dependentDeduction;
    private LocalDate effectiveDate;

    public PayrollSetting() {
    }

    public PayrollSetting(int id, double employeeSocialInsurance, double employeeHealthInsurance, double employeeUnemploymentInsurance,
                          double employeeUnion, double companySocialInsurance, double companyHealthInsurance, double companyUnemploymentInsurance,
                          double companyUnion, long selfDeduction, long dependentDeduction, LocalDate effectiveDate) {
        this.id = id;
        this.employeeSocialInsurance = employeeSocialInsurance;
        this.employeeHealthInsurance = employeeHealthInsurance;
        this.employeeUnemploymentInsurance = employeeUnemploymentInsurance;
        this.employeeUnion = employeeUnion;
        this.companySocialInsurance = companySocialInsurance;
        this.companyHealthInsurance = companyHealthInsurance;
        this.companyUnemploymentInsurance = companyUnemploymentInsurance;
        this.companyUnion = companyUnion;
        this.selfDeduction = selfDeduction;
        this.dependentDeduction = dependentDeduction;
        this.effectiveDate = effectiveDate;
    }

    public LocalDate getEffectiveDate() {
        return effectiveDate;
    }

    public long getDependentDeduction() {
        return dependentDeduction;
    }

    public long getSelfDeduction() {
        return selfDeduction;
    }

    public double getCompanyUnemploymentInsurance() {
        return companyUnemploymentInsurance;
    }

    public double getCompanyHealthInsurance() {
        return companyHealthInsurance;
    }

    public double getCompanySocialInsurance() {
        return companySocialInsurance;
    }

    public double getEmployeeUnemploymentInsurance() {
        return employeeUnemploymentInsurance;
    }

    public double getEmployeeHealthInsurance() {
        return employeeHealthInsurance;
    }

    public double getEmployeeSocialInsurance() {
        return employeeSocialInsurance;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setEmployeeSocialInsurance(double employeeSocialInsurance) {
        this.employeeSocialInsurance = employeeSocialInsurance;
    }

    public void setEmployeeHealthInsurance(double employeeHealthInsurance) {
        this.employeeHealthInsurance = employeeHealthInsurance;
    }

    public void setEmployeeUnemploymentInsurance(double employeeUnemploymentInsurance) {
        this.employeeUnemploymentInsurance = employeeUnemploymentInsurance;
    }

    public void setCompanySocialInsurance(double companySocialInsurance) {
        this.companySocialInsurance = companySocialInsurance;
    }

    public void setCompanyHealthInsurance(double companyHealthInsurance) {
        this.companyHealthInsurance = companyHealthInsurance;
    }

    public void setCompanyUnemploymentInsurance(double companyUnemploymentInsurance) {
        this.companyUnemploymentInsurance = companyUnemploymentInsurance;
    }

    public void setSelfDeduction(long selfDeduction) {
        this.selfDeduction = selfDeduction;
    }

    public void setDependentDeduction(long dependentDeduction) {
        this.dependentDeduction = dependentDeduction;
    }

    public void setEffectiveDate(LocalDate effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public double getEmployeeUnion() {
        return employeeUnion;
    }

    public double getCompanyUnion() {
        return companyUnion;
    }

    public void setCompanyUnion(double companyUnion) {
        this.companyUnion = companyUnion;
    }

    public void setEmployeeUnion(double employeeUnion) {
        this.employeeUnion = employeeUnion;
    }
}
