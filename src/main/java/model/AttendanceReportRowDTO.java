package model;

public class AttendanceReportRowDTO {
    private int employeeId;
    private String employeeCode;
    private String employeeName;
    private String positionName;
    private String departmentName;
    private int expectedWorkdays;
    private int presentDays;
    private int absentDays;
    private int lateDays;
    private int earlyLeaveDays;
    private int forgotCheckInDays;
    private int forgotCheckOutDays;
    private double totalWorkHours;
    private double totalOvertimeHours;
    private double registeredOvertimeHours;
    private double leaveDays;

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public String getEmployeeCode() {
        return employeeCode;
    }

    public void setEmployeeCode(String employeeCode) {
        this.employeeCode = employeeCode;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getPositionName() {
        return positionName;
    }

    public void setPositionName(String positionName) {
        this.positionName = positionName;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public int getExpectedWorkdays() {
        return expectedWorkdays;
    }

    public void setExpectedWorkdays(int expectedWorkdays) {
        this.expectedWorkdays = expectedWorkdays;
    }

    public int getPresentDays() {
        return presentDays;
    }

    public void setPresentDays(int presentDays) {
        this.presentDays = presentDays;
    }

    public int getAbsentDays() {
        return absentDays;
    }

    public void setAbsentDays(int absentDays) {
        this.absentDays = absentDays;
    }

    public int getLateDays() {
        return lateDays;
    }

    public void setLateDays(int lateDays) {
        this.lateDays = lateDays;
    }

    public int getEarlyLeaveDays() {
        return earlyLeaveDays;
    }

    public void setEarlyLeaveDays(int earlyLeaveDays) {
        this.earlyLeaveDays = earlyLeaveDays;
    }

    public int getForgotCheckInDays() {
        return forgotCheckInDays;
    }

    public void setForgotCheckInDays(int forgotCheckInDays) {
        this.forgotCheckInDays = forgotCheckInDays;
    }

    public int getForgotCheckOutDays() {
        return forgotCheckOutDays;
    }

    public void setForgotCheckOutDays(int forgotCheckOutDays) {
        this.forgotCheckOutDays = forgotCheckOutDays;
    }

    public double getTotalWorkHours() {
        return totalWorkHours;
    }

    public void setTotalWorkHours(double totalWorkHours) {
        this.totalWorkHours = totalWorkHours;
    }

    public double getTotalOvertimeHours() {
        return totalOvertimeHours;
    }

    public void setTotalOvertimeHours(double totalOvertimeHours) {
        this.totalOvertimeHours = totalOvertimeHours;
    }

    public double getRegisteredOvertimeHours() {
        return registeredOvertimeHours;
    }

    public void setRegisteredOvertimeHours(double registeredOvertimeHours) {
        this.registeredOvertimeHours = registeredOvertimeHours;
    }

    public double getLeaveDays() {
        return leaveDays;
    }

    public void setLeaveDays(double leaveDays) {
        this.leaveDays = leaveDays;
    }
}
