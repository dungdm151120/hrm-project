package model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class SickLeaveRequest {
    private int id;
    private int requestId;
    private LocalDate startDate;
    private LocalDate endDate;
    private String filePath;
    private LocalDateTime createdAt;

    public SickLeaveRequest() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getRequestId() { return requestId; }
    public void setRequestId(int requestId) { this.requestId = requestId; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getFormattedDateRange() {
        if (startDate != null && endDate != null && !startDate.equals(endDate)) {
            return startDate + " to " + endDate;
        }
        return startDate != null ? startDate.toString() : "";
    }
}