package model;

import java.time.LocalDate;

public class SickLeaveDate {
    private int id;
    private int sickLeaveRequestId;
    private LocalDate leaveDate;

    public SickLeaveDate() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getSickLeaveRequestId() { return sickLeaveRequestId; }
    public void setSickLeaveRequestId(int sickLeaveRequestId) { this.sickLeaveRequestId = sickLeaveRequestId; }
    public LocalDate getLeaveDate() { return leaveDate; }
    public void setLeaveDate(LocalDate leaveDate) { this.leaveDate = leaveDate; }
}