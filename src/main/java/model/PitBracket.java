package model;

import java.time.LocalDate;

public class PitBracket {
    private int id;
    private int versionId;
    private int bracketLevel;
    private long minValue;
    private Long maxValue;
    private double taxRate;

    public PitBracket() {
    }

    public PitBracket(int id, int versionId, int bracketLevel, long minValue, Long maxValue, double taxRate) {
        this.id = id;
        this.versionId = versionId;
        this.bracketLevel = bracketLevel;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.taxRate = taxRate;
    }

    public int getId() {
        return id;
    }

    public int getVersionId() {
        return versionId;
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

    public void setId(int id) {
        this.id = id;
    }

    public void setVersionId(int versionId) {
        this.versionId = versionId;
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

}
