package model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

public class AttendanceChangeRequest {
    private int id;
    private int requestId;
    private LocalDate workDate;
    private LocalTime desiredCheckIn;
    private LocalTime desiredCheckOut;
    private String reason;
    private LocalDateTime createdAt;
    private boolean isApplied;

    public AttendanceChangeRequest() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getRequestId() { return requestId; }
    public void setRequestId(int requestId) { this.requestId = requestId; }
    public LocalDate getWorkDate() { return workDate; }
    public void setWorkDate(LocalDate workDate) { this.workDate = workDate; }
    public LocalTime getDesiredCheckIn() { return desiredCheckIn; }
    public void setDesiredCheckIn(LocalTime desiredCheckIn) { this.desiredCheckIn = desiredCheckIn; }
    public LocalTime getDesiredCheckOut() { return desiredCheckOut; }
    public void setDesiredCheckOut(LocalTime desiredCheckOut) { this.desiredCheckOut = desiredCheckOut; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public boolean isApplied() { return isApplied; }
    public void setApplied(boolean isApplied) { this.isApplied = isApplied; }
}