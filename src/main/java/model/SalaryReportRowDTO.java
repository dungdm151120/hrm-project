package model;

public class SalaryReportRowDTO {
    private String groupName;
    private String departmentName;
    private int employeeCount;
    private long workdayIncome;
    private long productIncome;
    private long overtimeIncome;
    private long sickLeaveIncome;
    private long grossIncome;
    private long totalIncome;

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public int getEmployeeCount() {
        return employeeCount;
    }

    public void setEmployeeCount(int employeeCount) {
        this.employeeCount = employeeCount;
    }

    public long getWorkdayIncome() {
        return workdayIncome;
    }

    public void setWorkdayIncome(long workdayIncome) {
        this.workdayIncome = workdayIncome;
    }

    public long getProductIncome() {
        return productIncome;
    }

    public void setProductIncome(long productIncome) {
        this.productIncome = productIncome;
    }

    public long getOvertimeIncome() {
        return overtimeIncome;
    }

    public void setOvertimeIncome(long overtimeIncome) {
        this.overtimeIncome = overtimeIncome;
    }

    public long getSickLeaveIncome() {
        return sickLeaveIncome;
    }

    public void setSickLeaveIncome(long sickLeaveIncome) {
        this.sickLeaveIncome = sickLeaveIncome;
    }

    public long getGrossIncome() {
        return grossIncome;
    }

    public void setGrossIncome(long grossIncome) {
        this.grossIncome = grossIncome;
    }

    public long getTotalIncome() {
        return totalIncome;
    }

    public void setTotalIncome(long totalIncome) {
        this.totalIncome = totalIncome;
    }
}
