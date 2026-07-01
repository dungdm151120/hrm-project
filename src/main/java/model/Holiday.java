package model;

import java.time.LocalDate;

public class Holiday {
    private LocalDate holidayDate;
    private String holidayName;

    public Holiday() {}

    public Holiday(LocalDate holidayDate, String holidayName) {
        this.holidayDate = holidayDate;
        this.holidayName = holidayName;
    }

    public LocalDate getHolidayDate() {
        return holidayDate;
    }

    public void setHolidayDate(LocalDate holidayDate) {
        this.holidayDate = holidayDate;
    }

    public String getHolidayName() {
        return holidayName;
    }

    public void setHolidayName(String holidayName) {
        this.holidayName = holidayName;
    }

    @Override
    public String toString() {
        return holidayName + " (" + holidayDate + ")";
    }
}