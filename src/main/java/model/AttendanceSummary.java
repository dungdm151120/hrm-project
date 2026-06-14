package model;

public class AttendanceSummary {
    private double totalWorkHours;
    private double expectedWorkHours;
    private int lateCount;
    private int earlyLeaveCount;
    private int forgotCheckCount;
    private double totalLateAndEarlyHours;
    private double remainingLeaveDays;
    private double entitledLeaveDays;
    private double advancedLeaveDays;
    private double overtimeHours;
    private double leaveDaysInMonth;

    public double getTotalWorkHours() {
        return totalWorkHours;
    }

    public void setTotalWorkHours(double totalWorkHours) {
        this.totalWorkHours = totalWorkHours;
    }

    public double getExpectedWorkHours() {
        return expectedWorkHours;
    }

    public void setExpectedWorkHours(double expectedWorkHours) {
        this.expectedWorkHours = expectedWorkHours;
    }

    public int getLateCount() {
        return lateCount;
    }

    public void setLateCount(int lateCount) {
        this.lateCount = lateCount;
    }

    public int getEarlyLeaveCount() {
        return earlyLeaveCount;
    }

    public void setEarlyLeaveCount(int earlyLeaveCount) {
        this.earlyLeaveCount = earlyLeaveCount;
    }

    public int getForgotCheckCount() {
        return forgotCheckCount;
    }

    public void setForgotCheckCount(int forgotCheckCount) {
        this.forgotCheckCount = forgotCheckCount;
    }

    public double getTotalLateAndEarlyHours() {
        return totalLateAndEarlyHours;
    }

    public void setTotalLateAndEarlyHours(double totalLateAndEarlyHours) {
        this.totalLateAndEarlyHours = totalLateAndEarlyHours;
    }

    public double getRemainingLeaveDays() {
        return remainingLeaveDays;
    }

    public void setRemainingLeaveDays(double remainingLeaveDays) {
        this.remainingLeaveDays = remainingLeaveDays;
    }

    public double getEntitledLeaveDays() {
        return entitledLeaveDays;
    }

    public void setEntitledLeaveDays(double entitledLeaveDays) {
        this.entitledLeaveDays = entitledLeaveDays;
    }

    public double getAdvancedLeaveDays() {
        return advancedLeaveDays;
    }

    public void setAdvancedLeaveDays(double advancedLeaveDays) {
        this.advancedLeaveDays = advancedLeaveDays;
    }

    public double getOvertimeHours() {
        return overtimeHours;
    }

    public void setOvertimeHours(double overtimeHours) {
        this.overtimeHours = overtimeHours;
    }

    public double getLeaveDaysInMonth() {
        return leaveDaysInMonth;
    }

    public void setLeaveDaysInMonth(double leaveDaysInMonth) {
        this.leaveDaysInMonth = leaveDaysInMonth;
    }
}
