package model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class AttendanceRecord {
    private int id;
    private int userId;
    private LocalDate workDate;
    private LocalDateTime checkIn;
    private LocalDateTime checkOut;
    private Double totalWorkHours;
    private Double overtimeHours;
    private Double lateHours;
    private Double earlyLeaveHours;
    private String status;
    private String note;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public AttendanceRecord() {}

    public AttendanceRecord(int id, int userId, LocalDate workDate,
                            LocalDateTime checkIn, LocalDateTime checkOut,
                            Double totalWorkHours, Double overtimeHours,
                            Double lateHours, Double earlyLeaveHours,
                            String status, String note,
                            LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.workDate = workDate;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.totalWorkHours = totalWorkHours;
        this.overtimeHours = overtimeHours;
        this.lateHours = lateHours;
        this.earlyLeaveHours = earlyLeaveHours;
        this.status = status;
        this.note = note;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public LocalDate getWorkDate() { return workDate; }
    public void setWorkDate(LocalDate workDate) { this.workDate = workDate; }

    public LocalDateTime getCheckIn() { return checkIn; }
    public void setCheckIn(LocalDateTime checkIn) { this.checkIn = checkIn; }

    public LocalDateTime getCheckOut() { return checkOut; }
    public void setCheckOut(LocalDateTime checkOut) { this.checkOut = checkOut; }

    public Double getTotalWorkHours() { return totalWorkHours; }
    public void setTotalWorkHours(Double totalWorkHours) { this.totalWorkHours = totalWorkHours; }

    public Double getOvertimeHours() { return overtimeHours; }
    public void setOvertimeHours(Double overtimeHours) { this.overtimeHours = overtimeHours; }

    public Double getLateHours() { return lateHours; }
    public void setLateHours(Double lateHours) { this.lateHours = lateHours; }

    public Double getEarlyLeaveHours() { return earlyLeaveHours; }
    public void setEarlyLeaveHours(Double earlyLeaveHours) { this.earlyLeaveHours = earlyLeaveHours; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}