package model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class AttendanceLog {
    private int id;
    private LocalDate workDate;
    private int employeeId;
    private LocalDateTime checkIn;
    private LocalDateTime checkOut;

    public AttendanceLog() {}

    public AttendanceLog(int id, LocalDate workDate, int employeeId,
                         LocalDateTime checkIn, LocalDateTime checkOut) {
        this.id = id;
        this.workDate = workDate;
        this.employeeId = employeeId;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public LocalDate getWorkDate() { return workDate; }
    public void setWorkDate(LocalDate workDate) { this.workDate = workDate; }

    public int getEmployeeId() { return employeeId; }
    public void setEmployeeId(int employeeId) { this.employeeId = employeeId; }

    public LocalDateTime getCheckIn() { return checkIn; }
    public void setCheckIn(LocalDateTime checkIn) { this.checkIn = checkIn; }

    public LocalDateTime getCheckOut() { return checkOut; }
    public void setCheckOut(LocalDateTime checkOut) { this.checkOut = checkOut; }
}