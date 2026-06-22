package model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class LeaveRequest {
    private int id;
    private int requestId;
    private LocalDate leaveDate;
    private String leaveType; // ON_LEAVE hoặc LEAVE
    private LocalDateTime createdAt;

    public LeaveRequest() {
    }

    public LeaveRequest(int requestId, LocalDate leaveDate) {
        this.requestId = requestId;
        this.leaveDate = leaveDate;
    }

    public LeaveRequest(int requestId, LocalDate leaveDate, String leaveType) {
        this.requestId = requestId;
        this.leaveDate = leaveDate;
        this.leaveType = leaveType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public LocalDate getLeaveDate() {
        return leaveDate;
    }

    public void setLeaveDate(LocalDate leaveDate) {
        this.leaveDate = leaveDate;
    }

    public String getLeaveType() {
        return leaveType;
    }

    public void setLeaveType(String leaveType) {
        this.leaveType = leaveType;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "LeaveRequest{" +
                "id=" + id +
                ", requestId=" + requestId +
                ", leaveDate=" + leaveDate +
                ", leaveType='" + leaveType + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}