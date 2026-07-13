package model;

import java.time.LocalDate;

public class PitBracketVersion {
    private int id;
    private String versionName;
    private LocalDate effectiveDate;
    private LocalDate createdAt;

    public PitBracketVersion() {
    }

    public PitBracketVersion(LocalDate createdAt, LocalDate effectiveDate, String versionName, int id) {
        this.createdAt = createdAt;
        this.effectiveDate = effectiveDate;
        this.versionName = versionName;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getVersionName() {
        return versionName;
    }

    public LocalDate getEffectiveDate() {
        return effectiveDate;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public void setEffectiveDate(LocalDate effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }
}
