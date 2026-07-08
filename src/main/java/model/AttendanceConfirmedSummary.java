package model;

public class AttendanceConfirmedSummary {
    private double totalWorkHours;
    private double expectedWorkHours;
    private double overtimeHours;

    public AttendanceConfirmedSummary() {
    }

    public AttendanceConfirmedSummary(double overtimeHours, double expectedWorkHours, double totalWorkHours) {
        this.overtimeHours = overtimeHours;
        this.expectedWorkHours = expectedWorkHours;
        this.totalWorkHours = totalWorkHours;
    }

    public double getTotalWorkHours() {
        return totalWorkHours;
    }

    public double getExpectedWorkHours() {
        return expectedWorkHours;
    }

    public double getOvertimeHours() {
        return overtimeHours;
    }

    public void setTotalWorkHours(double totalWorkHours) {
        this.totalWorkHours = totalWorkHours;
    }

    public void setExpectedWorkHours(double expectedWorkHours) {
        this.expectedWorkHours = expectedWorkHours;
    }

    public void setOvertimeHours(double overtimeHours) {
        this.overtimeHours = overtimeHours;
    }
}
