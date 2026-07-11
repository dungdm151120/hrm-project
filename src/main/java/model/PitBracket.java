package model;

import java.time.LocalDate;

public class PitBracket {
    private int id;
    private int bracketLevel;
    private long minValue;
    private Long maxValue;
    private double taxRate;
    private LocalDate effectiveDate;

    public PitBracket() {
    }

    public PitBracket(int id, int bracketLevel, long minValue, Long maxValue, double taxRate, LocalDate effectiveDate) {
        this.id = id;
        this.bracketLevel = bracketLevel;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.taxRate = taxRate;
        this.effectiveDate = effectiveDate;
    }

    public int getId() {
        return id;
    }

    public int getBracketLevel() {
        return bracketLevel;
    }

    public long getMinValue() {
        return minValue;
    }

    public Long getMaxValue() {
        return maxValue;
    }

    public double getTaxRate() {
        return taxRate;
    }

    public LocalDate getEffectiveDate() {
        return effectiveDate;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setBracketLevel(int bracketLevel) {
        this.bracketLevel = bracketLevel;
    }

    public void setMinValue(long minValue) {
        this.minValue = minValue;
    }

    public void setMaxValue(Long maxValue) {
        this.maxValue = maxValue;
    }

    public void setTaxRate(double taxRate) {
        this.taxRate = taxRate;
    }

    public void setEffectiveDate(LocalDate effectiveDate) {
        this.effectiveDate = effectiveDate;
    }
}
