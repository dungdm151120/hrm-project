package model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Dependent {
    private int id;
    private int userId;
    private String dependentName;
    private LocalDate dependentDob;
    private String dependentIdNumber;
    private String relationship;
    private String status;
    private LocalDate effectiveDate;
    private LocalDateTime createdAt;

    public Dependent() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getDependentName() {
        return dependentName;
    }

    public void setDependentName(String dependentName) {
        this.dependentName = dependentName;
    }

    public LocalDate getDependentDob() {
        return dependentDob;
    }

    public void setDependentDob(LocalDate dependentDob) {
        this.dependentDob = dependentDob;
    }

    public String getDependentIdNumber() {
        return dependentIdNumber;
    }

    public void setDependentIdNumber(String dependentIdNumber) {
        this.dependentIdNumber = dependentIdNumber;
    }

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(LocalDate effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
