package model;

public class AttendanceConfirmedSummary {
    private double totalWorkHours;
    private double expectedWorkHours;
    private double overtimeHours;
    private String status;

    public AttendanceConfirmedSummary() {
    }

    public AttendanceConfirmedSummary(double overtimeHours, double expectedWorkHours, double totalWorkHours, String status) {
        this.overtimeHours = overtimeHours;
        this.expectedWorkHours = expectedWorkHours;
        this.totalWorkHours = totalWorkHours;
        this.status = status;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
