package model;

import java.text.DateFormatSymbols;
import java.util.Locale;

public class DepartmentPayrollSummary {
    private int departmentId;
    private String departmentName;
    private int month;
    private int year;
    private double totalPayroll;

    public int getDepartmentId() {
        return departmentId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

    public double getTotalPayroll() {
        return totalPayroll;
    }

    public void setDepartmentId(int departmentId) {
        this.departmentId = departmentId;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setTotalPayroll(double totalPayroll) {
        this.totalPayroll = totalPayroll;
    }

    public String getMonthName() {
        if (this.month >= 1 && this.month <= 12) {
            return new DateFormatSymbols(Locale.ENGLISH).getMonths()[this.month - 1];
        }
        return "";
    }
}
