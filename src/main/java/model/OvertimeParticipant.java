package model;

import java.time.LocalDateTime;

public class OvertimeParticipant {
    private int id;
    private int overtimeRequestId;
    private int userId;
    private String status;
    private double hoursActual;
    private LocalDateTime confirmedAt;
    private LocalDateTime createdAt;

    // Transient fields for display
    private String userFullName;
    private String employeeCode;
    private String positionName;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getOvertimeRequestId() { return overtimeRequestId; }
    public void setOvertimeRequestId(int overtimeRequestId) { this.overtimeRequestId = overtimeRequestId; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public double getHoursActual() { return hoursActual; }
    public void setHoursActual(double hoursActual) { this.hoursActual = hoursActual; }
    public LocalDateTime getConfirmedAt() { return confirmedAt; }
    public void setConfirmedAt(LocalDateTime confirmedAt) { this.confirmedAt = confirmedAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getUserFullName() { return userFullName; }
    public void setUserFullName(String userFullName) { this.userFullName = userFullName; }
    public String getEmployeeCode() { return employeeCode; }
    public void setEmployeeCode(String employeeCode) { this.employeeCode = employeeCode; }
    public String getPositionName() { return positionName; }
    public void setPositionName(String positionName) { this.positionName = positionName; }
}
