package model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class LeaveRequest {
    private int id;
    private int requestId;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate leaveDate;
    private String leaveType; // ON_LEAVE hoặc LEAVE
    private LocalDateTime createdAt;

    public LeaveRequest() {
    }

    public LeaveRequest(int requestId, LocalDate leaveDate) {
        this.requestId = requestId;
        this.leaveDate = leaveDate;
        this.startDate = leaveDate;
        this.endDate = leaveDate;
    }

    public LeaveRequest(int requestId, LocalDate startDate, LocalDate endDate, String leaveType) {
        this.requestId = requestId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.leaveDate = startDate;
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

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getFormattedDateRange() {
        if (startDate != null && endDate != null && !startDate.equals(endDate)) {
            return startDate + " to " + endDate;
        }
        return getLeaveDate() != null ? getLeaveDate().toString() : "";
    }

    public LocalDate getLeaveDate() {
        return leaveDate != null ? leaveDate : startDate;
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