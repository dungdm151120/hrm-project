package model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class LeaveRequest {
    private int id;
    private int requestId;
    private LocalDate leaveDate;
    private LocalDateTime createdAt;

    public LeaveRequest() {
    }

    public LeaveRequest(int requestId, LocalDate leaveDate) {
        this.requestId = requestId;
        this.leaveDate = leaveDate;
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
                ", createdAt=" + createdAt +
                '}';
    }
}