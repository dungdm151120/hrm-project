package model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class AttendanceRecordDTO {
    private int attendanceRecordId;
    private int userId;
    private String employeeCode;
    private String employeeName;
    private String departmentName;
    private LocalDate workDate;
    private LocalDateTime checkIn;
    private LocalDateTime checkOut;
    private Double totalWorkHours;
    private Double overtimeHours;
    private Double lateHours;
    private Double earlyLeaveHours;
    private String status;
    private String note;
    private String checkInText;
    private String checkOutText;
    private String cssClass;
    private boolean edited;
    private String otStatus;
    private String positionName;
    private boolean holiday; // <-- Thêm thuộc tính này

    // Getter & Setter cho holiday
    public boolean isHoliday() {
        return holiday;
    }

    public void setHoliday(boolean holiday) {
        this.holiday = holiday;
    }

    // Các getter/setter khác giữ nguyên
    public String getOtStatus() {
        return otStatus;
    }

    public void setOtStatus(String otStatus) {
        this.otStatus = otStatus;
    }

    public String getPositionName() {
        return positionName;
    }

    public void setPositionName(String positionName) {
        this.positionName = positionName;
    }

    public int getAttendanceRecordId() {
        return attendanceRecordId;
    }

    public void setAttendanceRecordId(int attendanceRecordId) {
        this.attendanceRecordId = attendanceRecordId;
    }

    public int getId() {
        return attendanceRecordId;
    }

    public void setId(int id) {
        this.attendanceRecordId = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getEmployeeCode() {
        return employeeCode;
    }

    public void setEmployeeCode(String employeeCode) {
        this.employeeCode = employeeCode;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public LocalDate getWorkDate() {
        return workDate;
    }

    public void setWorkDate(LocalDate workDate) {
        this.workDate = workDate;
    }

    public LocalDateTime getCheckIn() {
        return checkIn;
    }

    public void setCheckIn(LocalDateTime checkIn) {
        this.checkIn = checkIn;
    }

    public LocalDateTime getCheckOut() {
        return checkOut;
    }

    public void setCheckOut(LocalDateTime checkOut) {
        this.checkOut = checkOut;
    }

    public Double getTotalWorkHours() {
        return totalWorkHours;
    }

    public void setTotalWorkHours(Double totalWorkHours) {
        this.totalWorkHours = totalWorkHours;
    }

    public Double getOvertimeHours() {
        return overtimeHours;
    }

    public void setOvertimeHours(Double overtimeHours) {
        this.overtimeHours = overtimeHours;
    }

    public Double getLateHours() {
        return lateHours;
    }

    public void setLateHours(Double lateHours) {
        this.lateHours = lateHours;
    }

    public Double getEarlyLeaveHours() {
        return earlyLeaveHours;
    }

    public void setEarlyLeaveHours(Double earlyLeaveHours) {
        this.earlyLeaveHours = earlyLeaveHours;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getCheckInText() {
        return checkInText;
    }

    public void setCheckInText(String checkInText) {
        this.checkInText = checkInText;
    }

    public String getCheckOutText() {
        return checkOutText;
    }

    public void setCheckOutText(String checkOutText) {
        this.checkOutText = checkOutText;
    }

    public String getCssClass() {
        return cssClass;
    }

    public void setCssClass(String cssClass) {
        this.cssClass = cssClass;
    }

    public boolean isEdited() {
        return edited;
    }

    public void setEdited(boolean edited) {
        this.edited = edited;
    }
}