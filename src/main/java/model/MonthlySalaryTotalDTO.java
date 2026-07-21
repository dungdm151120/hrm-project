package model;

public class MonthlySalaryTotalDTO {
    private int month;
    private int year;
    private long totalNetPay;

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public long getTotalNetPay() {
        return totalNetPay;
    }

    public void setTotalNetPay(long totalNetPay) {
        this.totalNetPay = totalNetPay;
    }
}
