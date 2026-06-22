package model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class OvertimeDetail {
    private int userId;
    private String userFullName;
    private String employeeCode;
    private LocalDate overtimeDate;
    private LocalTime shiftStart;
    private LocalTime shiftEnd;
    private String reason;
    private String requestStatus;
    private Double hoursActual;
    private List<OvertimeParticipant> participants;

    // Getters and Setters
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserFullName() {
        return userFullName;
    }

    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }

    public String getEmployeeCode() {
        return employeeCode;
    }

    public void setEmployeeCode(String employeeCode) {
        this.employeeCode = employeeCode;
    }

    public LocalDate getOvertimeDate() {
        return overtimeDate;
    }

    public void setOvertimeDate(LocalDate overtimeDate) {
        this.overtimeDate = overtimeDate;
    }

    public LocalTime getShiftStart() {
        return shiftStart;
    }

    public void setShiftStart(LocalTime shiftStart) {
        this.shiftStart = shiftStart;
    }

    public LocalTime getShiftEnd() {
        return shiftEnd;
    }

    public void setShiftEnd(LocalTime shiftEnd) {
        this.shiftEnd = shiftEnd;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(String requestStatus) {
        this.requestStatus = requestStatus;
    }

    public Double getHoursActual() {
        return hoursActual;
    }

    public void setHoursActual(Double hoursActual) {
        this.hoursActual = hoursActual;
    }

    public List<OvertimeParticipant> getParticipants() {
        return participants;
    }

    public void setParticipants(List<OvertimeParticipant> participants) {
        this.participants = participants;
    }
}
